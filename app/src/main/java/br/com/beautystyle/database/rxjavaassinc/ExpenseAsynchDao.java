package br.com.beautystyle.database.rxjavaassinc;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.BeautyStyleDatabase;
import br.com.beautystyle.database.dao.RoomExpenseDao;
import br.com.beautystyle.model.entity.Expense;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ExpenseAsynchDao {

    private final RoomExpenseDao dao;

    @Inject
    public ExpenseAsynchDao(BeautyStyleDatabase database) {
        dao = database.getRoomExpenseDao();
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

    public Single<List<Long>> insertAll(List<Expense> response) {
        return dao.insertAll(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Expense>> getByPeriod(LocalDate startDate, LocalDate endDate){
        return dao.getByPeriod(startDate, endDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
