package br.com.beautystyle.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.rxjavaassinc.ExpenseAsynchDao;
import br.com.beautystyle.model.Report;
import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.retrofit.service.ExpenseService;
import br.com.beautystyle.retrofit.webclient.ExpenseWebClient;
import br.com.beautystyle.util.CalendarUtil;

public class ExpenseRepository {

    @Inject
    ExpenseService service;
    @Inject
    ExpenseWebClient webClient;
    @Inject
    ExpenseAsynchDao dao;
    private LocalDate startDate;
    private LocalDate endDate;

    @Inject
    public ExpenseRepository() {
    }

    public LiveData<Resource<List<Expense>>> getByPeriodFromRoom() {
        setDates();
        MutableLiveData<Resource<List<Expense>>> liveData = new MutableLiveData<>();
        dao.getByPeriod(startDate, endDate).doOnSuccess(expenses ->
                        liveData.setValue(new Resource<>(expenses, null)))
                .doOnError(error ->
                        liveData.setValue(new Resource<>(null, error.getMessage()))
                ).subscribe();
        return liveData;
    }

    private void setDates() {
        startDate = CalendarUtil.selectedDate.with(TemporalAdjusters.firstDayOfMonth());
        endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    public LiveData<Resource<List<Expense>>> getByPeriodFromApi() {
        setDates();
        MutableLiveData<Resource<List<Expense>>> liveData = new MutableLiveData<>();
        webClient.getByPeriod(startDate, endDate,
                new ResultsCallBack<List<Expense>>() {
                    @Override
                    public void onSuccess(List<Expense> expensesFromApi) {
                        dao.getByPeriod(startDate, endDate).doOnSuccess(expensesFromRoom ->
                                        updateLocal(expensesFromApi, liveData, expensesFromRoom))
                                .subscribe();
                    }

                    @Override
                    public void onError(String error) {
                        liveData.setValue(new Resource<>(null, error));
                    }
                });
        return liveData;
    }

    private void updateLocal(List<Expense> expensesFromApi,
                             MutableLiveData<Resource<List<Expense>>> liveData,
                             List<Expense> expensesFromRoom) {
        deleteFromRoomIfNotExistOnApi(expensesFromApi, expensesFromRoom);
        setExpenseRoomId(expensesFromRoom, expensesFromApi);
        dao.insertAll(expensesFromApi).doOnSuccess(ids -> {
            setNewIdsOnList(expensesFromApi, ids);
            liveData.setValue(new Resource<>(expensesFromApi, null));
        }).subscribe();
    }

    private void deleteFromRoomIfNotExistOnApi(List<Expense> expensesFromApi, List<Expense> expensesFromRoom) {
        expensesFromRoom.forEach(fromRoom -> {
            if (fromRoom.isNotExistOnApi(expensesFromApi))
                dao.delete(fromRoom).subscribe();
        });
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

    public LiveData<Resource<List<String>>> getYearsListFromApi() {
        MutableLiveData<Resource<List<String>>> liveData = new MutableLiveData<>();
        webClient.getYearsList(new ResultsCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                liveData.setValue(new Resource<>(result, null));
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Expense>> insert(Expense expense) {
        MutableLiveData<Resource<Expense>> liveData = new MutableLiveData<>();
        webClient.insert(expense, new ResultsCallBack<Expense>() {
            @Override
            public void onSuccess(Expense result) {
                liveData.setValue(new Resource<>(result, null));
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Expense>> update(Expense expense) {
        MutableLiveData<Resource<Expense>> liveData = new MutableLiveData<>();
        webClient.update(expense, new ResultsCallBack<Expense>() {
            @Override
            public void onSuccess(Expense result) {
                liveData.setValue(new Resource<>(result, null));
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
        return liveData;
    }

    public LiveData<Resource<Void>> delete(Expense expense) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        webClient.delete(expense, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                liveData.setValue(new Resource<>(result, null));
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
        return liveData;
    }

    public LiveData<Resource<List<Report>>> getReportByPeriod() {
        setDates();
        MutableLiveData<Resource<List<Report>>> liveData = new MutableLiveData<>();
        webClient.getReportByPeriod(startDate, endDate,
                new ResultsCallBack<List<Report>>() {
                    @Override
                    public void onSuccess(List<Report> result) {
                        liveData.setValue(new Resource<>(result,null));
                    }

                    @Override
                    public void onError(String error) {
                        liveData.setValue(new Resource<>(null, error));
                    }
                });
        return liveData;
    }
}
