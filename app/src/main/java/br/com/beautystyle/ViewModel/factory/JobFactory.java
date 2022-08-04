package br.com.beautystyle.ViewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import br.com.beautystyle.ViewModel.JobViewModel;
import br.com.beautystyle.repository.JobRepository;

public class JobFactory implements ViewModelProvider.Factory {

    private final JobRepository repository;

    public JobFactory(JobRepository repository) {
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new JobViewModel(repository);
    }
}
