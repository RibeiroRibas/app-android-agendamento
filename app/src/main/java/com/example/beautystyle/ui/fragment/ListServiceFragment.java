package com.example.beautystyle.ui.fragment;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import com.example.beautystyle.R;
import com.example.beautystyle.model.Services;
import com.example.beautystyle.ui.ListServiceView;

public class ListServiceFragment extends Fragment {

    private final ListServiceFragment.OnServiceListener onNewEventServiceListener;
    private ListServiceView listServiceView;
    private ListView listService;

    public ListServiceFragment(ListServiceFragment.OnServiceListener onNewEventServiceListener) {
        this.onNewEventServiceListener = onNewEventServiceListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listServiceView = new ListServiceView(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewInflate = inflater.inflate(R.layout.fragment_service, container, false);

        setServiceAdapter(viewInflate);
        listServiceOnClickListener();
        setSearchViewService(viewInflate);
        setSelectedServicesListener(viewInflate);
        setNewServiceListener(viewInflate);

        return viewInflate;
    }

    private void listServiceOnClickListener() {
        listService.setOnItemClickListener((adapter, view, position, id) -> {
            Services service = (Services) adapter.getItemAtPosition(position);
            if (listService.isSelected()) {
                listService.setSelected(false);
                onNewEventServiceListener.onServiceClickRemoveItemList(service);
                view.setBackgroundResource(R.drawable.custom_shape_list_default);
            } else {
                listService.setSelected(true);
                onNewEventServiceListener.onServiceClickAddItemList(service);
                view.setBackgroundResource(R.drawable.custom_shape_list_clicked);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        listServiceView.updateService();
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.fragment_list_service_and_client_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (R.id.activity_list_service_and_client_menu_edit == item.getItemId()){
            listServiceView.edit(item,requireActivity());
        }else if(R.id.activity_list_service_and_client_menu_remove == item.getItemId()){
            listServiceView.checkRemove(item);
        }
        return super.onContextItemSelected(item);
    }

    private void setServiceAdapter(View viewInflate) {
        listService = viewInflate.findViewById(R.id.lv_service);
        registerForContextMenu(listService);
        listServiceView.setAdapter(listService);
    }

    private void setNewServiceListener(View viewInflate) {
        ImageView addService = viewInflate.findViewById(R.id.imgBtn_add_Service);
        addService.setOnClickListener(v -> {
            NewServiceFragment newServiceFragment = new NewServiceFragment(listServiceView);
            newServiceFragment.show(requireActivity().getSupportFragmentManager(), "NewServiceFragment");
        });
    }

    private void setSelectedServicesListener(View viewInflate) {
        TextView addSelectedServices = viewInflate.findViewById(R.id.tv_add_selected);
        addSelectedServices.setOnClickListener(v -> {
            onNewEventServiceListener.onServiceClickFillForm();
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        });
    }

    private void setSearchViewService(View viewInflate) {
        SearchView svService = viewInflate.findViewById(R.id.sv_service);
        svService.setOnQueryTextListener(listServiceView.getServiceListener());
    }

    public interface OnServiceListener {
        void onServiceClickAddItemList(Services service);
        void onServiceClickRemoveItemList(Services service);
        void onServiceClickFillForm();
    }
}