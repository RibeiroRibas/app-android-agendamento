package br.com.beautystyle.ViewModel.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import br.com.beautystyle.ViewModel.BlockTimeViewModel;
import br.com.beautystyle.repository.BlockTimeRepository;

public class BlockTimeFactory implements ViewModelProvider.Factory {

    private final BlockTimeRepository repository;

    public BlockTimeFactory(BlockTimeRepository repository) {
        this.repository = repository;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
        return (T) new BlockTimeViewModel(repository);
    }

}
