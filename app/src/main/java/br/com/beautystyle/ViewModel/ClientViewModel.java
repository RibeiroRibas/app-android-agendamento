package br.com.beautystyle.ViewModel;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import br.com.beautystyle.data.repository.ClientRepository;
import br.com.beautystyle.domain.model.Client;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ClientViewModel extends AndroidViewModel {

    private final ClientRepository repository;
    @SuppressLint("StaticFieldLeak")
    private final Context context;

    public ClientViewModel(@NonNull Application application) {
        super(application);
        repository = new ClientRepository(application);
        this.context = application;
    }

    public Single<Client> getClientById(int id) {
        return repository.getById(id).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());
    }


    public Completable delete(Client clientAtPosition) {
       return  repository.delete(clientAtPosition);
    }

    public Completable update(Client client) {
        return repository.update(client);
    }

    public Single<Long> insert(Client client) {
        return repository.insert(client);
    }

    public Single<List<Client>> getAll() {
        return repository.getAll().observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io());
    }

    public Single<List<Client>> getContactListFromSmartPhone() {
        return repository.getContactListFromSmartphone(context);
    }

    public Single<Long[]> saveAllImportedClients(List<Client> contactList) {
        return repository.insertAll(contactList);
    }
}
