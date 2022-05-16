package br.com.beautystyle.repository;

import android.content.Context;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.database.retrofit.BeautyStyleRetrofit;
import br.com.beautystyle.database.retrofit.callback.CallBackReturn;
import br.com.beautystyle.database.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.database.room.BeautyStyleDatabase;
import br.com.beautystyle.database.room.dao.RoomEventDao;
import br.com.beautystyle.database.room.references.EventWithJobs;
import br.com.beautystyle.model.EventDto;
import br.com.beautystyle.model.entities.Client;
import br.com.beautystyle.model.entities.Event;
import br.com.beautystyle.model.entities.Job;
import br.com.beautystyle.retrofit.service.EventService;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;

public class EventRepository {

    private final RoomEventDao daoEvent;
    private final EventService service;
    private final EventWithJobRepository eventWithJobRepository;

    public EventRepository(Context context) {
        daoEvent = BeautyStyleDatabase.getInstance(context).getRoomEventDao();
        service = new BeautyStyleRetrofit().getEventService();
        eventWithJobRepository = new EventWithJobRepository(context);
    }

    public void insert(Event event, List<Job> jobList, Client client, ResultsCallBack<Event> callBack) {
        EventDto eventDto = new EventDto(event, client, jobList);
        Call<EventDto> callInsert = service.insert(eventDto);
        insertOnApi(callInsert, jobList, callBack);
    }

    private void insertOnApi(Call<EventDto> callInsert, List<Job> jobList, ResultsCallBack<Event> callBack) {
        callInsert.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<EventDto>() {
            @Override
            public void onSuccess(EventDto response) {
                Event event = response.convert();
                callBack.onSuccess(event);
                if(response.checkId())
                    insertOnRoom(event, jobList);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    private void insertOnRoom(Event event, List<Job> jobList) {
        daoEvent.insert(event)
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> eventWithJobRepository.insert(event, jobList))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }


    public void update(Event event, Client client, List<Job> jobList, ResultsCallBack<Event> callBack) {
        EventDto eventDto = new EventDto(event, client, jobList);
        Call<EventDto> callUpdate = service.update(eventDto);
        updateOnApi(callUpdate, jobList, callBack);
    }

    private void updateOnApi(Call<EventDto> callUpdate, List<Job> jobList, ResultsCallBack<Event> callBack) {
        callUpdate.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<EventDto>() {
            @Override
            public void onSuccess(EventDto response) {
                Event event = response.convert();
                callBack.onSuccess(event);
                updateOnRoom(event, jobList);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    private void updateOnRoom(Event event, List<Job> jobList) {
        daoEvent.update(event)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> eventWithJobRepository.update(event.getEventId(), jobList))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }


    public void delete(Event event, ResultsCallBack<Void> callBack) {
        Call<Void> callDelete = service.delete(event.getEventId());
        deleteOnApi(event, callDelete, callBack);
    }

    private void deleteOnApi(Event event, Call<Void> callDelete, ResultsCallBack<Void> callBack) {
        callDelete.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
            @Override
            public void onSuccess() {
                callBack.onSuccess(null);
                deleteOnRoom(event);
            }

            @Override
            public void onError(String erro) {
                callBack.onError(erro);
            }
        }));
    }

    private void deleteOnRoom(Event event) {
        daoEvent.delete(event)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public Single<List<EventWithJobs>> getByDateFromRooom(LocalDate date) {
        return daoEvent.getEventListByDate(date).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }



    public Single<List<Event>> getAllFromRoom() {
        return daoEvent.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void getAllFromApi(ResultsCallBack<List<Event>> callBack) {
        Call<List<EventDto>> callGetAll = service.getAll();
        callGetAll.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<EventDto>>() {
            @Override
            public void onSuccess(List<EventDto> eventListDto) {
                callBack.onSuccess(Event.convert(eventListDto));
                insertAllOnRoom(eventListDto).subscribe();
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public Completable insertAllOnRoom(List<EventDto> eventListDto) {
        return daoEvent.insertAll(Event.convert(eventListDto))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void getByDateFromApi(LocalDate date, ResultsCallBack<List<EventDto>> callBack) {
        Call<List<EventDto>> callByDate = service.getByDate(date);
        callByDate.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<EventDto>>() {
            @Override
            public void onSuccess(List<EventDto> eventListDto) {
                callBack.onSuccess(eventListDto);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }
}