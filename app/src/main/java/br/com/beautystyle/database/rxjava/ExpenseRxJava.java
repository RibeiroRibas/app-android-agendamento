package br.com.beautystyle.database.rxjava;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.BeautyStyleDatabase;
import br.com.beautystyle.database.dao.RoomExpenseDao;
import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.util.CalendarUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ExpenseRxJava {

    private final RoomExpenseDao dao;

    @Inject
    public ExpenseRxJava(BeautyStyleDatabase database) {
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

    public Single<List<Expense>> getByPeriod(LocalDate startDate,
                                             LocalDate endDate,
                                             Long tenant) {
        return dao.getByPeriod(startDate, endDate, tenant)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<LocalDate>> getYearsList(Long tenant) {
        return dao.getYearsList(tenant)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Single<List<Expense>> getByDate(Long tenant) {
        return dao.getByDate(CalendarUtil.selectedDate,tenant)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
