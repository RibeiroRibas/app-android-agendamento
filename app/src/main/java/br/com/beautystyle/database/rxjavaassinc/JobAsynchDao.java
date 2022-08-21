package br.com.beautystyle.database.rxjavaassinc;


import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.BeautyStyleDatabase;
import br.com.beautystyle.database.dao.RoomJobDao;
import br.com.beautystyle.model.entity.Job;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class JobAsynchDao {

    private final RoomJobDao dao;

    @Inject
    public JobAsynchDao(BeautyStyleDatabase database) {
        dao = database.getRoomJobDao();
    }

    public Observable<List<Job>> getAllLiveData(Long tenant) {
        return dao.getAllLiveData(tenant)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable insert(Job job) {
        return dao.insert(job)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable update(Job job) {
        return dao.update(job)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable updateAll(List<Job> jobs) {
        return dao.updateAll(jobs)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable delete(Job job) {
        return dao.delete(job)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Job>> getAll(Long tenant) {
        return dao.getAll(tenant)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Long>> insertAll(List<Job> jobs) {
        return dao.insertAll(jobs)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }
}
