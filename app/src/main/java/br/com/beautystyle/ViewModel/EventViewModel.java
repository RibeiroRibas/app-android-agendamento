package br.com.beautystyle.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import br.com.beautystyle.model.entity.Client;
import br.com.beautystyle.model.entity.Job;

public class EventViewModel extends AndroidViewModel {

    private MutableLiveData<Client> clientLiveData;
    private MutableLiveData<List<Job>> jobsLiveData;

    public EventViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<Job>> getJobsLiveData() {
        if (jobsLiveData == null) {
            jobsLiveData = new MutableLiveData<>();
        }
        return jobsLiveData;
    }

    public void add(List<Job> serviceList) {
        this.jobsLiveData.setValue(serviceList);
    }

    public MutableLiveData<Client> getClientLiveData() {
        if (clientLiveData == null) {
            clientLiveData = new MutableLiveData<>();
        }
        return clientLiveData;
    }

    public void add(Client client) {
        clientLiveData.setValue(client);
    }

}
