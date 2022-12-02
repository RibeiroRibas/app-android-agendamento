package br.com.beautystyle.repository;

import static br.com.beautystyle.repository.ConstantsRepository.FREE_ACCOUNT;
import static br.com.beautystyle.repository.ConstantsRepository.PROFILE_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.USER_PREMIUM;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.com.beautystyle.database.rxjava.JobRxJava;
import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.retrofit.webclient.JobWebClient;

public class JobRepository {

    @Inject
    JobRxJava dao;
    @Inject
    JobWebClient webClient;
    private final MutableLiveData<Resource<List<Job>>> mutableLiveData = new MutableLiveData<>();
    private final String profile;
    private final Long tenant;

    @Inject
    public JobRepository(SharedPreferences preferences) {
        profile = preferences.getString(PROFILE_SHARED_PREFERENCES, "");
        tenant = preferences.getLong(TENANT_SHARED_PREFERENCES, 0);
    }

    public void updateJobs(List<Job> jobsFromApi, ResultsCallBack<List<Job>> callBack) {
        dao.getAll(tenant).doOnSuccess(jobsFromRoom -> {
            if (callBack == null)
                deleteFromRoomIfNotExistOnApi(jobsFromApi, jobsFromRoom);
            mergeJobId(jobsFromRoom, jobsFromApi);
            List<Job> jobsToUpdate = getJobsToUpdate(jobsFromApi);
            dao.updateAll(jobsToUpdate).doOnComplete(() -> {
                List<Job> newJobs = getJobsToInsert(jobsFromApi);
                newJobs.forEach(job -> job.setId(null));
                dao.insertAll(newJobs).doOnSuccess(idList -> {
                    if (callBack != null) {
                        setJobsIds(newJobs, idList);
                        mergeJobId(newJobs, jobsFromApi);
                        callBack.onSuccess(jobsFromApi);
                    }
                }).subscribe();
            }).subscribe();
        }).subscribe();
    }

    private void deleteFromRoomIfNotExistOnApi(List<Job> jobsFromApi,
                                               List<Job> jobsFromRoom) {
        jobsFromRoom.forEach(fromRoom -> {
            if (fromRoom.isNotExistOnApi(jobsFromApi))
                dao.delete(fromRoom).subscribe();
        });
    }

    private void setJobsIds(List<Job> newJobs, List<Long> idList) {
        for (int i = 0; i < newJobs.size(); i++) {
            newJobs.get(i).setId(idList.get(i));
        }
    }

    @NonNull
    private List<Job> getJobsToInsert(List<Job> jobsFromApi) {
        return jobsFromApi.stream()
                .filter(job -> !job.isIdNotNull())
                .collect(Collectors.toList());
    }

    @NonNull
    private List<Job> getJobsToUpdate(List<Job> jobsFromApi) {
        return jobsFromApi.stream()
                .filter(Job::isIdNotNull)
                .collect(Collectors.toList());
    }

    private void mergeJobId(List<Job> jobsFromRoom, List<Job> jobsFromApi) {
        jobsFromApi.forEach(jobApi ->
                jobsFromRoom.forEach(jobRoom -> {
                    if (jobRoom.isApiIdEquals(jobApi)) {
                        jobApi.setId(jobRoom.getId());
                    }
                })
        );
    }

    public LiveData<Resource<List<Job>>> getAllLiveData() {
        getAllFromRoomObservable();
        if (isUserPremium()) {
            getAllFromApi();
        }
        return mutableLiveData;
    }

    private void getAllFromRoomObservable() {
        dao.getAllLiveData(tenant).doOnNext(jobs -> {
                    Resource<List<Job>> resource = new Resource<>(jobs, null);
                    mutableLiveData.setValue(resource);
                }).doOnError(error ->
                        mutableLiveData.setValue(new Resource<>(null, error.getMessage())))
                .subscribe();
    }

    private void getAllFromApi() {
        webClient.getAll(new ResultsCallBack<List<Job>>() {
            @Override
            public void onSuccess(List<Job> jobs) {
                updateJobs(jobs, null);
            }

            @Override
            public void onError(String error) {
                mutableLiveData.setValue(new Resource<>(null, error));
            }
        });
    }

    private void insertOnApi(Job job) {
        webClient.insert(job, new ResultsCallBack<Job>() {
            @Override
            public void onSuccess(Job result) {
                insertOnRoom(result);
            }

            @Override
            public void onError(String error) {
                mutableLiveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public void update(Job job) {
        if (isUserPremium()) {
            updateOnApi(job);
        }
        if (isFreeAccount()) {
            updateOnRoom(job);
        }
    }

    private void updateOnRoom(Job job) {
        dao.update(job).subscribe();
    }

    private void updateOnApi(Job job) {
        webClient.update(job, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                updateOnRoom(job);
            }

            @Override
            public void onError(String error) {
                mutableLiveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public void delete(Job job) {
        if(isUserPremium()){
            deleteOnApi(job);
        }
        if(isFreeAccount()){
            deleteOnRoom(job);
        }
    }

    private void deleteOnRoom(Job job) {
        dao.delete(job).subscribe();
    }

    private void deleteOnApi(Job job) {
        webClient.delete(job, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                deleteOnRoom(job);
            }

            @Override
            public void onError(String error) {
                mutableLiveData.setValue(new Resource<>(null, error));
            }
        });
    }

    private boolean isFreeAccount() {
        return profile.equals(FREE_ACCOUNT);
    }

    private boolean isUserPremium() {
        return profile.equals(USER_PREMIUM);
    }

    public void insert(Job job) {
        if (isUserPremium()) {
            insertOnApi(job);
        }
        if (isFreeAccount()) {
            job.setTenant(tenant);
            insertOnRoom(job);
        }
    }

    private void insertOnRoom(Job job) {
        dao.insert(job).subscribe();
    }
}
