package br.com.beautystyle.retrofit.webclient;

import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.model.Report;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.retrofit.callback.CallBackReturn;
import br.com.beautystyle.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.retrofit.service.EventService;
import retrofit2.Call;

public class EventWebClient {

    @Inject
    EventService service;
    private final String token;
    private final Long tenant;


    @Inject
    public EventWebClient(SharedPreferences preferences) {
        token = preferences.getString(TOKEN_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public void insert(EventWithClientAndJobs event,
                       ResultsCallBack<EventWithClientAndJobs> callBack) {
            event.getEvent().setCompanyId(tenant);
            Call<EventWithClientAndJobs> callInsert = service.insert(event, token);
            callInsert.enqueue(new CallBackReturn<>(
                            new CallBackReturn.CallBackResponse<EventWithClientAndJobs>() {
                                @Override
                                public void onSuccess(EventWithClientAndJobs response) {
                                    callBack.onSuccess(response);
                                }

                                @Override
                                public void onError(String error) {
                                    callBack.onError(error);
                                }
                            }
                    )
            );
    }

    public void update(EventWithClientAndJobs event,
                       ResultsCallBack<Void> callBack) {
        Call<Void> callUpdate = service.update(event, token);
        callUpdate.enqueue(new CallBackWithoutReturn(
                        new CallBackWithoutReturn.CallBackResponse() {
                            @Override
                            public void onSuccess() {
                                callBack.onSuccess(null);
                            }

                            @Override
                            public void onError(String erro) {
                                callBack.onError(erro);
                            }
                        }
                )
        );
    }

    public void delete(Long eventId, ResultsCallBack<Void> callBack) {
        Call<Void> callDelete = service.delete(eventId, token);
        callDelete.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
            @Override
            public void onSuccess() {
                callBack.onSuccess(null);
            }

            @Override
            public void onError(String erro) {
                callBack.onError(erro);
            }
        }));
    }

    public void getAllByDate(LocalDate date,
                             ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
            Call<List<EventWithClientAndJobs>> callByDate =
                    service.getByDate(date, tenant, token);
            callByDate.enqueue(new CallBackReturn<>(
                            new CallBackReturn.CallBackResponse<List<EventWithClientAndJobs>>() {
                                @Override
                                public void onSuccess(List<EventWithClientAndJobs> eventListDto) {
                                    callBack.onSuccess(eventListDto);
                                }

                                @Override
                                public void onError(String error) {
                                    callBack.onError(error);
                                }
                            }
                    )
            );
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

    public void getReportByPeriod(LocalDate startDate, LocalDate endDate,
                                         ResultsCallBack<List<Report>> callBack) {
        Call<List<Report>> callReport = service.getReportByPeriod(startDate, endDate, tenant, token);
        callReport.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<Report>>() {
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

    public void getEventReportByDate(LocalDate selectedDate,
                                            ResultsCallBack<List<Report>> callBack) {
        Call<List<Report>> callReport = service.getReportByDate(selectedDate, tenant, token);
        callReport.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<Report>>() {
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
