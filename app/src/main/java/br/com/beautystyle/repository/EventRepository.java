package br.com.beautystyle.repository;

import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.com.beautystyle.database.retrofit.callback.CallBackReturn;
import br.com.beautystyle.database.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.database.retrofit.service.EventService;
import br.com.beautystyle.database.room.BeautyStyleDatabase;
import br.com.beautystyle.database.room.dao.RoomEventDao;
import br.com.beautystyle.database.room.references.EventWithClientAndJobs;
import br.com.beautystyle.model.Report;
import br.com.beautystyle.model.entity.Event;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;

public class EventRepository {

    private final RoomEventDao daoEvent;
    @Inject
    EventService service;
    private final String token;
    private final Long tenant;

    @Inject
    public EventRepository(BeautyStyleDatabase database, SharedPreferences preferences) {
        daoEvent = database.getRoomEventDao();
        token = preferences.getString(TOKEN_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public void insertOnApi(EventWithClientAndJobs event,
                            ResultsCallBack<EventWithClientAndJobs> callBack) {
        event.getEvent().setCompanyId(tenant);
        Call<EventWithClientAndJobs> callInsert = service.insert(event, token);
        callInsert.enqueue(new CallBackReturn<>(
                        new CallBackReturn.CallBackResponse<EventWithClientAndJobs>() {
                            @Override
                            public void onSuccess(EventWithClientAndJobs response) {
                                callBack.onSuccess(response);
                            }

                            @Override
                            public void onError(String error) {
                                callBack.onError(error);
                            }
                        }
                )
        );
    }

    public Single<Long> insertOnRoom(Event event) {
        return daoEvent.insert(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public void updateOnApi(EventWithClientAndJobs event,
                            ResultsCallBack<Void> callBack) {
        Call<Void> callUpdate = service.update(event, token);
        callUpdate.enqueue(new CallBackWithoutReturn(
                        new CallBackWithoutReturn.CallBackResponse() {
                            @Override
                            public void onSuccess() {
                                callBack.onSuccess(null);
                            }

                            @Override
                            public void onError(String erro) {
                                callBack.onError(erro);
                            }
                        }
                )
        );
    }

    public Completable updateOnRoom(Event event) {
        return daoEvent.update(event)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public void deleteOnApi(Long eventId, ResultsCallBack<Void> callBack) {
        Call<Void> callDelete = service.delete(eventId, token);
        callDelete.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
            @Override
            public void onSuccess() {
                callBack.onSuccess(null);
            }

            @Override
            public void onError(String erro) {
                callBack.onError(erro);
            }
        }));
    }

    public Completable deleteOnRoom(Event event) {
        return daoEvent.delete(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<EventWithClientAndJobs>> getByDateFromRooom(LocalDate date) {
        return daoEvent.getEventListByDate(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void getByDateFromApi(LocalDate date,
                                 ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        Call<List<EventWithClientAndJobs>> callByDate =
                service.getByDate(date, tenant, token);
        callByDate.enqueue(new CallBackReturn<>(
                        new CallBackReturn.CallBackResponse<List<EventWithClientAndJobs>>() {
                            @Override
                            public void onSuccess(List<EventWithClientAndJobs> eventListDto) {
                                callBack.onSuccess(eventListDto);
                            }

                            @Override
                            public void onError(String error) {
                                callBack.onError(error);
                            }
                        }
                )
        );
    }

    public void updateAll(List<EventWithClientAndJobs> eventsFromApi,
                          ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        getByDateFromRooom(eventsFromApi.get(0).getEvent().getEventDate())
                .doOnSuccess(eventsFromRoom -> {
                            setEventIdToUpdate(eventsFromRoom, eventsFromApi);
                            List<Event> eventsToUpdate = getEventsToUpdate(eventsFromApi);
                            updateOnRoom(eventsToUpdate)
                                    .doOnComplete(() -> insertOnRoom(eventsFromApi, callBack))
                                    .subscribe();
                        }
                ).subscribe();

    }

    private void insertOnRoom(List<EventWithClientAndJobs> events,
                              ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        List<Event> newEvents = getEventsToInsert(events);
        if (newEvents.isEmpty()) {
            callBack.onSuccess(events);
        } else {
            newEvents.forEach(event -> event.setEventId(null));
            insertAllInRoom(newEvents)
                    .doOnSuccess(ids -> {
                                setEventIds(newEvents, ids);
                                mergeEventId(events, newEvents);
                                callBack.onSuccess(events);
                            }
                    ).subscribe();
        }
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

    private Single<List<Long>> insertAllInRoom(List<Event> newEvents) {
        return daoEvent.insertAll(newEvents)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateOnRoom(List<Event> updateEvents) {
        return daoEvent.updatelist(updateEvents)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
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

    public Single<List<Event>> getEventsByClientId(Long clientId) {
        return daoEvent.findByClientId(clientId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void getYearsListFromApi(ResultsCallBack<List<String>> callBack) {
        Call<List<String>> callYearsList = service.getYearsList(tenant, token);
        callYearsList.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<String>>() {
            @Override
            public void onSuccess(List<String> response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public void getReportByPeriodFromApi(LocalDate startDate, LocalDate endDate,
                                         ResultsCallBack<List<Report>> callBack) {
        Call<List<Report>> callReport = service.getReportByPeriod(startDate, endDate, tenant, token);
        callReport.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<Report>>() {
            @Override
            public void onSuccess(List<Report> response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public void getEventReportByDateFromApi(LocalDate selectedDate,
                                            ResultsCallBack<List<Report>> callBack) {
        Call<List<Report>> callReport = service.getReportByDate(selectedDate, tenant, token);
        callReport.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<Report>>() {
            @Override
            public void onSuccess(List<Report> response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }
}