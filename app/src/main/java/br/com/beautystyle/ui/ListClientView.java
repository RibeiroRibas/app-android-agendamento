package br.com.beautystyle.ui;

import android.content.Context;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.beautystyle.dao.ClienteDao;
import br.com.beautystyle.model.Client;
import br.com.beautystyle.ui.adapter.recyclerview.ClientListAdapter;

public class ListClientView {
    private final ClientListAdapter adapter;
    private final ClienteDao dao;
    private final Context context;

    public ListClientView(Context context, ClientListAdapter.OnClientListener onNewEventClientListener, ClientListAdapter.OnClientListener onListClientFragmentListener) {
        this.context = context;
        this.adapter = new ClientListAdapter(context, onNewEventClientListener, onListClientFragmentListener);
        this.dao = new ClienteDao();
    }

    public void setAdapter(RecyclerView clientList) {
        clientList.setAdapter(adapter);
        adapter.publishAllClient(dao.listAll());
    }

    public void save(Client client) {
        dao.save(client);
        adapter.publishResultsNew(client);
    }

    public void edit(Client client, int position) {
        dao.edit(client);
        adapter.publishResultsEdited(client, position);
    }

    public Client getClientAtPosition(MenuItem item) {
        return adapter.getClientAtPosition(item.getGroupId());
    }

    private void remove(Client client, int position) {
        dao.remove(client);
        adapter.publishResultsRemoved(client, position);
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
                return false;
            }
        };
    }

    public void checkRemove(MenuItem item) {
        new AlertDialog
                .Builder(context)
                .setTitle("Removendo Cliente")
                .setMessage("Tem Certeza que deseja remover esse item?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    Client selectedClient = adapter.getClientAtPosition(item.getGroupId());
                    remove(selectedClient, item.getGroupId());
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
        if (contactList.size() > 0) {
            new AlertDialog
                    .Builder(context)
                    .setMessage("Foram encontrados " + contactList.size() + " contatos no seu smartphone! Deseja importar todos para sua Agenda?")
                    .setPositiveButton("Sim", ((dialog, which) -> saveAllImportedClients(contactList)))
                    .setNegativeButton("Não", null)
                    .show();
        } else {
            new AlertDialog
                    .Builder(context)
                    .setTitle("Nenhum contato novo encontrato")
                    .setMessage("Você já adicionou todos os contatos do seu smartphone para sua agenda.")
                    .setPositiveButton("Ok", null)
                    .show();
        }
    }
}
