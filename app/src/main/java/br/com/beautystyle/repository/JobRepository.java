package br.com.beautystyle.repository;

import android.content.Context;

import java.util.List;

import br.com.beautystyle.database.retrofit.BeautyStyleRetrofit;
import br.com.beautystyle.database.retrofit.callback.CallBackReturn;
import br.com.beautystyle.database.retrofit.callback.CallBackWithoutReturn;
import br.com.beautystyle.database.retrofit.service.JobService;
import br.com.beautystyle.database.room.BeautyStyleDatabase;
import br.com.beautystyle.database.room.dao.RoomJobDao;
import br.com.beautystyle.model.entities.Job;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;

public class JobRepository {

    private final RoomJobDao dao;
    private final JobService service;

    public JobRepository(Context context) {
        dao = BeautyStyleDatabase.getInstance(context).getRoomJobDao();
        service = new BeautyStyleRetrofit().getJobService();
    }

    public void insert(Job job, ResultOnError callBack) {
        insertOnApi(job, callBack);
    }

    private void insertOnApi(Job job, ResultOnError callBack) {
        Call<Job> callJob = service.insert(job);
        callJob.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<Job>() {
            @Override
            public void onSuccess(Job response) {
                insertOnRoom(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    private void insertOnRoom(Job job) {
        dao.insert(job)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void update(Job job, ResultOnError callBack) {
        updateOnApi(job, callBack);
    }

    private void updateOnApi(Job job, ResultOnError callBack) {
        Call<Job> callJob = service.update(job);
        callJob.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<Job>() {
            @Override
            public void onSuccess(Job response) {
                updateOnRoom(response);
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    private void updateOnRoom(Job job) {
        dao.update(job)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void delete(Job job, ResultOnError callBack) {
        deleteOnApi(job, callBack);
    }

    private void deleteOnApi(Job job, ResultOnError callBack) {
        Call<Void> callJob = service.delete(job.getJobId());
        callJob.enqueue(new CallBackWithoutReturn(new CallBackWithoutReturn.CallBackResponse() {
            @Override
            public void onSuccess() {
                deleteOnRoom(job);
            }

            @Override
            public void onError(String erro) {
                callBack.onError(erro);
            }
        }));
    }

    private void deleteOnRoom(Job job) {
        dao.delete(job)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public Disposable getAll(ResultsCallBack<List<Job>> callBack) {
        return getAllFromRoom(callBack);
    }

    private Disposable getAllFromRoom(ResultsCallBack<List<Job>> callBack) {
        return dao.getAll().observeOn(AndroidSchedulers.mainThread())
                .doOnNext(jobs -> {
                    callBack.onSuccess(jobs);
                    getAllFromApi(callBack);
                })
                .doOnError(throwable -> callBack.onError(throwable.getMessage()))
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

    private void getAllFromApi(ResultsCallBack<List<Job>> callBack) {
        Call<List<Job>> callJobs = service.getAll();
        callJobs.enqueue(new CallBackReturn<>(new CallBackReturn.CallBackResponse<List<Job>>() {
            @Override
            public void onSuccess(List<Job> response) {
                callBack.onSuccess(response);
                insertAllOnRoom(response).subscribe();
            }

            @Override
            public void onError(String error) {
                callBack.onError(error);
            }
        }));
    }

    public Completable insertAllOnRoom(List<Job> response) {
      return  dao.insertAll(response)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
