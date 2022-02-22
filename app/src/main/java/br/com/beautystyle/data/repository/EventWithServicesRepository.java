package br.com.beautystyle.data.repository;

import android.content.Context;

import java.util.List;

import br.com.beautystyle.data.db.BeautyStyleDatabase;
import br.com.beautystyle.data.db.dao.RoomEventWithServicesDao;
import br.com.beautystyle.data.db.references.EventServiceCroosRef;
import br.com.beautystyle.data.db.references.EventWithServices;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EventWithServicesRepository{

    private final RoomEventWithServicesDao dao;

    public EventWithServicesRepository(Context context) {
        dao = BeautyStyleDatabase.getInstance(context).getRoomEventWithServicesDao();
    }

    public Completable insert(List<EventServiceCroosRef> eventServiceCroosRef) {
        return dao.insert(eventServiceCroosRef)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable delete(List<EventServiceCroosRef> eventServiceCroosRef) {
        return dao.delete(eventServiceCroosRef)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Single<List<EventWithServices>> getEventWithServices() {
        return dao.getEventWithServices().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<EventServiceCroosRef>> getById(long id) {
        return dao.getById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
