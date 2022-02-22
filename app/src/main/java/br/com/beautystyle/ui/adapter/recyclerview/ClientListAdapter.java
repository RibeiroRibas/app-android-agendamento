package br.com.beautystyle.ui.adapter.recyclerview;

import static br.com.beautystyle.ui.adapter.ConstantsAdapter.ITEM_MENU_REMOVE;
import static br.com.beautystyle.ui.adapter.ConstantsAdapter.ITEM_MENU_UPDATE;

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

import br.com.beautystyle.domain.model.Client;
import br.com.beautystyle.ui.adapter.recyclerview.listener.AdapterListener;

public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.ListClientHolder> {

    private final List<Client> clientList;
    private final List<Client> clientListAll;
    private final Context context;
    private AdapterListener.OnClientClickListener onItemClickListener;

    public ClientListAdapter(Context context) {
        this.context = context;
        this.clientList = new ArrayList<>();
        this.clientListAll = new ArrayList<>();
    }

    @NonNull
    @Override
    public ListClientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View createdView = LayoutInflater.from(context).inflate(R.layout.item_service_and_client, parent, false);
        return new ListClientHolder(createdView);
    }

    @Override
    public void onBindViewHolder(@NonNull ListClientHolder holder, int position) {
        if (!clientList.isEmpty()) {
            Client client = clientList.get(position);
            holder.SetClientName(client);
        }
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }

    public void setOnItemClickListener(AdapterListener.OnClientClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void filteredClient(String newText) {
        List<Client> filteredClientLIst = new ArrayList<>();
        if (newText.isEmpty()) {
            itemInserted(filteredClientLIst);
        } else {
            itemRemoved(newText, filteredClientLIst);
        }
    }

    private void itemRemoved(String newText, List<Client> filteredClientList) {
        for (Client client : clientList) {
            if (!client.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredClientList.add(client);
            }
        }
        for (Client client : filteredClientList) {
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

    public void publishResultsInsert(Client client) {
        clientList.add(client);
        clientListAll.add(client);
        notifyItemInserted(clientList.indexOf(client));
    }

    public void publishClientList(List<Client> clientList) {
        int size = this.clientList.isEmpty() ? 0 : this.clientList.size();
        this.clientList.addAll(clientList);
        clientListAll.addAll(clientList);
        notifyItemRangeInserted(size, this.clientList.size());
    }

    public void publishResultsRemoved(Client client, int position) {
        clientList.remove(client);
        clientListAll.remove(client);
        notifyItemRemoved(position);
    }

    public void publishResultsUpdate(Client client, int position) {
        clientList.set(position, client);
        clientListAll.set(position, client);
        notifyItemChanged(position, client);
    }

    public Client getClientAtPosition(int position) {
        return clientList.get(position);
    }

    class ListClientHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private final TextView nameClient;
        private Client client;

        public ListClientHolder(@NonNull View itemView) {
            super(itemView);
            this.nameClient = itemView.findViewById(R.id.tv_name);

            onClickListener();
            itemView.setOnCreateContextMenuListener(this);
        }

        private void onClickListener() {
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(client,getAdapterPosition()));
        }

        public void SetClientName(Client client) {
            this.client = client;
            nameClient.setText(client.getName());
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 1, 0, ITEM_MENU_UPDATE);
            menu.add(this.getAdapterPosition(), 2, 1, ITEM_MENU_REMOVE);
        }
    }
}
