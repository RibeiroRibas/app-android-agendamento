package br.com.beautystyle.retrofit.webclient;

import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.retrofit.callback.CallBackReturn;
import br.com.beautystyle.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.retrofit.service.JobService;
import retrofit2.Call;

public class JobWebClient {

    @Inject
    JobService service;
    private final String token;
    private final Long tenant;

    @Inject
    public JobWebClient(SharedPreferences preferences) {
        token = preferences.getString(TOKEN_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public void getAll(ResultsCallBack<List<Job>> callBack) {
        Call<List<Job>> callJobs = service.getAll(token);
        callJobs.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<Job>>() {
            @Override
            public void onSuccess(List<Job> response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public void insert(Job job, ResultsCallBack<Job> callBack) {
        Call<Job> callJob = service.insert(job, token);
        callJob.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<Job>() {
            @Override
            public void onSuccess(Job response) {
                callBack.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public void update(Job job, ResultsCallBack<Void> callBack) {
        Call<Void> callJob = service.update(job.getApiId(), job, token);
        callJob.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
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

    public void delete(Job job, ResultsCallBack<Void> callBack) {
        Call<Void> callJob = service.delete(job.getApiId(), token);
        callJob.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
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
}
