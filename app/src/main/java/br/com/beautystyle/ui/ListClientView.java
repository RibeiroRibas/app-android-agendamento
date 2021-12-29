package br.com.beautystyle.ui;

import android.content.Context;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import br.com.beautystyle.dao.ClienteDao;
import br.com.beautystyle.model.Client;
import br.com.beautystyle.ui.adapter.ListClientAdapter;
import br.com.beautystyle.ui.fragment.NewClientFragment;

import java.util.List;

public class ListClientView {
    private  ListClientAdapter adapter;
    private ClienteDao dao;
    private Context context;

    public ListClientView(Context context, ListClientAdapter.OnClientListener onNewEventClientListener, ListClientAdapter.OnClientListener onListClientFragmentListener) {
        this.context = context;
        this.adapter = new ListClientAdapter(context,onNewEventClientListener,onListClientFragmentListener);
        this.dao = new ClienteDao();
    }

    public ListClientView() {
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

    public void showContactList(List<Client> contactList) {
        if(contactList.size()>0){
            new AlertDialog
                    .Builder(context)
                    .setMessage("Foram encontrados "+contactList.size()+" contatos no seu smartphone! Deseja importar todos para sua Agenda?")
                    .setPositiveButton("Sim",((dialog, which) -> {
                        saveAllImportedClients(contactList);
                    }))
                    .setNegativeButton("Não", null)
                    .show();
        }else{
            new AlertDialog
                    .Builder(context)
                    .setTitle("Nenhum contato novo encontrato")
                    .setMessage("Você já adicionou todos os contatos do seu smartphone para sua agenda.")
                    .setPositiveButton("Ok",null)
                    .show();
        }
    }
}
