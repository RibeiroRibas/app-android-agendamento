package br.com.beautystyle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.beautystyle.model.entity.Category;
import br.com.beautystyle.repository.CategoryRepository;
import br.com.beautystyle.repository.Resource;

public class CategoryViewModel extends ViewModel {

    private final CategoryRepository repository;

    public CategoryViewModel(CategoryRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<Category>>> getAllLiveData() {
        return repository.getAllLiveData();
    }
}
