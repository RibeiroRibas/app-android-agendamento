package br.com.beautystyle.data.repository;

import android.content.Context;

import java.util.List;

import br.com.beautystyle.data.database.BeautyStyleDatabase;
import br.com.beautystyle.data.database.dao.RoomServiceDao;
import br.com.beautystyle.model.Services;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ServiceRepository {

    private final RoomServiceDao dao;

    public ServiceRepository(Context context) {
        dao = BeautyStyleDatabase.getInstance(context).getRoomServiceDao();
    }

    public Completable insert(Services service) {
        return dao.insert(service)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable update(Services service) {
        return dao.update(service)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable delete(Services service) {
        return dao.delete(service)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Services>> getAll() {
        return dao.getAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
