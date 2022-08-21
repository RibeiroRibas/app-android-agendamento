package br.com.beautystyle.ui.fragment.job;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_JOB;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_JOB;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_JOB;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_INSERT_JOB;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_UPDATE_JOB;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.ViewModel.EventViewModel;
import br.com.beautystyle.ViewModel.JobViewModel;
import br.com.beautystyle.ViewModel.factory.JobFactory;
import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.repository.JobRepository;
import br.com.beautystyle.ui.adapter.recyclerview.JobListAdapter;

public class JobListFragment extends Fragment {

    private final List<Job> selectedJobs = new ArrayList<>();
    private EventViewModel eventViewModel;
    private JobViewModel jobViewModel;
    @Inject
    JobRepository repository;
    private JobListAdapter adapter;

    @Override
    public void onAttach(@NonNull Context context) {
        ((BeautyStyleApplication) requireActivity().getApplicationContext())
                .applicationComponent.injectJobListFrag(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        JobFactory factory = new JobFactory(repository);
        jobViewModel = new ViewModelProvider(this, factory).get(JobViewModel.class);
        adapter = new JobListAdapter(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewInflate = inflater.inflate(R.layout.fragment_job_list, container, false);

        setRecyclerViewAdapter(viewInflate);

        //LISTENERS
        setSearchViewListener(viewInflate);//adapter filter jobList
        adapterRecyclerViewListener();
        selectedJobListener(viewInflate);
        addJobListener(viewInflate);
        setFragmentResultListener();

        jobsLiveData();

        return viewInflate;
    }

    private void jobsLiveData() {
        jobViewModel.getAllLiveData().observe(requireActivity(), resource -> {
            if (resource.isDataNotNull()) {
                adapter.update(resource.getData());
            } else {
                showErrorMessage(resource.getError());
            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int adapterPosition = item.getGroupId();
        if (isDeleteJobMenu(item.getItemId())) {
            Job job = adapter.getJobAtPosition(adapterPosition);
            checkDelete(job);
        } else if (isUpdateJobMenu(item.getItemId())) { // is update job menu
            Job job = adapter.getJobAtPosition(adapterPosition);
            showNewJobFragmentUpdateMode(job);
        }
        return super.onContextItemSelected(item);
    }

    private boolean isUpdateJobMenu(int itemId) {
        return itemId == 3;
    }

    private boolean isDeleteJobMenu(int itemId) {
        return itemId == 4;
    }

    private Bundle createBundle(Job job) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_UPDATE_JOB, job);
        return bundle;
    }

    private void showNewJobFragmentUpdateMode(Job job) {
        Bundle bundle = createBundle(job);
        NewJobFragment newJobFragment = new NewJobFragment();
        newJobFragment.setArguments(bundle);
        newJobFragment.show(getChildFragmentManager(), TAG_UPDATE_JOB);
    }

    public void checkDelete(Job job) {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("Removendo Serviço")
                .setMessage("Tem Certeza que deseja remover esse item?")
                .setPositiveButton("Sim",
                        (dialog, which) -> delete(job)
                )
                .setNegativeButton("Não", null)
                .show();
    }

    private void delete(Job job) {
        repository.delete(job);
    }

    private void setRecyclerViewAdapter(View viewInflate) {
        RecyclerView jobListRv = viewInflate.findViewById(R.id.fragment_job_list_rv);
        jobListRv.setAdapter(adapter);
        registerForContextMenu(jobListRv);
    }

    private void adapterRecyclerViewListener() {
        adapter.setOnItemClickListener((
                (job, isSelected) -> {
                    if (isSelected) {
                        selectedJobs.add(job);
                    } else {
                        selectedJobs.remove(job);
                    }
                }
        ));
    }

    private void setSearchViewListener(View viewInflate) {
        SearchView svJob = viewInflate.findViewById(R.id.fragment_job_list_search_view);
        svJob.setOnQueryTextListener(getServiceListener());
    }

    @NonNull
    public SearchView.OnQueryTextListener getServiceListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.filterJobsByName(newText);
                return false;
            }
        };
    }

    private void selectedJobListener(View viewInflate) {
        TextView addSelectedJobs = viewInflate.findViewById(R.id.fragment_list_service_add_selected_tv);
        addSelectedJobs.setOnClickListener(v -> {
            eventViewModel.add(selectedJobs);
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .commit();
        });
    }

    private void addJobListener(View viewInflate) {
        ImageView addJob = viewInflate.findViewById(R.id.fragment_job_list_btn_add);
        addJob.setOnClickListener(v -> {
            NewJobFragment newJobFragment = new NewJobFragment();
            newJobFragment.show(getChildFragmentManager(), TAG_INSERT_JOB);
        });
    }

    private void setFragmentResultListener() {
        getChildFragmentManager().setFragmentResultListener(
                KEY_JOB, this, (requestKey, result) -> {
                    isInsert(result);
                    isUpdate(result);
                });
    }

    private void isUpdate(Bundle result) {
        if (result.containsKey(KEY_UPDATE_JOB)) {
            Job job = (Job) result.getSerializable(KEY_UPDATE_JOB);
            repository.update(job);
        }
    }

    private void isInsert(Bundle result) {
        if (result.containsKey(KEY_INSERT_JOB)) {
            Job job = (Job) result.getSerializable(KEY_INSERT_JOB);
            repository.insert(job);
        }
    }

    private void showErrorMessage(String erro) {
        if (this.getActivity() != null)
            Toast.makeText(requireActivity(), erro, Toast.LENGTH_LONG).show();
    }

}