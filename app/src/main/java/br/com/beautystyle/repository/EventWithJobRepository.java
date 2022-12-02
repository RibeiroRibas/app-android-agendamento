package br.com.beautystyle.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.com.beautystyle.database.BeautyStyleDatabase;
import br.com.beautystyle.database.dao.RoomEventWithJobsDao;
import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.EventJobCrossRef;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EventWithJobRepository {

    private final RoomEventWithJobsDao dao;

    @Inject
    public EventWithJobRepository(BeautyStyleDatabase localDatabase) {
        dao = localDatabase.getRoomEventWithJobDao();
    }

    public Completable insert(List<EventJobCrossRef> eventJobCrossRefs) {
        return dao.insert(eventJobCrossRefs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    private Long[] getIds(List<EventJobCrossRef> eventJobCrossRefs) {
        return eventJobCrossRefs.stream()
                .map(EventJobCrossRef::getEventId)
                .distinct()
                .toArray(Long[]::new);
    }

    public Completable deleteAllByIds(List<EventJobCrossRef> eventJobCrossRefs) {
        Long[] eventIds = getIds(eventJobCrossRefs);
        return dao.deleteAllByIds(eventIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void update(EventWithClientAndJobs eventWithClientAndJobs,
                       MutableLiveData<Resource<Void>> liveData) {
        List<EventJobCrossRef> eventJobCrossRefs =
                new ArrayList<>(getEventJobsCrossRefs(eventWithClientAndJobs));
        deleteEventWithJobsCrossRef(liveData, eventJobCrossRefs);
    }

    private List<EventJobCrossRef> getEventJobsCrossRefs(EventWithClientAndJobs event) {
        return event.getJobs().stream()
                .map(job -> new EventJobCrossRef(event.getEvent().getId(), job.getId()))
                .collect(Collectors.toList());
    }

    private void deleteEventWithJobsCrossRef(MutableLiveData<Resource<Void>> liveData,
                                             List<EventJobCrossRef> eventJobCrossRefs) {
        deleteAllByIds(eventJobCrossRefs)
                .doOnComplete(() ->
                        insertEventJobsCrossRef(liveData, eventJobCrossRefs)
                ).subscribe();
    }

    private void insertEventJobsCrossRef(MutableLiveData<Resource<Void>> liveData,
                                         List<EventJobCrossRef> eventJobCrossRefs) {
        insert(eventJobCrossRefs)
                .doOnComplete(() ->
                        liveData.setValue(new Resource<>(null, null))
                ).subscribe();
    }

    public void updateAll(List<EventWithClientAndJobs> events,
                          ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        List<EventJobCrossRef> eventJobCrossRefs = new ArrayList<>();
        events.forEach(event -> eventJobCrossRefs.addAll(getEventJobsCrossRefs(event)));
        deleteAllByIds(eventJobCrossRefs)
                .doOnComplete(() ->
                        insert(eventJobCrossRefs)
                                .doOnComplete(() -> callBack.onSuccess(events))
                                .subscribe()
                ).subscribe();
    }
}
