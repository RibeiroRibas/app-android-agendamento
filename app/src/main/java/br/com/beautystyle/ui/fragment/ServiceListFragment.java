package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_SERVICE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_SERVICE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_SERVICE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_UPDATE_SERVICE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_INSERT_SERVICE;

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
import br.com.beautystyle.ViewModel.ServiceViewModel;
import br.com.beautystyle.model.Services;
import br.com.beautystyle.ui.adapter.listview.ServiceListAdapter;

public class ServiceListFragment extends Fragment {

    private List<Services> serviceList;
    private ListView listViewServices;
    private  EventViewModel eventViewModel;
    private ServiceViewModel serviceViewModel;
    private ServiceListAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        serviceViewModel = new ViewModelProvider(requireActivity()).get(ServiceViewModel.class);
        eventViewModel= new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        adapter = new ServiceListAdapter(requireActivity());
        serviceList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View viewInflate = inflater.inflate(R.layout.fragment_list_service, container, false);

        setServiceAdapter(viewInflate);
        setSearchViewService(viewInflate);

        //LISTENERS
        serviceListOnClickListener();
        selectedServicesListener(viewInflate);
        newServiceListener(viewInflate);
        setFragmentResultListener();

        return viewInflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        observableServiceList();
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.fragment_list_service_and_client_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (R.id.activity_list_service_and_client_menu_edit == item.getItemId()) {
            Services selectedService = getServiceAtposition(item);
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_UPDATE_SERVICE, selectedService);
            showNewServiceFragmentEditMode(bundle);
        } else if (R.id.activity_list_service_and_client_menu_remove == item.getItemId()) {
            checkRemove(item);
        }
        return super.onContextItemSelected(item);
    }

    public Services getServiceAtposition(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        return (Services) adapter.getItem(menuInfo.position);
    }

    public void checkRemove(MenuItem item) {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("Removendo Serviço")
                .setMessage("Tem Certeza que deseja remover esse item?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    AdapterView.AdapterContextMenuInfo menuInfo =
                            (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                    Services service = (Services) adapter.getItem(menuInfo.position);
                    serviceViewModel.delete(service).subscribe();
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void showNewServiceFragmentEditMode(Bundle bundle) {
        NewServiceFragment newServiceFragment = new NewServiceFragment();
        newServiceFragment.setArguments(bundle);
        newServiceFragment.show(getChildFragmentManager(), TAG_UPDATE_SERVICE);
    }

    private void setServiceAdapter(View viewInflate) {
        listViewServices = viewInflate.findViewById(R.id.fragment_list_service_lv);
        listViewServices.setAdapter(adapter);
        registerForContextMenu(listViewServices);

    }

    private void serviceListOnClickListener() {
        listViewServices.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listViewServices.setOnItemClickListener((adapter, view, position, id) -> {
            Services service = (Services) adapter.getItemAtPosition(position);
            if (!listViewServices.isItemChecked(position)) {
                serviceList.remove(service);
                view.setBackgroundResource(R.drawable.custom_shape_list_default);
            } else {
                serviceList.add(service);
                view.setBackgroundResource(R.drawable.custom_shape_list_clicked);
            }
        });
    }
    private void setSearchViewService(View viewInflate) {
        SearchView svService = viewInflate.findViewById(R.id.fragment_list_service_sv);
        svService.setOnQueryTextListener(getServiceListener());
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

    private void selectedServicesListener(View viewInflate) {
        TextView addSelectedServices = viewInflate.findViewById(R.id.fragment_list_service_add_selected_tv);
        addSelectedServices.setOnClickListener(v -> {
            eventViewModel.add(serviceList);
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        });
    }

    private void newServiceListener(View viewInflate) {
        ImageView addService = viewInflate.findViewById(R.id.fragment_list_service_btn_add);
        addService.setOnClickListener(v -> {
            NewServiceFragment newServiceFragment = new NewServiceFragment();
            newServiceFragment.show(getChildFragmentManager(), TAG_INSERT_SERVICE);
        });
    }

    private void setFragmentResultListener() {
        getChildFragmentManager().setFragmentResultListener(KEY_SERVICE, this, (requestKey, result) -> {
            if (result.containsKey(KEY_UPDATE_SERVICE)) {
                Services service = (Services) result.getSerializable(KEY_UPDATE_SERVICE);
                serviceViewModel.update(service).subscribe();
            } else {
                Services service = (Services) result.getSerializable(KEY_INSERT_SERVICE);
                serviceViewModel.insert(service).subscribe();
            }
        });
    }

    private void observableServiceList() {
        serviceViewModel.getAll()
                .doOnNext(serviceList -> adapter.update(serviceList))
                .subscribe();
    }
}