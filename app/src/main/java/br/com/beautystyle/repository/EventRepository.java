package br.com.beautystyle.repository;

import static android.content.ContentValues.TAG;
import static br.com.beautystyle.repository.ConstantsRepository.PROFILE_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.database.rxjavaassinc.EventAsynchDao;
import br.com.beautystyle.model.Report;
import br.com.beautystyle.model.entity.Costumer;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.EventJobCrossRef;
import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.retrofit.webclient.EventWebClient;
import br.com.beautystyle.util.CalendarUtil;

public class EventRepository {

    @Inject
    EventWebClient webClient;
    @Inject
    EventAsynchDao dao;
    @Inject
    EventWithJobRepository eventWithJobRepository;
    @Inject
    ClientRepository costumerRepository;
    @Inject
    JobRepository jobRepository;
    private final String profile;
    private final Long tenant;

    @Inject
    public EventRepository(SharedPreferences preferences) {
        profile = preferences.getString(PROFILE_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public LiveData<Resource<List<EventWithClientAndJobs>>> getByDateFromRoom() {
        MutableLiveData<Resource<List<EventWithClientAndJobs>>> mutableEvents = new MutableLiveData<>();
        dao.getAllByDate(CalendarUtil.selectedDate).doOnSuccess(eventsFromRoom -> {
                    Resource<List<EventWithClientAndJobs>> resource =
                            new Resource<>(eventsFromRoom, null);
                    mutableEvents.setValue(resource);
                }).doOnError(error ->
                        mutableEvents.setValue(new Resource<>(null, error.getMessage())))
                .subscribe();
        return mutableEvents;
    }

    public LiveData<Resource<List<EventWithClientAndJobs>>> getByDateFromApi(LocalDate date) {
        MutableLiveData<Resource<List<EventWithClientAndJobs>>> mutableEvents = new MutableLiveData<>();
        webClient.getAllByDate(date, new ResultsCallBack<List<EventWithClientAndJobs>>() {
            @Override
            public void onSuccess(List<EventWithClientAndJobs> eventsFromApi) {
                updateLocalDatabase(eventsFromApi, mutableEvents);
            }

            @Override
            public void onError(String error) {
                mutableEvents.setValue(new Resource<>(null, error));
            }
        });
        return mutableEvents;
    }

    private void updateLocalDatabase(List<EventWithClientAndJobs> eventsFromApi,
                                     MutableLiveData<Resource<List<EventWithClientAndJobs>>> mutableEvents) {
        //update costumers -> update jobs -> update events
        updateCostumersJobsAndEvents(eventsFromApi, new ResultsCallBack<List<EventWithClientAndJobs>>() {
            @Override
            public void onSuccess(List<EventWithClientAndJobs> events) {
                Resource<List<EventWithClientAndJobs>> resource = new Resource<>(events, null);
                mutableEvents.setValue(resource);
            }

            @Override
            public void onError(String error) {
                mutableEvents.setValue(new Resource<>(null, error));
            }
        });
    }

    public void updateAll(List<EventWithClientAndJobs> eventsFromApi,
                          ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        dao.getAllByDate(CalendarUtil.selectedDate)
                .doOnSuccess(eventsFromRoom -> {
                            deleteFromRoomIfNotExistOnApi(eventsFromApi, eventsFromRoom);
                            setEventIdToUpdate(eventsFromRoom, eventsFromApi);
                            updateAllOnRoom(eventsFromApi, callBack);
                        }
                ).subscribe();
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
        newEvents.forEach(event -> event.setEventId(null));
        dao.insertAll(newEvents)
                .doOnSuccess(ids -> {
                            setEventIds(newEvents, ids);
                            mergeEventId(eventsFromApi, newEvents);
                            callBack.onSuccess(eventsFromApi);
                        }
                ).subscribe();
    }

    private void deleteFromRoomIfNotExistOnApi(List<EventWithClientAndJobs> eventsFromApi,
                                               List<EventWithClientAndJobs> eventsFromRoom) {
        eventsFromRoom.forEach(fromRoom -> {
            List<Event> events = eventsFromApi.stream()
                    .map(EventWithClientAndJobs::getEvent)
                    .collect(Collectors.toList());
            if (fromRoom.getEvent().isNotExistOnApi(events))
                dao.delete(fromRoom.getEvent()).subscribe();
        });
    }

    private void mergeEventId(List<EventWithClientAndJobs> eventsFromApi,
                              List<Event> newEvents) {
        eventsFromApi.forEach(eventFromApi ->
                newEvents.forEach(newEvent -> {
                            if ((isApiIdEquals(eventFromApi.getEvent(), newEvent))) {
                                setEventIds(eventFromApi.getEvent(), newEvent);
                            }
                        }
                )
        );
    }

    private void setEventIds(List<Event> eventsToInsert, List<Long> ids) {
        for (int i = 0; i < eventsToInsert.size(); i++) {
            eventsToInsert.get(i).setEventId(ids.get(i));
        }
    }

    @NonNull
    private List<Event> getEventsToInsert(List<EventWithClientAndJobs> eventsFromApi) {
        return eventsFromApi.stream()
                .map(EventWithClientAndJobs::getEvent)
                .filter(event -> !event.checkId())
                .collect(Collectors.toList());
    }

    @NonNull
    private List<Event> getEventsToUpdate(List<EventWithClientAndJobs> eventsFromApi) {
        return eventsFromApi.stream()
                .map(EventWithClientAndJobs::getEvent)
                .filter(Event::checkId)
                .collect(Collectors.toList());
    }

    private void setEventIdToUpdate(List<EventWithClientAndJobs> eventsFromRoom,
                                    List<EventWithClientAndJobs> eventsFromApi) {
        eventsFromRoom.forEach(eventFromRoom ->
                eventsFromApi.forEach(eventFromApi -> {
                    if (isApiIdEquals(eventFromRoom.getEvent(), eventFromApi.getEvent())) {
                        setEventIds(eventFromApi.getEvent(), eventFromRoom.getEvent());
                    }
                })
        );
    }

    private void setEventIds(Event eventFromApi, Event eventFromRoom) {
        eventFromApi.setEventId(eventFromRoom.getEventId());
    }

    private boolean isApiIdEquals(Event eventFromRoom, Event eventFromApi) {
        return eventFromRoom.getApiId().equals(eventFromApi.getApiId());
    }

    public LiveData<Resource<EventWithClientAndJobs>> insertOnApi(EventWithClientAndJobs event) {
        MutableLiveData<Resource<EventWithClientAndJobs>> liveData = new MutableLiveData<>();
        webClient.insert(event, new ResultsCallBack<EventWithClientAndJobs>() {
            @Override
            public void onSuccess(EventWithClientAndJobs result) {
                liveData.setValue(new Resource<>(result, null));
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
        return liveData;
    }

    private List<EventJobCrossRef> getEventJobsCrossRefs(EventWithClientAndJobs event) {
        return event.getJobs().stream()
                .map(job -> new EventJobCrossRef(event.getEvent().getEventId(), job.getJobId()))
                .collect(Collectors.toList());
    }

    public LiveData<Resource<Void>> updateOnApi(EventWithClientAndJobs event) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        webClient.update(event, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                liveData.setValue(new Resource<>(result, null));
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Void>> deleteOnApi(Event event) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        webClient.delete(event.getApiId(), new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                liveData.setValue(new Resource<>(result, null));
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
        return liveData;
    }


    public void updateCostumersJobsAndEvents(List<EventWithClientAndJobs> events,
                                             ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        List<Costumer> costumers = getClients(events);
        costumerRepository.updateAndInsertAll(costumers,
                new ResultsCallBack<List<Costumer>>() {
                    @Override
                    public void onSuccess(List<Costumer> costumers) {
                        setCostumerIdOnEvents(costumers, events);
                        updateJobs(events, callBack);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }

                });
    }

    @NonNull
    private List<Costumer> getClients(List<EventWithClientAndJobs> events) {
        return events.stream()
                .map(EventWithClientAndJobs::getClient)
                .distinct()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void setCostumerIdOnEvents(List<Costumer> costumers, List<EventWithClientAndJobs> events) {
        events.forEach(event ->
                costumers.forEach(client -> {
                    try {
                        if (event.getClient().getApiId().equals(client.getApiId())) {
                            event.getClient().setClientId(client.getClientId());
                            event.getEvent().setClientCreatorId(client.getClientId());
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
                            if (jobFromApi.getApiId().equals(job.getApiId())) {
                                jobFromApi.setJobId(job.getJobId());
                            }
                        })
                )
        );
    }

    private void updateEvents(List<EventWithClientAndJobs> events,
                              ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        updateAll(events, new ResultsCallBack<List<EventWithClientAndJobs>>() {
            @Override
            public void onSuccess(List<EventWithClientAndJobs> events) {
                eventWithJobRepository.updateAll(events,callBack);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        });

    }

    public LiveData<Resource<List<Report>>> getReportByPeriod() {
        MutableLiveData<Resource<List<Report>>> liveData = new MutableLiveData<>();
        LocalDate startDate = CalendarUtil.selectedDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        webClient.getReportByPeriod(startDate, endDate, new ResultsCallBack<List<Report>>() {
            @Override
            public void onSuccess(List<Report> result) {
                liveData.setValue(new Resource<>(result, null));
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Void>> insertOnRoom(EventWithClientAndJobs eventWithClientAndJobs) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        Event event = eventWithClientAndJobs.getEvent();
        event.setEventId(null);
        event.setCompanyId(tenant);
        dao.insert(event)
                .doOnSuccess(id -> {
                    eventWithClientAndJobs.getEvent().setEventId(id);
                    insertEventWithJobsCrossRef(eventWithClientAndJobs, liveData);
                })
                .subscribe();
        return liveData;
    }

    public boolean isFreeUser() {
        return profile.equals("ROLE_FREE_ACCOUNT");
    }

    public boolean isUserPremium() {
        return profile.equals("ROLE_PROFISSIONAL");
    }

    private void insertEventWithJobsCrossRef(EventWithClientAndJobs eventWithClientAndJobs,
                                             MutableLiveData<Resource<Void>> liveData) {
        List<EventJobCrossRef> eventJobCrossRefs =
                new ArrayList<>(getEventJobsCrossRefs(eventWithClientAndJobs));
        eventWithJobRepository.insert(eventJobCrossRefs).doOnComplete(() ->
                liveData.setValue(new Resource<>(null, null))
        ).subscribe();
    }

    public LiveData<Resource<Void>> deleteOnRoom(Event event) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        dao.delete(event).doOnError(error ->
                        liveData.setValue(new Resource<>(null, error.getMessage()))
                ).doOnComplete(() ->
                        liveData.setValue(new Resource<>(null, null)))
                .subscribe();
        return liveData;
    }

    public LiveData<Resource<Void>> updateOnRoom(EventWithClientAndJobs eventWithClientAndJobs) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        dao.update(eventWithClientAndJobs.getEvent())
                .doOnComplete(() ->
                        eventWithJobRepository.update(
                                eventWithClientAndJobs, liveData)
                ).doOnError(error ->
                        liveData.setValue(new Resource<>(null, error.getMessage()))
                ).subscribe();
        return liveData;
    }
}