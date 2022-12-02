package br.com.beautystyle.retrofit.webclient;

import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.model.util.Report;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.retrofit.callback.CallBackReturn;
import br.com.beautystyle.retrofit.service.ReportService;
import retrofit2.Call;

public class ReportWebClient {
    @Inject
    ReportService service;
    private final String token;
    private final Long tenant;

    @Inject
    public ReportWebClient(SharedPreferences preferences) {
        token = preferences.getString(TOKEN_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public void getReportByPeriod(LocalDate startDate, LocalDate endDate,
                                  ResultsCallBack<List<Report>> callBack) {
        Call<List<Report>> callReportByPeriod =
                service.getReportByPeriod(startDate, endDate, tenant, token);
        callReportByPeriod.enqueue(new CallBackReturn<>(
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

    public void getYearsList(ResultsCallBack<List<String>> callBack) {
        Call<List<String>> callYearsList = service.getYearsList(tenant, token);
        callYearsList.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<String>>() {
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

    public void getReportByDate( LocalDate date, ResultsCallBack<List<Report>> callBack) {
        Call<List<Report>> callReportByPeriod =
                service.getReportByDate(date, tenant, token);
        callReportByPeriod.enqueue(new CallBackReturn<>(
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
