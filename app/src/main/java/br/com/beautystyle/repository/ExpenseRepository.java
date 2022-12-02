package br.com.beautystyle.repository;

import static br.com.beautystyle.repository.ConstantsRepository.FREE_ACCOUNT;
import static br.com.beautystyle.repository.ConstantsRepository.PROFILE_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.USER_PREMIUM;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.rxjava.ExpenseRxJava;
import br.com.beautystyle.model.util.Report;
import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.retrofit.webclient.ExpenseWebClient;
import br.com.beautystyle.util.CalendarUtil;

public class ExpenseRepository {

    @Inject
    ExpenseWebClient webClient;
    @Inject
    ExpenseRxJava dao;
    private LocalDate startDate;
    private LocalDate endDate;
    private final String profile;
    private final Long tenant;

    @Inject
    public ExpenseRepository(SharedPreferences preferences) {
        profile = preferences.getString(PROFILE_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public LiveData<Resource<List<Expense>>> getByPeriodLiveData() {
        setDates();
        MutableLiveData<Resource<List<Expense>>> liveData = new MutableLiveData<>();
        getByPeriodFromRoom(liveData);
        if (isUserPremium()) {
            getByPeriodFromApi(liveData);
        }
        return liveData;
    }

    private void getByPeriodFromRoom(MutableLiveData<Resource<List<Expense>>> liveData) {
        dao.getByPeriod(startDate, endDate, tenant).doOnSuccess(expenses ->
                        liveData.setValue(new Resource<>(expenses, null)))
                .subscribe();
    }

    private void setDates() {
        startDate = CalendarUtil.selectedDate.with(TemporalAdjusters.firstDayOfMonth());
        endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
    }

    public void getByPeriodFromApi(MutableLiveData<Resource<List<Expense>>> liveData) {
        webClient.getByPeriod(startDate, endDate,
                new ResultsCallBack<List<Expense>>() {
                    @Override
                    public void onSuccess(List<Expense> expensesFromApi) {
                        dao.getByPeriod(startDate, endDate, tenant).doOnSuccess(expensesFromRoom ->
                                        updateLocal(expensesFromApi, liveData, expensesFromRoom))
                                .subscribe();
                    }

                    @Override
                    public void onError(String error) {
                        liveData.setValue(new Resource<>(null, error));
                    }
                });
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
                    if (expenseFromRoom.isApiIdEquals(expenseFromApi))
                        expenseFromApi.setId(expenseFromRoom.getId());
                }));
    }

    public LiveData<Resource<List<String>>> getYearsListLiveData() {
        MutableLiveData<Resource<List<String>>> liveData = new MutableLiveData<>();
        getYearsListFromRoom(liveData);
        if (isUserPremium()) {
            getYearsListFromApi(liveData);
        }
        return liveData;
    }

    private void getYearsListFromRoom(MutableLiveData<Resource<List<String>>> liveData) {
        dao.getYearsList(tenant).doOnSuccess(dates -> {
            List<String> years = new ArrayList<>();
            dates.forEach(date -> {
                years.add(String.valueOf(date.getYear()));
            });
            liveData.setValue(new Resource<>(years, null));
        }).subscribe();
    }

    private void getYearsListFromApi(MutableLiveData<Resource<List<String>>> liveData) {
        webClient.getYearsList(new ResultsCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> years) {
                liveData.setValue(new Resource<>(years, null));
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public LiveData<Resource<Expense>> insert(Expense expense) {
        MutableLiveData<Resource<Expense>> liveData = new MutableLiveData<>();
        expense.setTenant(tenant);
        if (isFreeAccount()) {
            insertOnRoom(expense, liveData);
        }
        if (isUserPremium()) {
            insertOnApi(expense, liveData);
        }
        return liveData;
    }

    private void insertOnRoom(Expense expense, MutableLiveData<Resource<Expense>> liveData) {
        dao.insert(expense).doOnSuccess(id -> {
            expense.setId(id);
            liveData.setValue(new Resource<>(expense, null));
        }).subscribe();
    }

    private void insertOnApi(Expense expense, MutableLiveData<Resource<Expense>> liveData) {
        webClient.insert(expense, new ResultsCallBack<Expense>() {
            @Override
            public void onSuccess(Expense result) {
                insertOnRoom(result, liveData);
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public LiveData<Resource<Expense>> update(Expense expense) {
        MutableLiveData<Resource<Expense>> liveData = new MutableLiveData<>();
        if (isUserPremium()) {
            updateOnApi(expense, liveData);
        }
        if (isFreeAccount()) {
            updateOnRoom(expense, liveData);
        }
        return liveData;
    }

    private void updateOnRoom(Expense expense, MutableLiveData<Resource<Expense>> liveData) {
        dao.update(expense).doOnComplete(() ->
                        liveData.setValue(new Resource<>(expense, null)))
                .subscribe();
    }

    private void updateOnApi(Expense expense, MutableLiveData<Resource<Expense>> liveData) {
        webClient.update(expense, new ResultsCallBack<Expense>() {
            @Override
            public void onSuccess(Expense result) {
                updateOnRoom(result, liveData);
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public LiveData<Resource<Void>> delete(Expense expense) {
        MutableLiveData<Resource<Void>> liveData = new MutableLiveData<>();
        if (isUserPremium()) {
            deleteOnApi(expense, liveData);
        }
        if (isFreeAccount()) {
            deleteOnRoom(expense, liveData);
        }
        return liveData;
    }

    private void deleteOnRoom(Expense expense, MutableLiveData<Resource<Void>> liveData) {
        dao.delete(expense).doOnComplete(() ->
                liveData.setValue(new Resource<>(null, null))
        ).subscribe();
    }

    private void deleteOnApi(Expense expense, MutableLiveData<Resource<Void>> liveData) {
        webClient.delete(expense, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                deleteOnRoom(expense, liveData);
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public LiveData<Resource<List<Report>>> getReportByPeriod() {
        setDates();
        MutableLiveData<Resource<List<Report>>> liveData = new MutableLiveData<>();
        webClient.getReportByPeriod(startDate, endDate,
                new ResultsCallBack<List<Report>>() {
                    @Override
                    public void onSuccess(List<Report> result) {
                        liveData.setValue(new Resource<>(result, null));
                    }

                    @Override
                    public void onError(String error) {
                        liveData.setValue(new Resource<>(null, error));
                    }
                });
        return liveData;
    }

    private boolean isFreeAccount() {
        return profile.equals(FREE_ACCOUNT);
    }

    private boolean isUserPremium() {
        return profile.equals(USER_PREMIUM);
    }
}
