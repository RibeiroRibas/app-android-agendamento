package com.example.beautystyle.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.beautystyle.model.Services;
import com.example.beautystyle.R;

import java.util.ArrayList;
import java.util.List;

public class ListServiceAdapter extends BaseAdapter implements Filterable {
    private final Context context;
    private final List<Services> listServiceAll = new ArrayList<>();
    private final List<Services> listService = new ArrayList<>();

    public ListServiceAdapter(Context context) {
        this.context = context;

    }

    @Override
    public int getCount() {
        return listService.size();
    }

    @Override
    public Object getItem(int position) {
        return listService.get(position);
    }

    @Override
    public long getItemId(int position) {
        return listService.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View createdView = getInflate(parent);
        setServiceName(createdView, position);
        return createdView;
    }

    private View getInflate(ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_list_service, parent, false);
    }

    private void setServiceName(View createdView, int position) {
        TextView service = createdView.findViewById(R.id.tv_service);
        service.setText(listService.get(position).getName());
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                List<Services> filteredListService = new ArrayList<>();

                if (constraint.toString().isEmpty()) {
                    filteredListService.addAll(listServiceAll);
                } else {
                    for (Services service : listServiceAll) {
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
                listService.clear();
                listService.addAll((List<Services>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public void update(List<Services> listAll) {
        listService.clear();
        listService.addAll(listAll);
        listServiceAll.addAll(listAll);
        notifyDataSetChanged();
    }

    public  void remove(Services service){
        listService.remove(service);
        listServiceAll.remove(service);
        notifyDataSetChanged();
    }

    public void save(Services service) {
        listService.add(service);
        listServiceAll.add(service);
        notifyDataSetChanged();
    }
}
