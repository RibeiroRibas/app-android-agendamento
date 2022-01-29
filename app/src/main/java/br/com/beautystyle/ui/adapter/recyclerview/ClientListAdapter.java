package br.com.beautystyle.ui.adapter.recyclerview;

import static br.com.beautystyle.ui.adapter.ConstantsAdapter.ITEM_MENU_DELETE;
import static br.com.beautystyle.ui.adapter.ConstantsAdapter.ITEM_MENU_EDIT;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.beautystyle.model.Client;

public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.ListClientHolder> {

    private final List<Client> clientList;
    private final List<Client> clientListAll;
    private final OnClientListener onNewEventClientListener;
    private final OnClientListener onClientListFragmentListener;
    private final Context context;

    public ClientListAdapter(Context context, OnClientListener onNewEventClientListener, OnClientListener onListClientFragmentListener) {
        this.context = context;
        this.clientList = new ArrayList<>();
        this.clientListAll = new ArrayList<>();
        this.onNewEventClientListener = onNewEventClientListener;
        this.onClientListFragmentListener = onListClientFragmentListener;
    }

    @NonNull
    @Override
    public ListClientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View createdView = LayoutInflater.from(context).inflate(R.layout.item_service_and_client, parent, false);
        return new ListClientHolder(createdView, onNewEventClientListener, clientList, onClientListFragmentListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListClientHolder holder, int position) {
        if (!clientList.isEmpty()) {
            Client client = clientList.get(position);
            holder.setTextView(client);
        }
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public void filteredClient(String newText) {
        List<Client> filteredListClient = new ArrayList<>();
        if (newText.isEmpty()) {
            itemInserted(filteredListClient);
        } else {
            itemRemoved(newText, filteredListClient);
        }
    }

    private void itemRemoved(String newText, List<Client> filteredListClient) {
        for (Client client : clientList) {
            if (!client.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredListClient.add(client);
            }
        }
        for (Client client : filteredListClient) {
            int index = clientList.indexOf(client);
            clientList.remove(client);
            notifyItemRemoved(index);
        }
    }

    private void itemInserted(List<Client> filteredListClient) {
        if (clientList.isEmpty()) {
            filteredListClient = clientListAll;
        }
        for (Client c : clientList) {
            filteredListClient = clientListAll.stream().filter(client -> client != c).collect(Collectors.toList());
        }
        for (Client c : filteredListClient) {
            clientList.add(c);
            notifyItemInserted(clientList.indexOf(c));
        }
    }

    public void publishAllClient(List<Client> listAll) {
        clientList.addAll(listAll);
        clientListAll.addAll(listAll);
        notifyItemRangeInserted(0, listAll.size());
    }

    public void publishResultsNew(Client client) {
        clientList.add(client);
        clientListAll.add(client);
        notifyItemInserted(clientList.indexOf(client));
    }

    public void publishResultsImportedClients(List<Client> newListClient) {
        int size = clientList.size();
        clientList.addAll(newListClient);
        clientListAll.addAll(newListClient);
        notifyItemRangeInserted(size, clientList.size());
    }

    public void publishResultsRemoved(Client client, int position) {
        clientList.remove(client);
        clientListAll.remove(client);
        notifyItemRemoved(position);
    }

    public void publishResultsEdited(Client client, int position) {
        clientList.set(position, client);
        clientListAll.set(position, client);
        notifyItemChanged(position, client);
    }

    public Client getClientAtPosition(int position) {
        return clientList.get(position);
    }

    public static class ListClientHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        private final TextView nameClient;
        private final OnClientListener onNewEventClientListener;
        private final List<Client> clientList;
        private final OnClientListener onClientListFragmentListener;

        public ListClientHolder(@NonNull View itemView, OnClientListener onNewEventClientListener, List<Client> clientList, OnClientListener onClientListFragmentListener) {
            super(itemView);
            this.nameClient = itemView.findViewById(R.id.tv_name);
            this.onNewEventClientListener = onNewEventClientListener;
            this.clientList = clientList;
            this.onClientListFragmentListener = onClientListFragmentListener;
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void setTextView(Client client) {
            nameClient.setText(client.getName());
        }

        @Override
        public void onClick(View v) {
            onNewEventClientListener.onClientClickNewEvent(clientList.get(getAdapterPosition()));
            onClientListFragmentListener.onClientClickRemoveFragment();
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 1, 0, ITEM_MENU_EDIT);
            menu.add(this.getAdapterPosition(), 2, 1, ITEM_MENU_DELETE);
        }
    }

    public interface OnClientListener {
        void onClientClickNewEvent(Client client);
        void onClientClickRemoveFragment();
    }
}
