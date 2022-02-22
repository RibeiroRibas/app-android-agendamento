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

import br.com.beautystyle.domain.model.Services;

public class ServiceListAdapter extends BaseAdapter implements Filterable {

    private final Context context;
    private final List<Services> serviceListAll;
    private final List<Services> serviceList;
    private List<Services> filteredListService;

    public ServiceListAdapter(Context context) {
        this.context = context;
        this.serviceList = new ArrayList<>();
        this.serviceListAll = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return serviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return serviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return serviceList.get(position).getServiceId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View createdView = getInflate(parent);
        setServiceName(createdView, position);
        return createdView;
    }

    private View getInflate(ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_service_and_client, parent, false);
    }

    private void setServiceName(View createdView, int position) {
        TextView service = createdView.findViewById(R.id.tv_name);
        service.setText(serviceList.get(position).getName());
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                filteredListService = new ArrayList<>();

                if (constraint.toString().isEmpty()) {
                    filteredListService.addAll(serviceListAll);
                } else {
                    for (Services service : serviceListAll) {
                        if (service.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredListService.add(service);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredListService;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                serviceList.clear();
                serviceList.addAll(filteredListService);
                notifyDataSetChanged();
            }
        };
    }

    public void update(List<Services> serviceList) {
        this.serviceList.clear();
        this.serviceList.addAll(serviceList);
        serviceListAll.addAll(serviceList);
        notifyDataSetChanged();
    }

    public void remove(Services service) {
        serviceList.remove(service);
        serviceListAll.remove(service);
        notifyDataSetChanged();
    }

}
