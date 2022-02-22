package br.com.beautystyle.data.repository;

import android.content.Context;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.data.db.BeautyStyleDatabase;
import br.com.beautystyle.data.db.dao.RoomEventDao;
import br.com.beautystyle.domain.model.Event;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EventRepository {

    private final RoomEventDao dao;

    public EventRepository(Context context) {
        dao = BeautyStyleDatabase.getInstance(context).getRoomEventDao();
    }

    public Single<Long> insert(Event event) {
       return dao.insert(event)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable update(Event event) {
          return dao.update(event)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable delete(Event event) {
        return dao.delete(event).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Event>> getByDate(LocalDate date) {
        return  dao.getByDate(date).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Event>> getAll() {
        return dao.getAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}