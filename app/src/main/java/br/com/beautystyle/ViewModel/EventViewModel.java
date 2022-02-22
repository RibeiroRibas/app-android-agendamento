package br.com.beautystyle.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.data.repository.EventRepository;
import br.com.beautystyle.domain.model.Client;
import br.com.beautystyle.domain.model.Event;
import br.com.beautystyle.domain.model.Services;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class EventViewModel extends AndroidViewModel {

    private final EventRepository eventRepository;
    private MutableLiveData<Client> clientLive;
    private MutableLiveData<List<Services>> serviceListLiveData;
    private MutableLiveData<LocalDate> eventDate;

    public MutableLiveData<LocalDate> getEventDate(){
        if(eventDate ==null){
            eventDate = new MutableLiveData<>();
        }
        return eventDate;
    }

    public void add(LocalDate date){
        this.eventDate.setValue(date);
    }

    public MutableLiveData<List<Services>> getServiceListLiveData(){
        if(serviceListLiveData ==null){
            serviceListLiveData = new MutableLiveData<>();
        }
        return serviceListLiveData;
    }

    public void add(List<Services> serviceList){
        this.serviceListLiveData.setValue(serviceList);
    }

    public MutableLiveData<Client> getClientLive(){
        if(clientLive==null){
            clientLive = new MutableLiveData<>();
        }
        return clientLive;
    }

    public void add(Client client){clientLive.setValue(client);}

    public EventViewModel(@NonNull Application application) {
        super(application);
        eventRepository = new EventRepository(application);
    }

    public Single<Long> insert(Event event){
        return eventRepository.insert(event);
    }

    public Completable delete(Event chosedEvent) {
        return eventRepository.delete(chosedEvent);
    }

    public Completable update(Event event){
       return eventRepository.update(event);
    }

    public Observable<List<Event>> getByDate(LocalDate date){
        return eventRepository.getByDate(date);
    }

    public Single<List<Event>> getAll(){
        return eventRepository.getAll();
    }
}
