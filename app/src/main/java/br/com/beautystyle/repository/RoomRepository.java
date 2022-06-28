package br.com.beautystyle.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.com.beautystyle.database.room.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.Client;
import br.com.beautystyle.model.entity.EventJobCroosRef;
import br.com.beautystyle.model.entity.Job;

public class RoomRepository  {

    @Inject
    EventRepository eventRepository;
    @Inject
    ClientRepository clientRepository;
    @Inject
    JobRepository jobRepository;
    @Inject
    EventWithJobRepository eventWithJobRepository;

    @Inject
    public RoomRepository (){
    }

    public void updateLocalDatabase(List<EventWithClientAndJobs> events,
                                    ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        //update clients -> update jobs -> update events
        updateClients(events, callBack);
    }

    private void updateClients(List<EventWithClientAndJobs> events,
                               ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        List<Client> clients = getClients(events);
        clientRepository.updateClients(clients,
                new ResultsCallBack<List<Client>>() {
                    @Override
                    public void onSuccess(List<Client> clients) {
                        setClientIdOnEvents(clients, events);
                        updateJobs(events, callBack);
                    }

                    @Override
                    public void onError(String erro) {
                        callBack.onError(erro);
                    }

                });
    }

    @NonNull
    private List<Client> getClients(List<EventWithClientAndJobs> events) {
        return events.stream()
                .map(EventWithClientAndJobs::getClient)
                .distinct()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private void setClientIdOnEvents(List<Client> clients, List<EventWithClientAndJobs> events) {
        events.forEach(event ->
                clients.forEach(client -> {
                    try {
                        if (event.getClient().getApiId().equals(client.getApiId())) {
                            event.getClient().setClientId(client.getClientId());
                            event.getEvent().setClientCreatorId(client.getClientId());
                        }
                    } catch (Exception erro) {
                        Log.i(TAG, "eventApiId Null: " + erro);
                    }

                })
        );
    }

    private void updateJobs(List<EventWithClientAndJobs> events,
                            ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        List<Job> jobsFromApi = getJobsFromApi(events);
        jobRepository.updatejobs(jobsFromApi,
                new ResultsCallBack<List<Job>>() {
                    @Override
                    public void onSuccess(List<Job> jobs) {
                        setJobIdsOnEvents(jobs, events);
                        updateEvents(events, callBack);
                    }

                    @Override
                    public void onError(String erro) {
                        callBack.onError(erro);
                    }
                });
    }

    private List<Job> getJobsFromApi(List<EventWithClientAndJobs> eventListFromApi) {
        return eventListFromApi.stream()
                .map(EventWithClientAndJobs::getJobs)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private void setJobIdsOnEvents(List<Job> jobs, List<EventWithClientAndJobs> events) {
        events.forEach(event ->
                event.getJobs().forEach(jobFromApi ->
                        jobs.forEach(job -> {
                            if (jobFromApi.getApiId().equals(job.getApiId())) {
                                jobFromApi.setJobId(job.getJobId());
                            }
                        })
                )
        );
    }

    private void updateEvents(List<EventWithClientAndJobs> events,
                              ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        eventRepository.updateAll(events,
                new ResultsCallBack<List<EventWithClientAndJobs>>() {
                    @Override
                    public void onSuccess(List<EventWithClientAndJobs> events) {
                        updateEventWithJobs(events, callBack);
                    }

                    @Override
                    public void onError(String erro) {
                        callBack.onError(erro);
                    }
                });

    }

    private void updateEventWithJobs(List<EventWithClientAndJobs> events,
                                     ResultsCallBack<List<EventWithClientAndJobs>> callBack) {
        List<EventJobCroosRef> eventJobCroosRefs = new ArrayList<>();
        events.forEach(event -> eventJobCroosRefs.addAll(getEventJobsCrossRefs(event)));
        updateEventWithJobsCrossRef(eventJobCroosRefs,
                new ResultsCallBack<Void>() {
                    @Override
                    public void onSuccess(Void resultado) {
                        callBack.onSuccess(events);
                    }

                    @Override
                    public void onError(String erro) {
                        callBack.onError(erro);
                    }
                }
        );
    }


    public void insertEvent(EventWithClientAndJobs event, ResultsCallBack<Void> callBack) {
        event.getEvent().setEventId(null);
        eventRepository.insertOnRoom(event.getEvent())
                .doOnSuccess(id -> {
                    event.getEvent().setEventId(id);
                    insertEventWithJobs(event, callBack);
                })
                .subscribe();
    }

    private void insertEventWithJobs(EventWithClientAndJobs events,
                                     ResultsCallBack<Void> callBack) {
        List<EventJobCroosRef> eventJobCroosRefs = getEventJobsCrossRefs(events);
        eventWithJobRepository.insert(eventJobCroosRefs)
                .doOnComplete(() -> callBack.onSuccess(null))
                .subscribe();
    }

    private List<EventJobCroosRef> getEventJobsCrossRefs(EventWithClientAndJobs event) {
        return event.getJobs().stream()
                .map(job -> new EventJobCroosRef(event.getEvent().getEventId(), job.getJobId()))
                .collect(Collectors.toList());
    }

    public void updateEvent(EventWithClientAndJobs event, ResultsCallBack<Void> callBack) {
        eventRepository.updateOnRoom(event.getEvent())
                .doOnComplete(() -> {
                            List<EventJobCroosRef> eventJobsCrossRefs = getEventJobsCrossRefs(event);
                            updateEventWithJobsCrossRef(eventJobsCrossRefs, callBack);
                        }
                ).subscribe();
    }

    private void updateEventWithJobsCrossRef(List<EventJobCroosRef> eventJobsCrossRefs,
                                             ResultsCallBack<Void> callBack) {
        eventWithJobRepository.deleteAllByIds(eventJobsCrossRefs)
                .doOnComplete(() ->
                        eventWithJobRepository
                                .insert(eventJobsCrossRefs)
                                .doOnComplete(() -> callBack.onSuccess(null))
                                .subscribe()
                ).subscribe();
    }

}

