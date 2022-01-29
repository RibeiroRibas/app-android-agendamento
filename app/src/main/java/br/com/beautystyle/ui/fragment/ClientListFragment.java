package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_EDIT_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_NEW_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_EDIT_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_NEW_CLIENT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.model.Client;
import br.com.beautystyle.ui.ListClientView;
import br.com.beautystyle.ui.ProgressButtom;
import br.com.beautystyle.ui.adapter.recyclerview.ClientListAdapter;

public class ClientListFragment extends Fragment implements ClientListAdapter.OnClientListener {

    private final ClientListAdapter.OnClientListener onNewEventClientListener;
    private View inflateDView;
    private ListClientView listClientView;
    private final ActivityResultLauncher<String> requestPermissionLauncher = getPermission();
    private Runnable runnable = null;
    private final Handler handler = new Handler();
    private ProgressButtom progressButtom;
    private static final String NO_PERMISSION = "Permissão Negada";

    @NonNull
    private ActivityResultLauncher<String> getPermission() {
        return this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                startRunnable();
            } else {
                Toast.makeText(requireActivity(), NO_PERMISSION, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    public ClientListFragment(ClientListAdapter.OnClientListener onNewEventClientListener) {
        this.onNewEventClientListener = onNewEventClientListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listClientView = new ListClientView(getActivity(), onNewEventClientListener, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflateDView = inflater.inflate(R.layout.fragment_list_client, container, false);

        setAdapterRecyclerView();
        setSearchViewClient();
        setNewClientListener();
        importContactListener();
        setFragmentResultListener();

        return inflateDView;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 1) {
            Client selectedClient = listClientView.getClientAtPosition(item);
            Bundle bundle = createBundle(selectedClient,item);
            showNewClientFragmentEditMode(bundle);
        } else if (item.getItemId() == 2) {
            listClientView.checkRemove(item);
        }
        return super.onContextItemSelected(item);
    }

    private Bundle createBundle(Client selectedClient,MenuItem item) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_EDIT_CLIENT, selectedClient);
        bundle.putInt(KEY_POSITION, item.getGroupId());
        return bundle;
    }

    private void showNewClientFragmentEditMode(Bundle bundle) {
        NewClientFragment newClientFragment = new NewClientFragment();
        newClientFragment.setArguments(bundle);
        newClientFragment.show(getChildFragmentManager(), TAG_EDIT_CLIENT);
    }

    private void setAdapterRecyclerView() {
        RecyclerView listClient = inflateDView.findViewById(R.id.fragment_list_cliente_rv);
        listClientView.setAdapter(listClient);
        registerForContextMenu(listClient);
    }

    private void importContactListener() {
        View importContactList = inflateDView.findViewById(R.id.fragment_list_client_import);
        importContactList.setOnClickListener(v -> {
            progressButtom = new ProgressButtom(v, requireActivity());
            if (checkPermission()) {
                startRunnable();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
            }
        });
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void startRunnable() {
        progressButtom.buttonActivated();
        runnable = () -> {
            List<Client> contactList = progressButtom.getContactList(listClientView);
            listClientView.showContactList(contactList);
        };
        handler.postDelayed(runnable, 3000);
    }

    private void setSearchViewClient() {
        SearchView svClient = inflateDView.findViewById(R.id.fragment_list_client_sv);
        svClient.setOnQueryTextListener(listClientView.getClientListener());
    }

    private void setNewClientListener() {
        ImageView addClient = inflateDView.findViewById(R.id.fragment_list_cliente_btn_add);
        addClient.setOnClickListener(v -> {
            NewClientFragment newClientFragment = new NewClientFragment();
            newClientFragment.show(getChildFragmentManager(), TAG_NEW_CLIENT);
        });
    }

    @Override
    public void onClientClickNewEvent(Client client) {

    }

    @Override
    public void onClientClickRemoveFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }

    private void setFragmentResultListener() {
        getChildFragmentManager().setFragmentResultListener(KEY_CLIENT, this, (requestKey, result) -> {
            if (result.containsKey(KEY_EDIT_CLIENT)) {
                Client client = (Client) result.getSerializable(KEY_EDIT_CLIENT);
                int position = result.getInt(KEY_POSITION);
                listClientView.edit(client, position);
            } else {
                Client client = (Client) result.getSerializable(KEY_NEW_CLIENT);
                listClientView.save(client);
            }
        });
    }
}
