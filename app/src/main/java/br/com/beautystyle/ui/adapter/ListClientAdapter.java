package br.com.beautystyle.ui.adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.com.beautystyle.model.Client;
import com.example.beautystyle.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListClientAdapter extends RecyclerView.Adapter<ListClientAdapter.ListClientHolder>{

    private final List<Client> listCliente;
    private final List<Client> listClienteAll;
    private final OnClientListener onNewEventClientListener;
    private final OnClientListener onListClientFragmentListener;
    private final Context context;

    public ListClientAdapter(Context context, OnClientListener onNewEventClientListener, OnClientListener onListClientFragmentListener) {
        this.context = context;
        this.listCliente = new ArrayList<>();
        this.listClienteAll = new ArrayList<>();
        this.onNewEventClientListener = onNewEventClientListener;
        this.onListClientFragmentListener = onListClientFragmentListener;
    }

    @NonNull
    @Override
    public ListClientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View createdView = LayoutInflater.from(context).inflate(R.layout.item_list_service, parent, false);
        return new ListClientHolder(createdView, onNewEventClientListener, listCliente, onListClientFragmentListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ListClientHolder holder, int position) {
        if(!listCliente.isEmpty()){
            Client client = listCliente.get(position);
            holder.setTextView(client);
        }
    }

    @Override
    public int getItemCount() {
        return listCliente.size();
    }

    public void filteredClient(String newText) {
        List<Client> filteredListClient = new ArrayList<>();
        if(newText.isEmpty()) {
            itemInserted(filteredListClient);
        }else{
            itemRemoved(newText, filteredListClient);
        }
    }

    private void itemRemoved(String newText, List<Client> filteredListClient) {
        for (Client client : listCliente) {
            if (!client.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredListClient.add(client);
            }
        }
        for (Client client : filteredListClient) {
            int index = listCliente.indexOf(client);
            listCliente.remove(client);
            notifyItemRemoved(index);
        }
    }

    private void itemInserted(List<Client> filteredListClient) {
        if(listCliente.isEmpty()){
          filteredListClient = listClienteAll;
        }
        for (Client c : listCliente) {
            filteredListClient = listClienteAll.stream().filter(client -> client != c).collect(Collectors.toList());
            }
        for (Client c : filteredListClient) {
            listCliente.add(c);
            notifyItemInserted(listCliente.indexOf(c));
        }
    }

    public void publishAllClient(List<Client> listAll) {
        listCliente.clear();
        listCliente.addAll(listAll);
        listClienteAll.addAll(listAll);
        notifyDataSetChanged();
    }

    public void publishResultsNew(Client client) {
        listCliente.add(client);
        listClienteAll.add(client);
        notifyItemInserted(listCliente.indexOf(client));
    }

    public void publishResultsImportedClients(List<Client> newListClient) {
        for (Client c : newListClient) {
            publishResultsNew(c);
        }
    }
    public void publishResultsRemoved(Client client) {
        int position = getPosition(client);
        listCliente.remove(client);
        listClienteAll.remove(client);
        notifyItemRemoved(position);
    }

    public void publishResultsEdited(Client client) {
        int position = getPosition(client);
        listCliente.set(position, client);
        listClienteAll.set(position, client);
        notifyItemChanged(position, client);
    }

    private int getPosition(Client client){
        int position = 0;
        for (Client c : listCliente) {
            if(c.getId()==client.getId()){
                position = listCliente.indexOf(c);
                return position;
            }
        }
        return position;
    }

    public Client getClientAtPosition(int position) {
        return listCliente.get(position);
    }

    public static class ListClientHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

        private final TextView nameClient;
        private final OnClientListener onNewEventClientListener;
        private final List<Client> listClient;
        private final OnClientListener onListClientFragmentListener;

        public ListClientHolder(@NonNull View itemView, OnClientListener onNewEventClientListener, List<Client> listClient, OnClientListener onListClientFragmentListener) {
            super(itemView);
            this.nameClient = itemView.findViewById(R.id.tv_service);
            this.onNewEventClientListener = onNewEventClientListener;
            this.listClient = listClient;
            this.onListClientFragmentListener = onListClientFragmentListener;
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        public void setTextView(Client client) {
            nameClient.setText(client.getName());
        }

        @Override
        public void onClick(View v) {
            onNewEventClientListener.onClientClickNewEvent(listClient.get(getAdapterPosition()));
            onListClientFragmentListener.onClientClickRemoveFragment();
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 1, 0, "Editar");
            menu.add(this.getAdapterPosition(), 2, 1, "Excluir");
        }
    }

    public interface OnClientListener {
        void onClientClickNewEvent(Client client);

        void onClientClickRemoveFragment();
    }
}
