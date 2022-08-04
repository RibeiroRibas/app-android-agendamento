package br.com.beautystyle.ui.adapter.recyclerview;

import static br.com.beautystyle.ui.adapter.ConstantsAdapter.ITEM_MENU_REMOVE;
import static br.com.beautystyle.ui.adapter.ConstantsAdapter.ITEM_MENU_UPDATE;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.ui.adapter.recyclerview.listener.AdapterListener;
import br.com.beautystyle.util.SortByJobName;

public class JobListAdapter extends RecyclerView.Adapter<JobListAdapter.JobListHolder> {

    private final List<Job> jobs;
    private final List<Job> jobsToFilter;
    private final Context context;
    private AdapterListener.OnJobClickListener onItemClickListener;

    public JobListAdapter(Context context) {
        this.jobs = new ArrayList<>();
        this.jobsToFilter = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public JobListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View createdView = inflateLayout(parent);
        return new JobListAdapter.JobListHolder(createdView);
    }

    private View inflateLayout(@NonNull ViewGroup parent) {
        return LayoutInflater.from(context)
                .inflate(R.layout.item_job_and_client, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull JobListHolder holder, int position) {
        if (!jobs.isEmpty()) {
            Job job = jobs.get(position);
            holder.setJobName(job);
        }
    }

    @Override
    public int getItemCount() {
        return jobs.size();
    }

    public void setOnItemClickListener(AdapterListener.OnJobClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void filterJobsByName(String newText) {
        if (newText.isEmpty()) {
            itemRangeInserted();
        } else {
            itemRangeRemoved(newText);
        }
    }

    private void itemRangeRemoved(String newText) {
        List<Job> filteredJobs = filterByInputText(newText);
        for (Job job : filteredJobs) {
            int index = jobs.indexOf(job);
            jobs.remove(job);
            notifyItemRemoved(index);
        }
    }

    @NonNull
    private List<Job> filterByInputText(String newText) {
        return jobs.stream()
                .filter(job ->
                        !job.getName().toLowerCase()
                                .contains(newText.toLowerCase())
                )
                .collect(Collectors.toList());
    }

    private void itemRangeInserted() {
        notifyItemRangeRemoved(0, this.jobs.size());
        this.jobs.clear();
        this.jobs.addAll(jobsToFilter);
        notifyItemRangeInserted(0, this.jobs.size());
    }

    public Job getJobAtPosition(int position) {
        return jobs.get(position);
    }

    public void update(List<Job> jobsToUpdate) {
        notifyItemRangeRemoved(0, jobs.size());
        jobs.clear();
        jobsToFilter.clear();
        jobs.addAll(jobsToUpdate);
        jobs.sort(new SortByJobName());
        jobsToFilter.addAll(jobs);
        notifyItemRangeInserted(0, jobs.size());
    }

    class JobListHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private final TextView jobName;
        private final View itemViewHolder;
        private Job job;
        private boolean isItemSelected = false;

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 3, 0, ITEM_MENU_UPDATE);
            menu.add(this.getAdapterPosition(), 4, 1, ITEM_MENU_REMOVE);
        }

        public JobListHolder(@NonNull View itemView) {
            super(itemView);

            this.itemViewHolder = itemView;
            jobName = itemView.findViewById(R.id.tv_name);
            onClickListener();
            itemView.setOnCreateContextMenuListener(this);
        }

        private void onClickListener() {
            itemView.setOnClickListener(v -> {
                checkIsItemSelected();
                onItemClickListener.onItemClick(job, isItemSelected);
            });
        }

        public void setJobName(Job job) {
            setBackground(R.drawable.custom_shape_list_default);
            this.job = job;
            jobName.setText(job.getName());
        }

        private void setBackground(int customShape) {
            itemViewHolder.setBackgroundResource(customShape);
        }

        private void checkIsItemSelected() {
            if (isItemSelected) {
                isItemSelected = false;
                setBackground(R.drawable.custom_shape_list_default);
            } else {
                isItemSelected = true;
                setBackground(R.drawable.custom_shape_list_clicked);
            }
        }
    }
}
