package br.com.beautystyle.repository;

import android.content.Context;

import java.util.List;
import java.util.stream.Collectors;

import br.com.beautystyle.database.room.BeautyStyleDatabase;
import br.com.beautystyle.database.room.dao.RoomEventWithJobsDao;
import br.com.beautystyle.database.room.references.EventWithJobs;
import br.com.beautystyle.model.entities.Event;
import br.com.beautystyle.model.entities.EventJobCroosRef;
import br.com.beautystyle.model.entities.Job;
import br.com.beautystyle.util.CreateListsUtil;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class EventWithJobRepository {

    private final RoomEventWithJobsDao dao;

    public EventWithJobRepository(Context context) {
        dao = BeautyStyleDatabase.getInstance(context).getRoomEventWithJobDao();
    }

    public void update(Long eventId, List<Job> jobList) {
        getById(eventId,jobList);
    }

    private void getById(Long eventId, List<Job> jobList) {
        dao.getById(eventId)
                .subscribeOn(Schedulers.io())
                .doOnSuccess(eventJobCroosRefs -> {
                    eventJobCroosRefs = updateData(jobList,eventId);
                    update(eventJobCroosRefs);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    private List<EventJobCroosRef> updateData(List<Job> jobList, Long eventId) {
        return jobList.stream()
                .map((Job job) -> new EventJobCroosRef(eventId, job.getJobId()))
                .collect(Collectors.toList());
    }

    private void update(List<EventJobCroosRef> eventJobCroosRefs) {
        dao.update(eventJobCroosRefs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void insert(Event event, List<Job> jobList) {
        List<EventJobCroosRef> newEventJobCroosRefs = CreateListsUtil.createNewJobList(event.getEventId(), jobList);
        dao.insert(newEventJobCroosRefs)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public void insertAll(List<EventWithJobs> eventWithJobs, ResultsCallBack<Void> callBack) {
        for (EventWithJobs evJob : eventWithJobs) {
            insert(evJob.getEvent(),evJob.getJobList());
        }
        callBack.onSuccess(null);
    }
}
