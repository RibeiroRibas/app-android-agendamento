package br.com.beautystyle.ViewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import br.com.beautystyle.ViewModel.ReportViewModel;
import br.com.beautystyle.repository.ReportRepository;

public class ReportFactory implements ViewModelProvider.Factory {

    private final ReportRepository repository;

    public ReportFactory(ReportRepository repository) {
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new ReportViewModel(repository);
    }
}
