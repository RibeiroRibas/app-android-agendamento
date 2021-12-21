package com.example.beautystyle.ui.fragment;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;
import com.example.beautystyle.model.Client;
import com.example.beautystyle.ui.ListClientView;
import com.example.beautystyle.ui.LoadingDialog;
import com.example.beautystyle.ui.ProgressButtom;
import com.example.beautystyle.ui.adapter.ListClientAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListClientFragment extends Fragment implements ListClientAdapter.OnClientListener{

    private final ListClientAdapter.OnClientListener onNewEventClientListener;
    private View inflateView;
    private ListClientView listClientView;
    private View importContactList;
    private LoadingDialog loadingDialog;
    private final ActivityResultLauncher<String> requestPermissionLauncher = getPermission();
    List<Client> contactList = new ArrayList<>();

    @NonNull
    private ActivityResultLauncher<String> getPermission() {
        return this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                //getContactList();
            } else {
                Toast.makeText(requireActivity(), "permissao negada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public ListClientFragment(ListClientAdapter.OnClientListener onNewEventClientListener) {
        this.onNewEventClientListener = onNewEventClientListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listClientView = new ListClientView(getActivity(),onNewEventClientListener,this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflateView = inflater.inflate(R.layout.fragment_client, container, false);
        setAdapterRecyclerView();
        setSearchViewClient();
        setNewClientListener();
        importContactList = inflateView.findViewById(R.id.button_import);
        importContactList.setOnClickListener(v->{
            if (checkPermission()) {
                Handler handler = new Handler();
                ProgressButtom progressButtom = new ProgressButtom(v);
                progressButtom.buttonActivated();

                try {
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            getContactList(progressButtom);
                        }
                    };
                    handler.postDelayed(runnable,10000);
                }catch (IllegalStateException e){

                }





            }else{
                requestPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
            }
        });
        return inflateView;
    }

    private boolean checkPermission(){
        return ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==1){
            listClientView.startFragmentEditClient(item, requireActivity());
        }else if(item.getItemId()==2){
            listClientView.checkRemove(item);
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: aqui entrou");
        listClientView.listClient();
    }


    @SuppressLint("Range")
    private void getContactList(ProgressButtom progressButtom) {

        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        Cursor cursor = requireActivity().getApplicationContext().getContentResolver().query(uri,null,null,null);
        if(cursor.getCount()>0){
            while(cursor.moveToNext()){
                @SuppressLint("Range") String id =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                @SuppressLint("Range") String name =cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Uri uriPhone = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?";
                Cursor phoneCursor = requireActivity().getApplicationContext().getContentResolver().query(uriPhone,null,selection,new String[]{id},null);
                @SuppressLint("Range") String number = null;
                if(phoneCursor.moveToNext()){
                    number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                }
                Client client = new Client(name,number);
                Log.i(TAG, "getContactList: contact list id "+id);
                if(!listClientView.checkContactList(client))
                    contactList.add(client);
                phoneCursor.close();
            }
        }
        cursor.close();
        progressButtom.buttonFinished();

            if(contactList.size()>0){
                new AlertDialog
                        .Builder(requireActivity())
                        .setMessage("Foram encontrados "+contactList.size()+" contatos no seu smartphone! Deseja importar todos para sua Agenda?")
                        .setPositiveButton("Sim",((dialog, which) -> {
                            listClientView.saveAllImportedClients(contactList);
                        }))
                        .setNegativeButton("Não", null)
                        .show();
            }else{
                new AlertDialog
                        .Builder(requireActivity())
                        .setTitle("Nenhum contato novo encontrato")
                        .setMessage("Você já adicionou todos os contatos do seu smartphone para sua agenda.")
                        .setPositiveButton("Ok",null)
                        .show();
            }

    }

    private void setAdapterRecyclerView() {
        RecyclerView listClient = inflateView.findViewById(R.id.rv_phone_contact_list);
        listClientView.setAdapter(listClient);
        registerForContextMenu(listClient);
    }

    private void setSearchViewClient() {
        SearchView svClient = inflateView.findViewById(R.id.sv_cliente);
        svClient.setOnQueryTextListener(listClientView.getClientListener());
    }

    private void setNewClientListener() {
        ImageView addClient = inflateView.findViewById(R.id.imgBtn_add_Service);
        addClient.setOnClickListener(v->{
            NewClientFragment newClientFragment = new NewClientFragment(listClientView);
            newClientFragment.show(requireActivity().getSupportFragmentManager(),"NewClientFragment");
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
