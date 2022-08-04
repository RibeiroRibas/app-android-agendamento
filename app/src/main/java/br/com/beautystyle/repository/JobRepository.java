package br.com.beautystyle.repository;

import static br.com.beautystyle.repository.ConstantsRepository.PROFILE_SHARED_PREFERENCES;
import static br.com.beautystyle.repository.ConstantsRepository.TENANT_SHARED_PREFERENCES;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.com.beautystyle.database.rxjavaassinc.JobAsynchDao;
import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.retrofit.webclient.JobWebClient;

public class JobRepository {

    @Inject
    JobAsynchDao dao;
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
        dao.getAll().doOnSuccess(jobsFromRoom -> {
            if (callBack == null)
                deleteFromRoomIfNotExistOnApi(jobsFromApi, jobsFromRoom);
            mergeJobId(jobsFromRoom, jobsFromApi);
            List<Job> jobsToUpdate = getJobsToUpdate(jobsFromApi);
            dao.updateAll(jobsToUpdate).doOnComplete(() -> {
                List<Job> newJobs = getJobsToInsert(jobsFromApi);
                newJobs.forEach(job -> job.setJobId(null));
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

    public LiveData<Resource<List<Job>>> getAllLiveData() {
        dao.getAllLiveData().doOnNext(jobs -> {
                    Resource<List<Job>> resource = new Resource<>(jobs, null);
                    mutableLiveData.setValue(resource);
                }).doOnError(error ->
                        mutableLiveData.setValue(new Resource<>(null, error.getMessage())))
                .subscribe();
        return mutableLiveData;
    }

    public void getAllFromApi() {
        if(isUserPremium()){
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
    }

    public void insertOnApi(Job job) {
        if(isUserPremium()){
            webClient.insert(job, new ResultsCallBack<Job>() {
                @Override
                public void onSuccess(Job result) {
                    result.setJobId(null);
                    dao.insert(result).subscribe();
                }

                @Override
                public void onError(String error) {
                    mutableLiveData.setValue(new Resource<>(null, error));
                }
            });
        }
    }

    public void update(Job job) {
        webClient.update(job, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                dao.update(job).subscribe();
            }

            @Override
            public void onError(String error) {
                mutableLiveData.setValue(new Resource<>(null, error));
            }
        });
    }

    public void delete(Job job) {
        webClient.delete(job, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                dao.delete(job).subscribe();
            }

            @Override
            public void onError(String error) {
                mutableLiveData.setValue(new Resource<>(null, error));
            }
        });
    }

    private boolean isFreeUser() {
        return profile.equals("ROLE_FREE_ACCOUNT");
    }

    private boolean isUserPremium() {
        return profile.equals("ROLE_PROFISSIONAL");
    }

    public void insertOnRoom(Job job) {
        if(isFreeUser()){
            job.setJobId(null);
            job.setCompanyId(tenant);
            dao.insert(job).subscribe();
        }
    }
}
