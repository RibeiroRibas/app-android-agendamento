package br.com.beautystyle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDate;
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

    public LiveData<Resource<List<EventWithClientAndJobs>>> getByDateFromRoomLiveData() {
        return repository.getByDateFromRoom();
    }

    public LiveData<Resource<List<EventWithClientAndJobs>>> getAllByDateFromApiLiveData(
            LocalDate selectedDate) {
        return repository.getByDateFromApi(selectedDate);
    }

    public LiveData<Resource<EventWithClientAndJobs>> insertOnApi(EventWithClientAndJobs event) {
        return repository.insertOnApi(event);
    }

    public LiveData<Resource<Void>> updateOnApi(EventWithClientAndJobs event) {
        return repository.updateOnApi(event);
    }

    public LiveData<Resource<Void>> deleteOnApi(Event event) {
        return repository.deleteOnApi(event);
    }

    public LiveData<Resource<List<Report>>> getReportByPeriod() {
        return  repository.getReportByPeriod();
    }

    public LiveData<Resource<Void>> insertOnRoom(EventWithClientAndJobs event) {
        return repository.insertOnRoom(event);
    }

    public LiveData<Resource<Void>> deleteOnRoom(Event event) {
        return repository.deleteOnRoom(event);
    }

    public LiveData<Resource<Void>> updateOnRoom(EventWithClientAndJobs event) {
        return repository.updateOnRoom(event);
    }
}
