package br.com.beautystyle.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import br.com.beautystyle.data.repository.ServiceRepository;
import br.com.beautystyle.domain.model.Services;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

public class ServiceViewModel extends AndroidViewModel {

    private final ServiceRepository repository;

    public ServiceViewModel(@NonNull Application application) {
        super(application);
        repository = new ServiceRepository(application);
    }

    public Completable update(Services service) {
        return repository.update(service);
    }

    public Completable insert(Services service) {
        return repository.insert(service);
    }

    public Completable delete(Services service) {
        return repository.delete(service);
    }

    public Observable<List<Services>> getAll() {
        return repository.getAll();
    }

}
