package br.com.beautystyle.repository;

import static br.com.beautystyle.repository.ConstantsRepository.PROFILE_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.USER_PREMIUM;

import android.content.SharedPreferences;
import android.util.ArraySet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import br.com.beautystyle.database.rxjava.EventRxJava;
import br.com.beautystyle.database.rxjava.ExpenseRxJava;
import br.com.beautystyle.model.util.Report;
import br.com.beautystyle.retrofit.webclient.ReportWebClient;
import br.com.beautystyle.util.CalendarUtil;

public class ReportRepository {

    @Inject
    ReportWebClient webClient;
    private final String profile;
    private final Long tenant;
    @Inject
    EventRxJava eventDao;
    @Inject
    ExpenseRxJava expenseDao;

    @Inject
    public ReportRepository(SharedPreferences preferences) {
        profile = preferences.getString(PROFILE_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public void getMonthlyReport(ResultsCallBack<List<Report>> callBack) {
        LocalDate startDate = CalendarUtil.selectedDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        getReportByPeriod(startDate, endDate, callBack);
    }

    public void getReportByPeriod(LocalDate startDate,
                                  LocalDate endDate,
                                  ResultsCallBack<List<Report>> callBack) {
        getReportByPeriodFromRoom(startDate, endDate, callBack);
        if (isUserPremium()) {
            getReportByPeriodFromApi(startDate, endDate, callBack);
        }
    }

    private void getReportByPeriodFromRoom(LocalDate startDate,
                                           LocalDate endDate,
                                           ResultsCallBack<List<Report>> callBack) {
        eventDao.getByPeriod(startDate, endDate, tenant).doOnSuccess(events -> {
            List<Report> reports = new ArrayList<>();
            events.forEach(event -> reports.add(new Report(event)));
            expenseDao.getByPeriod(startDate, endDate, tenant).doOnSuccess(expenses -> {
                expenses.forEach(expense -> reports.add(new Report(expense)));
                callBack.onSuccess(reports);
            }).subscribe();
        }).subscribe();
    }

    private void getReportByPeriodFromApi(LocalDate startDate, LocalDate endDate, ResultsCallBack<List<Report>> callBack) {
        webClient.getReportByPeriod(startDate, endDate,
                new ResultsCallBack<List<Report>>() {
                    @Override
                    public void onSuccess(List<Report> result) {
                        callBack.onSuccess(result);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                });
    }

    public LiveData<Resource<List<String>>> getYearsList() {
        MutableLiveData<Resource<List<String>>> liveData = new MutableLiveData<>();
        getYearsListFromRoom(liveData);
        if (isUserPremium()) {
            getYearsListFromApi(liveData);
        }
        return liveData;
    }

    private void getYearsListFromRoom(MutableLiveData<Resource<List<String>>> liveData) {
        eventDao.getYearsList(tenant).doOnSuccess(eventDates -> {
            Set<String> years = new ArraySet<>();
            eventDates.forEach(date -> years.add(String.valueOf(date.getYear())));
            expenseDao.getYearsList(tenant).doOnSuccess(expenseDates -> {
                expenseDates.forEach(date -> years.add(String.valueOf(date.getYear())));
                liveData.setValue(new Resource<>(new ArrayList<>(years), null));
            }).subscribe();
        }).subscribe();
    }

    private void getYearsListFromApi(MutableLiveData<Resource<List<String>>> liveData) {
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
    }

    public void getReportByDate(ResultsCallBack<List<Report>> callBack) {
        getByDateFromRoom(callBack);
        if(isUserPremium()){
            getByDateFromApi(callBack);
        }
    }

    private void getByDateFromRoom(ResultsCallBack<List<Report>> callBack) {
        eventDao.getByDate(tenant).doOnSuccess(eventsFromRoom->{
            List<Report> reports = new ArrayList<>();
            eventsFromRoom.forEach(event -> reports.add(new Report(event)));
            expenseDao.getByDate(tenant).doOnSuccess(expenses -> {
                expenses.forEach(expense -> reports.add(new Report(expense)));
                callBack.onSuccess(reports);
            }).subscribe();
        }).subscribe();
    }

    private void getByDateFromApi(ResultsCallBack<List<Report>> callBack) {
        webClient.getReportByDate(CalendarUtil.selectedDate,
                new ResultsCallBack<List<Report>>() {
                    @Override
                    public void onSuccess(List<Report> result) {
                        callBack.onSuccess(result);
                    }

                    @Override
                    public void onError(String error) {
                        callBack.onError(error);
                    }
                });
    }

    private boolean isUserPremium() {
        return profile.equals(USER_PREMIUM);
    }
}
