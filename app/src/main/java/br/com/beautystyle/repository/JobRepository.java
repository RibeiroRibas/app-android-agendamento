package br.com.beautystyle.repository;

import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TOKEN_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.com.beautystyle.database.retrofit.callback.CallBackReturn;
import br.com.beautystyle.database.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.database.retrofit.service.JobService;
import br.com.beautystyle.database.room.BeautyStyleDatabase;
import br.com.beautystyle.database.room.dao.RoomJobDao;
import br.com.beautystyle.model.entity.Job;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;

public class JobRepository {

    private final RoomJobDao dao;
    @Inject
    JobService service;
    private final String token;
    private final Long tenant;

    @Inject
    public JobRepository(BeautyStyleDatabase localDatabase, SharedPreferences preferences) {
        dao = localDatabase.getRoomJobDao();
        token = preferences.getString(TOKEN_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }


    public void insertOnApi(Job job, ResultsCallBack<Job> callBack) {
        job.setCompanyId(tenant);
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

    public Single<Long> insertOnRoom(Job job) {
        return dao.insert(job)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void updateOnApi(Job job, ResultsCallBack<Void> callBack) {
        Call<Void> callJob = service.update(job, token);
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

    public Completable updateOnRoom(Job job) {
        return dao.update(job)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public void deleteOnApi(Job job, ResultsCallBack<Void> callBack) {
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

    public Completable deleteOnRoom(Job job) {
        return dao.delete(job)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    public Single<List<Job>> getAllFromRoom() {
        return dao.getAll()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

    }

    public void getAllFromApi(ResultsCallBack<List<Job>> callBack) {
        Call<List<Job>> callJobs = service.getAllByCompanyId(tenant, token);
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

    public Single<List<Long>> insertAllOnRoom(List<Job> response) {
        return dao.insertAll(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Completable updateAllOnRoom(List<Job> updateJobs) {
        return dao.updateAll(updateJobs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void updatejobs(List<Job> jobsFromApi, ResultsCallBack<List<Job>> callBack) {
        getAllFromRoom().doOnSuccess(jobsFromRoom -> {
            mergeJobId(jobsFromRoom, jobsFromApi);
            List<Job> jobsToUpdate = getJobsToUpdate(jobsFromApi);
            updateAllOnRoom(jobsToUpdate).doOnComplete(() -> {
                List<Job> newJobs = getJobsToInsert(jobsFromApi);
                if (newJobs.isEmpty()) {
                    callBack.onSuccess(jobsFromApi);
                } else {
                    newJobs.forEach(job -> job.setJobId(null));
                    insertAllOnRoom(newJobs).doOnSuccess(idList -> {
                        setJobsIds(newJobs, idList);
                        mergeJobId(newJobs, jobsFromApi);
                        callBack.onSuccess(jobsFromApi);
                    }).subscribe();
                }
            }).subscribe();
        }).subscribe();
    }

    private void setJobsIds(List<Job> newJobs, List<Long> idList) {
        for (int i = 0; i < newJobs.size(); i++) {
            newJobs.get(i).setJobId(idList.get(i));
        }
    }

    @NonNull
    private List<Job> getJobsToInsert(List<Job> jobsFromApi) {
        return jobsFromApi.stream()
                .filter(job -> !job.checkId())
                .collect(Collectors.toList());
    }

    @NonNull
    private List<Job> getJobsToUpdate(List<Job> jobsFromApi) {
        return jobsFromApi.stream()
                .filter(Job::checkId)
                .collect(Collectors.toList());
    }

    private void mergeJobId(List<Job> jobsFromRoom, List<Job> jobsFromApi) {
        jobsFromApi.forEach(jobApi ->
                jobsFromRoom.forEach(jobRoom -> {
                    if (jobApi.getApiId().equals(jobRoom.getApiId())) {
                        jobApi.setJobId(jobRoom.getJobId());
                    }
                })
        );
    }
}
