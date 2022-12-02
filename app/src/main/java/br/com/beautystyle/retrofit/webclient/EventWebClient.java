package br.com.beautystyle.retrofit.webclient;

import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import javax.inject.Inject;

import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.retrofit.callback.CallBackReturn;
import br.com.beautystyle.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.retrofit.model.dto.EventWithClientAndJobsDto;
import br.com.beautystyle.retrofit.model.form.EventForm;
import br.com.beautystyle.retrofit.service.EventService;
import br.com.beautystyle.util.CalendarUtil;
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

    public void insert(EventForm event,
                       ResultsCallBack<EventWithClientAndJobs> callBack) {
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

    public void update(EventForm event,
                       ResultsCallBack<Void> callBack) {
        Call<Void> callUpdate = service.update(event.getEvent().getApiId(), event, token);
        callUpdate.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(null);
                    }

                    @Override
                    public void onError(String erro) {
                        callBack.onError(erro);
                    }
                })
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

    public void getAllByDate(ResultsCallBack<EventWithClientAndJobsDto> callBack) {

        Call<EventWithClientAndJobsDto> callByDate =
                service.getByDate(CalendarUtil.selectedDate, token);
        callByDate.enqueue(new CallBackReturn<>(
                        new CallBackReturn.CallBackResponse<EventWithClientAndJobsDto>() {
                            @Override
                            public void onSuccess(EventWithClientAndJobsDto eventListDto) {
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

}
