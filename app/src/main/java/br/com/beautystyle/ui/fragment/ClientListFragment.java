package br.com.beautystyle.ui.fragment;

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
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import br.com.beautystyle.model.Client;
import br.com.beautystyle.ui.ListClientView;
import br.com.beautystyle.ui.ProgressButtom;
import br.com.beautystyle.ui.adapter.recyclerview.ClientListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ClientListFragment extends Fragment implements ClientListAdapter.OnClientListener {

    private final ClientListAdapter.OnClientListener onNewEventClientListener;
    private View inflateView;
    private ListClientView listClientView;
    private final ActivityResultLauncher<String> requestPermissionLauncher = getPermission();
    private Runnable runnable = null;
    private final Handler handler = new Handler();
    private ProgressButtom progressButtom;

    @NonNull
    private ActivityResultLauncher<String> getPermission() {
        return this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                startRunnable();
            } else {
                Toast.makeText(requireActivity(), "permissao negada", Toast.LENGTH_SHORT).show();
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
        inflateView = inflater.inflate(R.layout.fragment_list_client, container, false);
        setAdapterRecyclerView();
        setSearchViewClient();
        setNewClientListener();
        importContactListener();
        return inflateView;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 1) {
            listClientView.startFragmentEditClient(item, requireActivity());
        } else if (item.getItemId() == 2) {
            listClientView.checkRemove(item);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        //listClientView.listClient();
    }

    private void setAdapterRecyclerView() {
        RecyclerView listClient = inflateView.findViewById(R.id.rv_phone_contact_list);
        listClientView.setAdapter(listClient);
        registerForContextMenu(listClient);
    }

    @SuppressLint("Range")
    private void getContactList(ProgressButtom progressButtom) {
        List<Client> contactList = new ArrayList<>();
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        Cursor cursor = requireActivity().getApplicationContext().getContentResolver().query(uri, null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?";
                Cursor phoneCursor = requireActivity().getApplicationContext().getContentResolver().query(uriPhone, null, selection, new String[]{id}, null);
                @SuppressLint("Range") String number = null;
                if (phoneCursor.moveToNext()) {
                    number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                Client client = new Client(name, number);
                if (!listClientView.checkContactList(client))
                    contactList.add(client);
                phoneCursor.close();
            }
        }
        cursor.close();
        progressButtom.buttonFinished();
        listClientView.showContactList(contactList);
    }

    private void importContactListener() {
        View importContactList = inflateView.findViewById(R.id.button_import);
        importContactList.setOnClickListener(v -> {
            progressButtom = new ProgressButtom(v, requireActivity());
            if (checkPermission()) {
                startRunnable();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
            }
        });
    }

    private void startRunnable() {
        progressButtom.buttonActivated();
        runnable = () -> {
            getContactList(progressButtom);
        };
        handler.postDelayed(runnable, 5000);
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    private void setSearchViewClient() {
        SearchView svClient = inflateView.findViewById(R.id.sv_cliente);
        svClient.setOnQueryTextListener(listClientView.getClientListener());
    }

    private void setNewClientListener() {
        ImageView addClient = inflateView.findViewById(R.id.imgBtn_add_Service);
        addClient.setOnClickListener(v -> {
            NewClientFragment newClientFragment = new NewClientFragment(listClientView);
            newClientFragment.show(requireActivity().getSupportFragmentManager(), "NewClientFragment");
        });
    }

    @Override
    public void onClientClickNewEvent(Client client) {

    }

    @Override
    public void onClientClickRemoveFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
