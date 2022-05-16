package br.com.beautystyle.ui.fragment.job;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_JOB;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_JOB;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_JOB;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_INSERT_JOB;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_UPDATE_JOB;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.beautystyle.R;

import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.ViewModel.EventViewModel;
import br.com.beautystyle.model.entities.Job;
import br.com.beautystyle.repository.JobRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.ui.adapter.listview.JobListAdapter;
import io.reactivex.rxjava3.disposables.Disposable;

public class JobListFragment extends Fragment {

    private List<Job> jobList;
    private ListView listView;
    private EventViewModel eventViewModel;
    private JobRepository repository;
    private JobListAdapter adapter;
    private Disposable disposable;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        eventViewModel= new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        repository = new JobRepository(requireActivity());
        adapter = new JobListAdapter(requireActivity());
        jobList = new ArrayList<>();
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

        ObservableJobList();

        return viewInflate;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v,
                                    @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.fragment_job_and_client_list_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (R.id.fragment_job_and_client_list_menu_edit == item.getItemId()) {
            Bundle bundle = createBundle(item);
            showNewJobFragmentEditMode(bundle);
        } else if (R.id.fragment_job_and_client_list_menu_remove == item.getItemId()) {
            checkRemove(item);
        }
        return super.onContextItemSelected(item);
    }

    private Bundle createBundle(MenuItem item) {
        Job job = (Job) adapter.getItem(getPosition(item));
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_UPDATE_JOB, job);
        return  bundle;
    }

    private void showNewJobFragmentEditMode(Bundle bundle) {
        NewJobFragment newJobFragment = new NewJobFragment();
        newJobFragment.setArguments(bundle);
        newJobFragment.show(getChildFragmentManager(), TAG_UPDATE_JOB);
    }

    public void checkRemove(MenuItem item) {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("Removendo Serviço")
                .setMessage("Tem Certeza que deseja remover esse item?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    Job job = (Job) adapter.getItem(getPosition(item));
                    repository.delete(job, this::showErrorMessage);
                })
                .setNegativeButton("Não", null)
                .show();
    }

    public int getPosition(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        return menuInfo.position;
    }

    private void setJobListAdapter(View viewInflate) {
        listView = viewInflate.findViewById(R.id.fragment_job_list_view);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);

    }

    private void jobListOnClickListener() {
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setOnItemClickListener((adapter, view, position, id) -> {
            Job job = (Job) adapter.getItemAtPosition(position);
            if (!listView.isItemChecked(position)) {
                jobList.remove(job);
                view.setBackgroundResource(R.drawable.custom_shape_list_default);
            } else {
                jobList.add(job);
                view.setBackgroundResource(R.drawable.custom_shape_list_clicked);
            }
        });

    }

    private void addFromList(Job job, View view) {
        jobList.add(job);
        view.setBackgroundResource(R.drawable.custom_shape_list_clicked);
    }

    private void removeFromList(Job job, View view) {
        jobList.remove(job);
        view.setBackgroundResource(R.drawable.custom_shape_list_default);
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
                adapter.getFilter().filter(newText);
                return false;
            }
        };
    }

    private void selectedJobListener(View viewInflate) {
        TextView addSelectedJobs = viewInflate.findViewById(R.id.fragment_list_service_add_selected_tv);
        addSelectedJobs.setOnClickListener(v -> {
            eventViewModel.add(jobList);
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
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
                updateJob(result);
            } else {
                insertJob(result);
            }
        });
    }

    private void insertJob(Bundle result) {
        Job job = (Job) result.getSerializable(KEY_INSERT_JOB);
        repository.insert(job, this::showErrorMessage);
    }

    private void updateJob(Bundle result) {
        Job job = (Job) result.getSerializable(KEY_UPDATE_JOB);
        repository.update(job, this::showErrorMessage);
    }

    private void showErrorMessage(String erro) {
        Toast.makeText(requireActivity(),erro,Toast.LENGTH_LONG).show();
    }

    private void ObservableJobList() {
        disposable = repository.getAll(new ResultsCallBack<List<Job>>() {
            @Override
            public void onSuccess(List<Job> jobs) {
                adapter.updateList(jobs);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(disposable !=null){
            disposable.dispose();
        }
    }
}