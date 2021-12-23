package com.example.beautystyle.ui;

import android.content.Context;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.dao.ClienteDao;
import com.example.beautystyle.model.Client;
import com.example.beautystyle.ui.adapter.ListClientAdapter;
import com.example.beautystyle.ui.fragment.NewClientFragment;

import java.util.List;

public class ListClientView {
    private final ListClientAdapter adapter;
    private final ClienteDao dao;
    private final Context context;

    public ListClientView(Context context, ListClientAdapter.OnClientListener onNewEventClientListener, ListClientAdapter.OnClientListener onListClientFragmentListener) {
        this.context = context;
        this.adapter = new ListClientAdapter(context,onNewEventClientListener,onListClientFragmentListener);
        this.dao = new ClienteDao();
    }

    public ListClientAdapter getAdapter() {
        return adapter;
    }

    public void setAdapter(RecyclerView listClient){
        listClient.setAdapter(adapter);
    }

    public void save(Client client){
        dao.save(client);
        adapter.publishResultsNew(client);
    }

    public void edit(Client client){
        dao.edit(client);
        adapter.publishResultsEdited(client);
    }

    public void startFragmentEditClient(MenuItem item, FragmentActivity listClientFragment) {
        Client selectedClient = adapter.getClientAtPosition(item.getGroupId());
        NewClientFragment newClientFragment = new NewClientFragment(this,selectedClient);
        newClientFragment.show(listClientFragment.getSupportFragmentManager(), "EditClientFragment");
    }

    private void remove(Client client) {
        dao.remove(client);
        adapter.publishResultsRemoved(client);
    }

    @NonNull
    public SearchView.OnQueryTextListener getClientListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
             adapter.filteredClient(newText);
             return  false;
            }
        };
    }

    public void listClient() {
        adapter.publishAllClient(dao.listAll());
    }

    public void checkRemove(MenuItem item) {
        new AlertDialog
                .Builder(context)
                .setTitle("Removendo Cliente")
                .setMessage("Tem Certeza que deseja remover esse item?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    Client selectedClient = adapter.getClientAtPosition(item.getGroupId());
                    remove(selectedClient);
                })
                .setNegativeButton("Não", null)
                .show();
    }

    public boolean checkContactList(Client client) {
        return dao.checkClient(client);
    }

    public void saveAllImportedClients(List<Client> contactList) {
        dao.saveAllImportedClients(contactList);
        adapter.publishResultsImportedClients(contactList);
    }
}
