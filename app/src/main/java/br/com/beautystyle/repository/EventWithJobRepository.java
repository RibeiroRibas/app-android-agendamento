package br.com.beautystyle.repository;

import androidx.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.room.BeautyStyleDatabase;
import br.com.beautystyle.database.room.dao.RoomEventWithJobsDao;
import br.com.beautystyle.model.entity.EventJobCroosRef;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EventWithJobRepository {

    private final RoomEventWithJobsDao dao;

    @Inject
    public EventWithJobRepository(BeautyStyleDatabase localDatabase) {
        dao = localDatabase.getRoomEventWithJobDao();
    }

    public Completable insert(List<EventJobCroosRef> eventJobCroosRefs) {
        return dao.insert(eventJobCroosRefs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @NonNull
    private Long[] getIds(List<EventJobCroosRef> eventJobCroosRefs) {
        return eventJobCroosRefs.stream()
                .map(EventJobCroosRef::getEventId)
                .distinct()
                .toArray(Long[]::new);
    }

    public Completable deleteAllByIds(List<EventJobCroosRef> eventJobCroosRefs) {
        Long[] eventIds = getIds(eventJobCroosRefs);
        return dao.deleteAllByIds(eventIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
