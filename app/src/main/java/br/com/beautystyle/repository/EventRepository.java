package br.com.beautystyle.repository;

import static android.content.ContentValues.TAG;
import static br.com.beautystyle.repository.ConstantsRepository.FREE_ACCOUNT;
import static br.com.beautystyle.repository.ConstantsRepository.PROFILE_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.USER_PREMIUM;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.database.rxjava.EventRxJava;
import br.com.beautystyle.model.entity.BlockTime;
import br.com.beautystyle.model.entity.Customer;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.EventJobCrossRef;
import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.retrofit.model.dto.EventWithClientAndJobsDto;
import br.com.beautystyle.retrofit.model.form.EventForm;
import br.com.beautystyle.retrofit.webclient.EventWebClient;

public class EventRepository {

    @Inject
    EventWebClient webClient;
    @Inject
    EventRxJava dao;
    @Inject
    EventWithJobRepository eventWithJobRepository;
    @Inject
    ClientRepository costumerRepository;
    @Inject
    JobRepository jobRepository;
    @Inject
    BlockTimeRepository blockTimeRepository;
    private final String profile;
    private final Long tenant;

    @Inject
    public EventRepository(SharedPreferences preferences) {
        profile = preferences.getString(PROFILE_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public LiveData<Resource<EventWithClientAndJobsDto>> getByDateLiveData() {
        MutableLiveData<Resource<EventWithClientAndJobsDto>> mutableEvents = new MutableLiveData<>();
        getByDateFromRoom(mutableEvents);
        if (isUserPremium()) {
            getByDateFromApi(mutableEvents);
        }
        return mutableEvents;
    }

    private void getByDateFromRoom(MutableLiveData<Resource<EventWithClientAndJobsDto>> mutableEvents) {
        dao.getByDate(tenant).doOnSuccess(eventsFromRoom -> {
            blockTimeRepository.getAllByDate()
                    .doOnSuccess(blockTimes -> {
                        EventWithClientAndJobsDto events = new EventWithClientAndJobsDto(
                                eventsFromRoom, blockTimes
                        );
                        Resource<EventWithClientAndJobsDto> resource =
                                new Resource<>(events, null);
                        mutableEvents.setValue(resource);
                    }).subscribe();
        }).subscribe();
    }

    private void getByDateFromApi(MutableLiveData<Resource<EventWithClientAndJobsDto>> mutableEvents) {
        webClient.getAllByDate(new ResultsCallBack<EventWithClientAndJobsDto>() {
            @Override
            public void onSuccess(EventWithClientAndJobsDto eventsFromApi) {
                updateLocalDatabase(eventsFromApi, mutableEvents);
            }

            @Override
            public void onError(String error) {
                mutableEvents.setValue(new Resource<>(null, error));
            }
        });
    }

    private void updateLocalDatabase(EventWithClientAndJobsDto eventsFromApiDto,
                                     MutableLiveData<Resource<EventWithClientAndJobsDto>> mutableEvents) {
        //update costumers -> update jobs -> update events
        List<EventWithClientAndJobs> eventsFromApi = eventsFromApiDto.getEvents();
        updateCostumersAndJobsAndEvents(eventsFromApi,
                new ResultsCallBack<List<EventWithClientAndJobs>>() {
                    @Override
                    public void onSuccess(List<EventWithClientAndJobs> events) {
                        eventsFromApiDto.setEvents(events);
                        updateBlockTimes(eventsFromApiDto, mutableEvents);
                    }

                    @Override
                    public void onError(String error) {
                        mutableEvents.setValue(new Resource<>(null, error));
                    }
                });
    }

    private void updateBlockTimes(EventWithClientAndJobsDto eventsFromApiDto,
                                  MutableLiveData<Resource<EventWithClientAndJobsDto>> mutableEvents) {

        blockTimeRepository.updateAll(eventsFromApiDto.getBlockTimes(),
                new ResultsCallBack<List<BlockTime>>() {
                    @Override
                    public void onSuccess(List<BlockTime> blockTimes) {
                        eventsFromApiDto.setBlockTimes(blockTimes);
                        Resource<EventWithClientAndJobsDto> resource =
                                new Resource<>(eventsFromApiDto, null);
                        mutableEvents.setValue(resource);
                    }

                    @Override
                    public void onError(String error) {
                        mutableEvents.setValue(new Resource<>(null, error));
                    }
                });
    }


    private void updateAllOnRoom(List<EventWithClientAndJobs> eventsFromApi,
                                 ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        List<Event> eventsToUpdate = getEventsToUpdate(eventsFromApi);
        dao.updateAll(eventsToUpdate)
                .doOnComplete(() -> insertAllOnRoom(eventsFromApi, callBack))
                .subscribe();
    }

    private void insertAllOnRoom(List<EventWithClientAndJobs> eventsFromApi,
                                 ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        List<Event> newEvents = getEventsToInsert(eventsFromApi);
        newEvents.forEach(event -> event.setId(null));
        dao.insertAll(newEvents)
                .doOnSuccess(ids -> {
                            setEventIds(newEvents, ids);
                            mergeEventId(eventsFromApi, newEvents);
                            eventWithJobRepository.updateAll(eventsFromApi, callBack);
                        }
                ).subscribe();
    }

    private void deleteFromRoomIfNotExistOnApi(List<EventWithClientAndJobs> eventsFromApi,
                                               List<EventWithClientAndJobs> eventsFromRoom) {
        eventsFromRoom.forEach(fromRoom -> {
            if (isNotExistOnApi(eventsFromApi, fromRoom))
                dao.delete(fromRoom.getEvent()).subscribe();
        });
    }

    private boolean isNotExistOnApi(List<EventWithClientAndJobs> eventsFromApi,
                                    EventWithClientAndJobs fromRoom) {
        return eventsFromApi.stream()
                .map(EventWithClientAndJobs::getEvent)
                .noneMatch(eventFromApi -> fromRoom.getEvent().isApiIdEquals(eventFromApi.getApiId()));
    }

    private void mergeEventId(List<EventWithClientAndJobs> eventsFromApi,
                              List<Event> newEvents) {
        eventsFromApi.stream().map(EventWithClientAndJobs::getEvent)
                .forEach(eventFromApi -> newEvents.forEach(newEvent -> {
                    if (newEvent.isApiIdEquals(eventFromApi.getApiId()))
                        eventFromApi.setId(newEvent.getId());
                }));
    }

    private void setEventIds(List<Event> eventsToInsert, List<Long> ids) {
        for (int i = 0; i < eventsToInsert.size(); i++) {
            eventsToInsert.get(i).setId(ids.get(i));
        }
    }

    @NonNull
    private List<Event> getEventsToInsert(List<EventWithClientAndJobs> eventsFromApi) {
        return eventsFromApi.stream()
                .map(EventWithClientAndJobs::getEvent)
                .filter(event -> !event.isEventIdNotNull())
                .collect(Collectors.toList());
    }

    @NonNull
    private List<Event> getEventsToUpdate(List<EventWithClientAndJobs> eventsFromApi) {
        return eventsFromApi.stream()
                .map(EventWithClientAndJobs::getEvent)
                .filter(Event::isEventIdNotNull)
                .collect(Collectors.toList());
    }

    private void setEventIdToUpdate(List<EventWithClientAndJobs> eventsFromRoom,
                                    List<EventWithClientAndJobs> eventsFromApi) {
        eventsFromRoom.forEach(eventFromRoom ->
                eventsFromApi.forEach(eventFromApi -> {
                    if (eventFromRoom.isApiIdEquals(eventFromApi)) {
                        setEventIds(eventFromApi.getEvent(), eventFromRoom.getEvent());
                    }
                })
        );
    }

    private void setEventIds(Event eventFromApi, Event eventFromRoom) {
        eventFromApi.setId(eventFromRoom.getId());
    }

    public LiveData<Resource<EventWithClientAndJobs>> insert(EventWithClientAndJobs event) {
        MutableLiveData<Resource<EventWithClientAndJobs>> liveData = new MutableLiveData<>();
        if (isUserPremium()) {
            insertOnApi(event, liveData);
        }
        if (isFreeAccount()) {
            event.getEvent().setTenant(tenant);
            insertOnRoom(event, liveData);
        }
        return liveData;
    }


    private void insertOnApi(EventWithClientAndJobs event,
                             MutableLiveData<Resource<EventWithClientAndJobs>> liveData) {
        EventForm eventForm = new EventForm(event.getEvent(),
                event.getCustomer().getApiId(),
                event.getJobs());
        webClient.insert(eventForm, new ResultsCallBack<EventWithClientAndJobs>() {
            @Override
            public void onSuccess(EventWithClientAndJobs result) {
                event.getEvent().setApiId(result.getEvent().getApiId());
                insertOnRoom(event, liveData);
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public void insertOnRoom(EventWithClientAndJobs eventWithClientAndJobs,
                             MutableLiveData<Resource<EventWithClientAndJobs>> liveData) {
        Event event = eventWithClientAndJobs.getEvent();
        dao.insert(event)
                .doOnSuccess(id -> {
                    eventWithClientAndJobs.getEvent().setId(id);
                    insertEventWithJobsCrossRef(eventWithClientAndJobs, liveData);
                })
                .subscribe();
    }

    private void insertEventWithJobsCrossRef(EventWithClientAndJobs eventWithClientAndJobs,
                                             MutableLiveData<Resource<EventWithClientAndJobs>> liveData) {
        List<EventJobCrossRef> eventJobCrossRefs =
                new ArrayList<>(getEventJobsCrossRefs(eventWithClientAndJobs));
        eventWithJobRepository.insert(eventJobCrossRefs).doOnComplete(() ->
                liveData.setValue(new Resource<>(eventWithClientAndJobs, null))
        ).subscribe();
    }

    private List<EventJobCrossRef> getEventJobsCrossRefs(EventWithClientAndJobs event) {
        return event.getJobs().stream()
                .map(job -> new EventJobCrossRef(event.getEvent().getId(), job.getId()))
                .collect(Collectors.toList());
    }

    public LiveData<Resource<Void>> update(EventWithClientAndJobs event) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        if (isUserPremium()) {
            updateOnApi(event, liveData);
        }
        if (isFreeAccount()) {
            updateOnRoom(event, liveData);
        }
        return liveData;
    }

    private void updateOnApi(EventWithClientAndJobs event, MutableLiveData<Resource<Void>> liveData) {
        EventForm eventForm = new EventForm(event.getEvent(),
                event.getCustomer().getApiId(),
                event.getJobs());
        webClient.update(eventForm, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                updateOnRoom(event, liveData);
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public void updateOnRoom(EventWithClientAndJobs eventWithClientAndJobs,
                             MutableLiveData<Resource<Void>> liveData) {
        dao.update(eventWithClientAndJobs.getEvent())
                .doOnComplete(() ->
                        eventWithJobRepository.update(
                                eventWithClientAndJobs, liveData)
                ).subscribe();
    }

    public LiveData<Resource<Void>> delete(Event event) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        if (isUserPremium()) {
            deleteOnApi(event, liveData);
        }
        if (isFreeAccount()) {
            deleteOnRoom(event, liveData);
        }
        return liveData;
    }

    private void deleteOnApi(Event event, MutableLiveData<Resource<Void>> liveData) {
        webClient.delete(event.getApiId(), new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                deleteOnRoom(event, liveData);
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public void deleteOnRoom(Event event, MutableLiveData<Resource<Void>> liveData) {
        dao.delete(event).doOnComplete(() ->
                        liveData.setValue(new Resource<>(null, null)))
                .subscribe();
    }

    public void updateCostumersAndJobsAndEvents(List<EventWithClientAndJobs> events,
                                                ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        List<Customer> customers = getClients(events);
        costumerRepository.updateAndInsertAll(customers,
                new ResultsCallBack<List<Customer>>() {
                    @Override
                    public void onSuccess(List<Customer> customers) {
                        setCostumerIdOnEvents(customers, events);
                        updateJobs(events, callBack);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }

                });
    }

    @NonNull
    private List<Customer> getClients(List<EventWithClientAndJobs> events) {
        return events.stream()
                .map(EventWithClientAndJobs::getCustomer)
                .distinct()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void setCostumerIdOnEvents(List<Customer> customers, List<EventWithClientAndJobs> events) {
        events.forEach(event ->
                customers.forEach(client -> {
                    try {
                        if (event.getCustomer().getApiId().equals(client.getApiId())) {
                            event.getCustomer().setId(client.getId());
                            event.getEvent().setCustomerCreatorId(client.getId());
                        }
                    } catch (Exception error) {
                        Log.i(TAG, "eventApiId Null: " + error);
                    }

                })
        );
    }

    private void updateJobs(List<EventWithClientAndJobs> events,
                            ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        List<Job> jobsFromApi = getJobsFromApi(events);
        jobRepository.updateJobs(jobsFromApi,
                new ResultsCallBack<List<Job>>() {
                    @Override
                    public void onSuccess(List<Job> jobs) {
                        setJobIdsOnEvents(jobs, events);
                        updateEvents(events, callBack);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                });
    }

    private List<Job> getJobsFromApi(List<EventWithClientAndJobs> eventListFromApi) {
        return eventListFromApi.stream()
                .map(EventWithClientAndJobs::getJobs)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private void setJobIdsOnEvents(List<Job> jobs, List<EventWithClientAndJobs> events) {
        events.forEach(event ->
                event.getJobs().forEach(jobFromApi ->
                        jobs.forEach(job -> {
                            if (job.isApiIdEquals(jobFromApi)) {
                                jobFromApi.setId(job.getId());
                            }
                        })
                )
        );
    }

    private void updateEvents(List<EventWithClientAndJobs> eventsFromApi,
                              ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        dao.getByDate(tenant)
                .doOnSuccess(eventsFromRoom -> {
                            deleteFromRoomIfNotExistOnApi(eventsFromApi, eventsFromRoom);
                            setEventIdToUpdate(eventsFromRoom, eventsFromApi);
                            updateAllOnRoom(eventsFromApi, callBack);
                        }
                ).subscribe();
    }

    private boolean isFreeAccount() {
        return profile.equals(FREE_ACCOUNT);
    }

    private boolean isUserPremium() {
        return profile.equals(USER_PREMIUM);
    }
}