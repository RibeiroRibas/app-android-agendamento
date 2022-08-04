package br.com.beautystyle.ViewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import br.com.beautystyle.ViewModel.EventViewModel;
import br.com.beautystyle.repository.EventRepository;

public class EventFactory implements ViewModelProvider.Factory {

    private final EventRepository repository;

    public EventFactory(EventRepository repository) {
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new EventViewModel(repository);
    }
}
