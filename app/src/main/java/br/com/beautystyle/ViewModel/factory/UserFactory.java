package br.com.beautystyle.ViewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import br.com.beautystyle.ViewModel.UserViewModel;
import br.com.beautystyle.repository.UserRepository;

public class UserFactory implements ViewModelProvider.Factory {

    private final UserRepository repository;

    public UserFactory(UserRepository repository) {
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new UserViewModel(repository);
    }
}
