package br.com.beautystyle.repository;

import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.retrofit.callback.CallBackReturn;
import br.com.beautystyle.database.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.database.retrofit.service.ExpenseService;
import br.com.beautystyle.database.room.BeautyStyleDatabase;
import br.com.beautystyle.database.room.dao.RoomExpenseDao;
import br.com.beautystyle.model.Report;
import br.com.beautystyle.model.entity.Expense;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;

public class ExpenseRepository {

    private final RoomExpenseDao dao;
    @Inject
    ExpenseService service;
    private final String token;
    private final Long tenant;

    @Inject
    public ExpenseRepository(BeautyStyleDatabase database, SharedPreferences preferences) {
        dao = database.getRoomExpenseDao();
        token = preferences.getString(TOKEN_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }


    public void insertOnApi(Expense expense, ResultsCallBack<Expense> callBack) {
        expense.setCompanyId(tenant);
        Call<Expense> callNewExpense = service.insert(expense, token);
        callNewExpense.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<Expense>() {
            @Override
            public void onSuccess(Expense response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public Single<Long> insertOnRoom(Expense expense) {
        return dao.insert(expense)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void updateOnApi(Expense expense, ResultsCallBack<Expense> callBack) {
        Call<Expense> callUpdate = service.update(expense, token);
        callUpdate.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<Expense>() {
            @Override
            public void onSuccess(Expense response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));

    }

    public Completable updateOnRoom(Expense expense) {
        return dao.update(expense)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public void deleteOnApi(Expense expense, ResultsCallBack<Void> callack) {
        Call<Void> callDelete = service.delete(expense.getApiId(), token);
        callDelete.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
            @Override
            public void onSuccess() {
                callack.onSuccess(null);
            }

            @Override
            public void onError(String erro) {
                callack.onError(erro);
            }
        }));
    }

    public Completable deleteOnRoom(Expense expense) {
        return dao.delete(expense)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Single<List<Long>> insertAllOnRoom(List<Expense> response) {
        return dao.insertAll(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public Single<List<Expense>> getByPeriodFromRoom(LocalDate startDate, LocalDate endDate) {
        return dao.getByPeriod(startDate, endDate)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void getByPeriodFromApi(LocalDate startDate, LocalDate endDate,
                                   ResultsCallBack<List<Expense>> callBack) {
        Call<List<Expense>> callByPeriod = service.getByPeriod(startDate, endDate, tenant, token);
        callByPeriod.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<Expense>>() {
            @Override
            public void onSuccess(List<Expense> expensesFromApi) {
                callBack.onSuccess(expensesFromApi);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));

    }

    public void getReportByPeriodFromApi(LocalDate startDate, LocalDate endDate,
                                         ResultsCallBack<List<Report>> callBack) {
        Call<List<Report>> callByPeriod = service.getReportByPeriod(startDate, endDate, tenant, token);
        callByPeriod.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<Report>>() {
            @Override
            public void onSuccess(List<Report> expensesFromApi) {
                callBack.onSuccess(expensesFromApi);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));

    }

    private void setNewIdsOnList(List<Expense> expensesFromApi, List<Long> ids) {
        for (int i = 0; i < expensesFromApi.size(); i++) {
            expensesFromApi.get(i).setId(ids.get(i));
        }
    }

    private void setExpenseRoomId(List<Expense> expensesFromRoom, List<Expense> expensesFromApi) {
        expensesFromRoom.forEach(expenseFromRoom ->
                expensesFromApi.forEach(expenseFromApi -> {
                    if (expenseFromRoom.getApiId().equals(expenseFromApi.getApiId()))
                        expenseFromApi.setId(expenseFromRoom.getId());
                }));
    }

    public void updateLocalDatabase(List<Expense> expensesFromApi, List<Expense> expensesFromRoom,
                                    ResultsCallBack<List<Expense>> callBack) {
        setExpenseRoomId(expensesFromRoom, expensesFromApi);
        insertAllOnRoom(expensesFromApi)
                .doOnSuccess(ids -> {
                    setNewIdsOnList(expensesFromApi, ids);
                    callBack.onSuccess(expensesFromApi);
                }).subscribe();
    }

    public void getYearsListFromApi(ResultsCallBack<List<String>> callBack) {
        Call<List<String>> callYears = service.getYearsList(tenant, token);
        callYears.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<String>>() {
            @Override
            public void onSuccess(List<String> response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));

    }

    public void getExpenseReportByDateFromApi(LocalDate selectedDate,
                                              ResultsCallBack<List<Report>> callBack) {
        Call<List<Report>> callReport = service.getReportByDate(selectedDate, tenant, token);
        callReport.enqueue(new CallBackReturn<>(
                new CallBackReturn.CallBackResponse<List<Report>>() {
                    @Override
                    public void onSuccess(List<Report> response) {
                        callBack.onSuccess(response);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                }));

    }
}
