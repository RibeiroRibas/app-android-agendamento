package br.com.beautystyle.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import br.com.beautystyle.data.database.references.EventServiceCroosRef;
import br.com.beautystyle.data.database.references.EventWithServices;
import br.com.beautystyle.data.repository.EventWithServicesRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class EventWithServicesViewModel extends AndroidViewModel {

    private final EventWithServicesRepository repository;

    public EventWithServicesViewModel(@NonNull Application application) {
        super(application);
        repository = new EventWithServicesRepository(application);
    }

    public Completable insert(List<EventServiceCroosRef> eventServiceCroosRef){
        return repository.insert(eventServiceCroosRef);
    }

    public Completable delete(List<EventServiceCroosRef> eventServiceCroosRefs) {
        return repository.delete(eventServiceCroosRefs);
    }

    public Single<List<EventServiceCroosRef>> getByEventId(long editedEventId) {
        return repository.getById(editedEventId);
    }

    public Single<List<EventWithServices>> getEventWithServices(){
        return repository.getEventWithServices();
    }
}
