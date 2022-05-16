package br.com.beautystyle.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.model.entities.Client;
import br.com.beautystyle.model.entities.Job;

public class EventViewModel extends AndroidViewModel {

    private MutableLiveData<Client> clientLive;
    private MutableLiveData<List<Job>> serviceListLiveData;
    private MutableLiveData<LocalDate> eventDate;

    public EventViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<LocalDate> getEventDate(){
        if(eventDate ==null){
            eventDate = new MutableLiveData<>();
        }
        return eventDate;
    }

    public void add(LocalDate date){
        this.eventDate.setValue(date);
    }

    public MutableLiveData<List<Job>> getServiceListLiveData(){
        if(serviceListLiveData ==null){
            serviceListLiveData = new MutableLiveData<>();
        }
        return serviceListLiveData;
    }

    public void add(List<Job> serviceList){
        this.serviceListLiveData.setValue(serviceList);
    }

    public MutableLiveData<Client> getClientLive(){
        if(clientLive==null){
            clientLive = new MutableLiveData<>();
        }
        return clientLive;
    }

    public void add(Client client){clientLive.setValue(client);}

}
