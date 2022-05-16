package br.com.beautystyle.ui.adapter.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.beautystyle.R;

import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.model.entities.Job;

public class JobListAdapter extends BaseAdapter implements Filterable {

    private final Context context;
    private final List<Job> jobListAll;
    private final List<Job> jobList;
    private List<Job> filteredJobList;

    public JobListAdapter(Context context) {
        this.context = context;
        this.jobList = new ArrayList<>();
        this.jobListAll = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return jobList.size();
    }

    @Override
    public Object getItem(int position) {
        return jobList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return jobList.get(position).getJobId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View createdView = getInflate(parent);
        setTextView(createdView, position);
        return createdView;
    }

    private View getInflate(ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_job_and_client, parent, false);
    }

    private void setTextView(View createdView, int position) {
        TextView name = createdView.findViewById(R.id.tv_name);
        name.setText(jobList.get(position).getName());
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                filteredJobList = new ArrayList<>();

                if (constraint.toString().isEmpty()) {
                    filteredJobList.addAll(jobListAll);
                } else {
                    for (Job service : jobListAll) {
                        if (service.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredJobList.add(service);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredJobList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                jobList.clear();
                jobList.addAll(filteredJobList);
                notifyDataSetChanged();
            }
        };
    }

    public void updateList(List<Job> serviceList) {
        this.jobList.clear();
        this.jobList.addAll(serviceList);
        jobListAll.addAll(serviceList);
        notifyDataSetChanged();
    }

}
