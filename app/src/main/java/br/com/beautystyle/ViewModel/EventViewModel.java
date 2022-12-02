package br.com.beautystyle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.Customer;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.repository.EventRepository;
import br.com.beautystyle.repository.Resource;
import br.com.beautystyle.retrofit.model.dto.EventWithClientAndJobsDto;

public class EventViewModel extends ViewModel {

    private MutableLiveData<Customer> clientLiveData;
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

    public LiveData<Customer> getClientLiveData() {
        if (clientLiveData == null) {
            clientLiveData = new MutableLiveData<>();
        }
        return clientLiveData;
    }

    public void add(Customer customer) {
        clientLiveData.setValue(customer);
    }

    public LiveData<Resource<EventWithClientAndJobsDto>> getByDateLiveData() {
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

}
