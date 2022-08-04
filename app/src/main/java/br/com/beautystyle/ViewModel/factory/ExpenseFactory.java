package br.com.beautystyle.ViewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import br.com.beautystyle.ViewModel.ExpenseViewModel;
import br.com.beautystyle.repository.ExpenseRepository;

public class ExpenseFactory implements ViewModelProvider.Factory {

    private final ExpenseRepository repository;

    public ExpenseFactory(ExpenseRepository repository) {
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new ExpenseViewModel(repository);
    }
}
