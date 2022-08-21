package br.com.beautystyle.database.rxjavaassinc;

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.BeautyStyleDatabase;
import br.com.beautystyle.database.dao.RoomClientDao;
import br.com.beautystyle.model.entity.Costumer;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CostumerAsynchDao {

    private final RoomClientDao dao;

    @Inject
    public CostumerAsynchDao(BeautyStyleDatabase database) {
        dao = database.getRoomClientDao();
    }

    public Observable<List<Costumer>> getAllObservable(Long tenant) {
        return dao.getAllObservable(tenant)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Costumer>> getAll(Long tenant) {
        return dao.getAll(tenant)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable insert(Costumer costumer) {
        return dao.insert(costumer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable update(Costumer costumer) {
        return dao.update(costumer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Long>> insertAll(List<Costumer> response) {
        return dao.insertAll(response)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable delete(Costumer costumer) {
        return dao.delete(costumer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateAll(List<Costumer> updatedCostumerList) {
        return dao.updateAll(updatedCostumerList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
