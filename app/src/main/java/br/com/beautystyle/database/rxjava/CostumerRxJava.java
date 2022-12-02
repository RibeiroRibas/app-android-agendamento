package br.com.beautystyle.database.rxjava;

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.BeautyStyleDatabase;
import br.com.beautystyle.database.dao.RoomCustomerDao;
import br.com.beautystyle.model.entity.Customer;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CostumerRxJava {

    private final RoomCustomerDao dao;

    @Inject
    public CostumerRxJava(BeautyStyleDatabase database) {
        dao = database.getRoomClientDao();
    }

    public Observable<List<Customer>> getAllObservable(Long tenant) {
        return dao.getAllObservable(tenant)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Customer>> getAll(Long tenant) {
        return dao.getAll(tenant)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable insert(Customer customer) {
        return dao.insert(customer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable update(Customer customer) {
        return dao.update(customer)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Single<List<Long>> insertAll(List<Customer> response) {
        return dao.insertAll(response)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }

    public Completable delete(Customer customer) {
        return dao.delete(customer)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateAll(List<Customer> updatedCustomerList) {
        return dao.updateAll(updatedCustomerList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
