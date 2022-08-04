package br.com.beautystyle.ViewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import br.com.beautystyle.ViewModel.CostumerViewModel;
import br.com.beautystyle.repository.ClientRepository;

public class CostumerFactory implements ViewModelProvider.Factory {

    private final ClientRepository repository;

    public CostumerFactory(ClientRepository clientRepository) {
        this.repository = clientRepository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new CostumerViewModel(repository);
    }
}
