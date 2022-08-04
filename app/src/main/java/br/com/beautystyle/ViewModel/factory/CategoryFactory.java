package br.com.beautystyle.ViewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import br.com.beautystyle.ViewModel.CategoryViewModel;
import br.com.beautystyle.repository.CategoryRepository;

public class CategoryFactory implements ViewModelProvider.Factory {

    private final CategoryRepository repository;

    public CategoryFactory(CategoryRepository repository) {
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new CategoryViewModel(repository);
    }
}
