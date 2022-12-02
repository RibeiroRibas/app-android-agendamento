package br.com.beautystyle.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.beautystyle.model.entity.Customer;
import br.com.beautystyle.repository.ClientRepository;
import br.com.beautystyle.repository.Resource;

public class CostumerViewModel extends ViewModel {

    private final ClientRepository repository;


    public CostumerViewModel(ClientRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<Customer>>> getAllLiveData() {
        return repository.getAllLiveData();
    }

    public LiveData<List<Customer>> getContactListFromSmartphone(Context context) {
        return repository.getContactListFromSmartphone(context);
    }

}
