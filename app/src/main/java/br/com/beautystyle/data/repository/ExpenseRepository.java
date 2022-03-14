package br.com.beautystyle.data.repository;

import android.content.Context;

import java.util.List;

import br.com.beautystyle.data.database.BeautyStyleDatabase;
import br.com.beautystyle.data.database.dao.RoomExpenseDao;
import br.com.beautystyle.model.Expense;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ExpenseRepository {

    private final RoomExpenseDao dao;

    public ExpenseRepository(Context context) {
        dao = BeautyStyleDatabase.getInstance(context).getRoomExpenseDao();
    }

    public Single<Long> insert(Expense expense) {
        return dao.insert(expense)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable update(Expense expense) {
        return dao.update(expense)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable delete(Expense expense) {
        return dao.delete(expense)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<List<Expense>> getAll() {
        return dao.getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
