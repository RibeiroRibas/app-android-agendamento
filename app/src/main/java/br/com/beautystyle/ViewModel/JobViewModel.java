package br.com.beautystyle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.repository.JobRepository;
import br.com.beautystyle.repository.Resource;

public class JobViewModel extends ViewModel {

    private final JobRepository repository;

    public JobViewModel(JobRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<Job>>> getAllLiveData() {
        return repository.getAllLiveData();
    }

}
