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

    private final List<Job> jobList;
    private final List<Job> jobListToFilter;
    private final Context context;
    private AdapterListener.OnJobClickListener onItemClickListener;

    public JobListAdapter(Context context) {
        this.jobList = new ArrayList<>();
        this.jobListToFilter = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public JobListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View createdView = LayoutInflater.from(context).inflate(R.layout.item_job_and_client, parent, false);
        return new JobListAdapter.JobListHolder(createdView);
    }

    @Override
    public void onBindViewHolder(@NonNull JobListHolder holder, int position) {
        if (!jobList.isEmpty()) {
            Job job = jobList.get(position);
            holder.setJobName(job);
        }
    }

    @Override
    public int getItemCount() {
        return jobList.size();
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
        List<Job> filteredJobList = jobList.stream()
                .filter(job ->
                        !job.getName().toLowerCase()
                                .contains(newText.toLowerCase())
                )
                .collect(Collectors.toList());
        for (Job job : filteredJobList) {
            int index = jobList.indexOf(job);
            itemRemoved(job, index);
        }
    }

    private void itemRemoved(Job job, int index) {
        jobList.remove(job);
        notifyItemRemoved(index);
    }

    private void itemRangeInserted() {
        notifyItemRangeRemoved(0, this.jobList.size());
        this.jobList.clear();
        this.jobList.addAll(jobListToFilter);
        notifyItemRangeInserted(0, this.jobList.size());
    }

    public void publishResultsInsert(Job job) {
        jobList.add(job);
        jobListToFilter.add(job);
        jobList.sort(new SortByJobName());
        notifyItemInserted(jobList.indexOf(job));
    }

    public void publishJobList(List<Job> jobs) {
        notifyItemRangeRemoved(0, jobList.size());
        jobList.clear();
        jobListToFilter.clear();
        jobList.addAll(jobs);
        jobList.sort(new SortByJobName());
        jobListToFilter.addAll(jobList);
        notifyItemRangeInserted(0, jobList.size());
    }

    public void publishResultsRemoved(Job job, int position) {
        itemRemoved(job, position);
        jobListToFilter.remove(job);
    }

    public void publishResultsUpdate(Job job, int position) {
        jobList.set(position, job);
        jobListToFilter.set(position, job);
        notifyItemChanged(position, job);
    }

    public Job getJobAtPosition(int position) {
        return jobList.get(position);
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
