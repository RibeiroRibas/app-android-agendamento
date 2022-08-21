package br.com.beautystyle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.model.Report;
import br.com.beautystyle.model.entity.Costumer;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.repository.EventRepository;
import br.com.beautystyle.repository.Resource;

public class EventViewModel extends ViewModel {

    private MutableLiveData<Costumer> clientLiveData;
    private MutableLiveData<List<Job>> jobsLiveData;
    private final EventRepository repository;

    public EventViewModel(EventRepository repository) {
        this.repository = repository;
    }

    public LiveData<List<Job>> getJobsLiveData() {
        if (jobsLiveData == null) {
            jobsLiveData = new MutableLiveData<>();
        }
        return jobsLiveData;
    }

    public void add(List<Job> serviceList) {

        this.jobsLiveData.setValue(serviceList);
    }

    public LiveData<Costumer> getClientLiveData() {
        if (clientLiveData == null) {
            clientLiveData = new MutableLiveData<>();
        }
        return clientLiveData;
    }

    public void add(Costumer costumer) {
        clientLiveData.setValue(costumer);
    }

    public LiveData<Resource<List<EventWithClientAndJobs>>> getByDateLiveData() {
        return repository.getByDateLiveData();
    }

    public LiveData<Resource<EventWithClientAndJobs>> insert(EventWithClientAndJobs event) {
        return repository.insert(event);
    }

    public LiveData<Resource<Void>> update(EventWithClientAndJobs event) {
        return repository.update(event);
    }

    public LiveData<Resource<Void>> delete(Event event) {
        return repository.delete(event);
    }

    public LiveData<Resource<List<Report>>> getReportByPeriod() {
        return  repository.getReportByPeriod();
    }
}
