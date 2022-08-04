package br.com.beautystyle.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.model.Report;
import br.com.beautystyle.retrofit.webclient.ReportWebClient;
import br.com.beautystyle.util.CalendarUtil;

public class ReportRepository {

    @Inject
    ReportWebClient webClient;

    @Inject
    public ReportRepository() {
    }

    public void getMonthlyReport(ResultsCallBack<List<Report>> callBack) {
        LocalDate startDate = CalendarUtil.selectedDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        getReportByPeriod(startDate, endDate, callBack);
    }

    public void getReportByPeriod(LocalDate startDate, LocalDate endDate, ResultsCallBack<List<Report>> callBack) {
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
        webClient.getYearsList(new ResultsCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                liveData.setValue(new Resource<>(result,null));
            }

            @Override
            public void onError(String error) {
                liveData.setValue(new Resource<>(null, error));
            }
        });
        return liveData;
    }

    public void getReportByDate(ResultsCallBack<List<Report>> callBack) {
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
}
