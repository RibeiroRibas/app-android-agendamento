package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_EDIT_SERVICE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_NEW_SERVICE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_SERVICE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_EDIT_SERVICE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_NEW_SERVICE;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import com.example.beautystyle.R;

import br.com.beautystyle.model.Services;
import br.com.beautystyle.ui.ListServiceView;

public class ServiceListFragment extends Fragment {

    private final ServiceListFragment.OnServiceListener onNewEventServiceListener;
    private ListServiceView listServiceView;
    private ListView listService;

    public ServiceListFragment(ServiceListFragment.OnServiceListener onNewEventServiceListener) {
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
        View viewInflate = inflater.inflate(R.layout.fragment_list_service, container, false);

        setServiceAdapter(viewInflate);
        serviceListOnClickListener();
        setSearchViewService(viewInflate);
        setSelectedServicesListener(viewInflate);
        setNewServiceListener(viewInflate);
        setFragmentResultListener();

        return viewInflate;
    }

    @Override
    public void onResume() {
        super.onResume();
        listServiceView.updateService();
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
            Services selectedService = listServiceView.getServiceAtposition(item);
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_EDIT_SERVICE, selectedService);
            showNewServiceFragmentEditMode(bundle);
        } else if (R.id.activity_list_service_and_client_menu_remove == item.getItemId()) {
            listServiceView.checkRemove(item);
        }
        return super.onContextItemSelected(item);
    }

    private void showNewServiceFragmentEditMode(Bundle bundle) {
        NewServiceFragment newServiceFragment = new NewServiceFragment();
        newServiceFragment.setArguments(bundle);
        newServiceFragment.show(getChildFragmentManager(), TAG_EDIT_SERVICE);
    }

    private void setServiceAdapter(View viewInflate) {
        listService = viewInflate.findViewById(R.id.fragment_list_service_lv);
        registerForContextMenu(listService);
        listServiceView.setAdapter(listService);
    }

    private void serviceListOnClickListener() {
        listService.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listService.setOnItemClickListener((adapter, view, position, id) -> {
            Services service = (Services) adapter.getItemAtPosition(position);
            if (!listService.isItemChecked(position)) {
                onNewEventServiceListener.onServiceClickRemoveItemList(service);
                view.setBackgroundResource(R.drawable.custom_shape_list_default);
            } else {
                onNewEventServiceListener.onServiceClickAddItemList(service);
                view.setBackgroundResource(R.drawable.custom_shape_list_clicked);
            }
        });
    }
    private void setSearchViewService(View viewInflate) {
        SearchView svService = viewInflate.findViewById(R.id.fragment_list_service_sv);
        svService.setOnQueryTextListener(listServiceView.getServiceListener());
    }
    private void setSelectedServicesListener(View viewInflate) {
        TextView addSelectedServices = viewInflate.findViewById(R.id.fragment_list_service_add_selected_tv);
        addSelectedServices.setOnClickListener(v -> {
            onNewEventServiceListener.onServiceClickFillForm();
            requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        });
    }

    private void setNewServiceListener(View viewInflate) {
        ImageView addService = viewInflate.findViewById(R.id.fragment_list_service_btn_add);
        addService.setOnClickListener(v -> {
            NewServiceFragment newServiceFragment = new NewServiceFragment();
            newServiceFragment.show(getChildFragmentManager(), TAG_NEW_SERVICE);
        });
    }

    private void setFragmentResultListener() {
        getChildFragmentManager().setFragmentResultListener(KEY_SERVICE, this, (requestKey, result) -> {
            if (result.containsKey(KEY_EDIT_SERVICE)) {
                Services service = (Services) result.getSerializable(KEY_EDIT_SERVICE);
                listServiceView.edit(service);
            } else {
                Services service = (Services) result.getSerializable(KEY_NEW_SERVICE);
                listServiceView.save(service);
            }
        });
    }

    public interface OnServiceListener {
        void onServiceClickAddItemList(Services service);

        void onServiceClickRemoveItemList(Services service);

        void onServiceClickFillForm();
    }
}