package br.com.beautystyle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import br.com.beautystyle.model.entity.BlockTime;
import br.com.beautystyle.repository.BlockTimeRepository;
import br.com.beautystyle.repository.Resource;

public class BlockTimeViewModel extends ViewModel {

    private final BlockTimeRepository blockTimeRepository;

    public BlockTimeViewModel(BlockTimeRepository blockTimeRepository) {
        this.blockTimeRepository = blockTimeRepository;
    }

    public LiveData<Resource<BlockTime>> insert(BlockTime blockTime) {
        return blockTimeRepository.insert(blockTime);
    }

    public LiveData<Resource<Void>> delete(BlockTime blockTime) {
        return blockTimeRepository.delete(blockTime);
    }

    public LiveData<Resource<Void>> update(BlockTime blockTime) {
        return blockTimeRepository.update(blockTime);
    }
}
