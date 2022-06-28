package br.com.beautystyle.ui.fragment.job;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_JOB;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_JOB;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;
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
import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.repository.JobRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.ui.adapter.recyclerview.JobListAdapter;

public class JobListFragment extends Fragment {

    private final List<Job> jobList = new ArrayList<>();
    private EventViewModel eventViewModel;
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
        adapter = new JobListAdapter(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewInflate = inflater.inflate(R.layout.fragment_job_list, container, false);

        setJobListAdapter(viewInflate);
        setSearchViewJob(viewInflate);//adapter filter jobList

        //LISTENERS
        jobListOnClickListener();
        selectedJobListener(viewInflate);
        newJobListener(viewInflate);
        setFragmentResultListener();

        getAllJobs();

        return viewInflate;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int adapterPosition = item.getGroupId();
        if (isDeleteJobMenu(item.getItemId())) {
            Job job = adapter.getJobAtPosition(adapterPosition);
            checkRemove(job, adapterPosition);
        } else if (isUpdateJobMenu(item.getItemId())) { // is update job menu
            Job job = adapter.getJobAtPosition(adapterPosition);
            Bundle bundle = createBundle(job, adapterPosition);
            showNewJobFragmentUpdateMode(bundle);
        }
        return super.onContextItemSelected(item);
    }

    private boolean isUpdateJobMenu(int itemId) { return itemId == 3; }

    private boolean isDeleteJobMenu(int itemId) {
        return itemId == 4;
    }

    private Bundle createBundle(Job job, int adapterPosition) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_POSITION, adapterPosition);
        bundle.putSerializable(KEY_UPDATE_JOB, job);
        return bundle;
    }

    private void showNewJobFragmentUpdateMode(Bundle bundle) {
        NewJobFragment newJobFragment = new NewJobFragment();
        newJobFragment.setArguments(bundle);
        newJobFragment.show(getChildFragmentManager(), TAG_UPDATE_JOB);
    }

    public void checkRemove(Job job, int adapterPosition) {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("Removendo Serviço")
                .setMessage("Tem Certeza que deseja remover esse item?")
                .setPositiveButton("Sim",
                        (dialog, which) -> delete(job, adapterPosition)
                )
                .setNegativeButton("Não", null)
                .show();
    }

    private void delete(Job job, int adapterPosition) {
        repository.deleteOnApi(job, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void resultado) {
                deleteOnRoom(job, adapterPosition);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void deleteOnRoom(Job job, int position) {
        repository.deleteOnRoom(job)
                .doOnComplete(() -> adapter.publishResultsRemoved(job, position))
                .subscribe();
    }

    private void setJobListAdapter(View viewInflate) {
        RecyclerView jobListRv = viewInflate.findViewById(R.id.fragment_job_list_rv);
        jobListRv.setAdapter(adapter);
        registerForContextMenu(jobListRv);
    }

    private void jobListOnClickListener() {
        adapter.setOnItemClickListener((
                (job, isSelected) -> {
                    if (isSelected) {
                        jobList.add(job);
                    } else {
                        jobList.remove(job);
                    }
                }
        ));
    }

    private void setSearchViewJob(View viewInflate) {
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
            eventViewModel.add(jobList);
            requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .remove(this)
                    .commit();
        });
    }

    private void newJobListener(View viewInflate) {
        ImageView addJob = viewInflate.findViewById(R.id.fragment_job_list_btn_add);
        addJob.setOnClickListener(v -> {
            NewJobFragment newJobFragment = new NewJobFragment();
            newJobFragment.show(getChildFragmentManager(), TAG_INSERT_JOB);
        });
    }

    private void setFragmentResultListener() {
        getChildFragmentManager().setFragmentResultListener(
                KEY_JOB, this, (requestKey, result) -> {
                    if (result.containsKey(KEY_UPDATE_JOB)) {
                        update(result);
                    } else {
                        insert(result);
                    }
                });
    }

    private void insert(Bundle result) {
        Job job = (Job) result.getSerializable(KEY_INSERT_JOB);
        repository.insertOnApi(job, new ResultsCallBack<Job>() {
            @Override
            public void onSuccess(Job jobFromApi) {
                insertOnRoom(jobFromApi);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void insertOnRoom(Job jobFromApi) {
        jobFromApi.setJobId(null);
        repository.insertOnRoom(jobFromApi)
                .doOnSuccess(id -> {
                    jobFromApi.setJobId(id);
                    adapter.publishResultsInsert(jobFromApi);
                }).subscribe();
    }

    private void update(Bundle result) {
        Job job = (Job) result.getSerializable(KEY_UPDATE_JOB);
        int position = result.getInt(KEY_POSITION);
        repository.updateOnApi(job, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void resultado) {
                updateonRoom(job, position);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });

    }

    private void updateonRoom(Job job, int position) {
        repository.updateOnRoom(job)
                .doOnComplete(() -> adapter.publishResultsUpdate(job, position))
                .subscribe();
    }

    private void showErrorMessage(String erro) {
        if (this.getActivity() != null)
            Toast.makeText(requireActivity(), erro, Toast.LENGTH_LONG).show();
    }

    private void getAllJobs() {
        repository.getAllFromRoom()
                .doOnSuccess(jobListFromRoom -> {
                    adapter.publishJobList(jobListFromRoom);
                    getAllFromApi();
                }).subscribe();
    }

    private void getAllFromApi() {
        repository.getAllFromApi(new ResultsCallBack<List<Job>>() {
            @Override
            public void onSuccess(List<Job> jobListFromApi) {
                repository.updatejobs(jobListFromApi,
                        new ResultsCallBack<List<Job>>() {
                            @Override
                            public void onSuccess(List<Job> result) {
                                adapter.publishJobList(result);
                            }

                            @Override
                            public void onError(String erro) {
                                showErrorMessage(erro);
                            }
                        });

            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }


}