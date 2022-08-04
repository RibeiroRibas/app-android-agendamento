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

import br.com.beautystyle.model.entity.Costumer;
import br.com.beautystyle.ui.adapter.recyclerview.listener.AdapterListener;
import br.com.beautystyle.util.SortByClientName;

public class ClientListAdapter extends RecyclerView.Adapter<ClientListAdapter.ListClientHolder> {

    private final List<Costumer> costumers;
    private final List<Costumer> costumersToFilter;
    private final Context context;
    private AdapterListener.OnClientClickListener onItemClickListener;

    public ClientListAdapter(Context context) {
        this.context = context;
        this.costumers = new ArrayList<>();
        this.costumersToFilter = new ArrayList<>();
    }

    @NonNull
    @Override
    public ListClientHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View createdView = inflateLayout(parent);
        return new ListClientHolder(createdView);
    }

    private View inflateLayout(@NonNull ViewGroup parent) {
        return LayoutInflater.from(context)
                .inflate(R.layout.item_job_and_client, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull ListClientHolder holder, int position) {
        if (!costumers.isEmpty()) {
            Costumer costumer = costumers.get(position);
            holder.setClientName(costumer);
        }
    }

    @Override
    public int getItemCount() {
        return costumers.size();
    }

    public void setOnItemClickListener(AdapterListener.OnClientClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void filterSearchView(String newText) {
        if (newText.isEmpty()) {
            itemRangeInserted();
        } else {
            itemRemoved(newText);
        }
    }

    private void itemRemoved(String newText) {
        List<Costumer> filteredCostumers = filterByInputText(newText);
        for (Costumer costumer : filteredCostumers) {
            int index = costumers.indexOf(costumer);
            costumers.remove(costumer);
            notifyItemRemoved(index);
        }
    }

    @NonNull
    private List<Costumer> filterByInputText(String newText) {
        return costumers.stream()
                .filter(client ->
                        !client.getName().toLowerCase()
                                .contains(newText.toLowerCase())
                )
                .collect(Collectors.toList());
    }

    private void itemRangeInserted() {
        notifyItemRangeRemoved(0, this.costumers.size());
        this.costumers.clear();
        this.costumers.addAll(costumersToFilter);
        notifyItemRangeInserted(0, this.costumers.size());
    }

    public void update(List<Costumer> costumers) {
        notifyItemRangeRemoved(0, this.costumers.size());
        this.costumers.clear();
        costumersToFilter.clear();
        this.costumers.addAll(costumers);
        this.costumers.sort(new SortByClientName());
        costumersToFilter.addAll(this.costumers);
        notifyItemRangeInserted(0, this.costumers.size());
    }

    public Costumer getClientAtPosition(int position) {
        return costumers.get(position);
    }

    class ListClientHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private final TextView costumerNameTv;
        private Costumer costumer;

        public ListClientHolder(@NonNull View itemView) {
            super(itemView);
            this.costumerNameTv = itemView.findViewById(R.id.tv_name);

            onClickListener();
            itemView.setOnCreateContextMenuListener(this);
        }

        private void onClickListener() {
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(costumer));
        }

        public void setClientName(Costumer costumer) {
            this.costumer = costumer;
            costumerNameTv.setText(costumer.getName());
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 1, 0, ITEM_MENU_UPDATE);
            menu.add(this.getAdapterPosition(), 2, 1, ITEM_MENU_REMOVE);
        }
    }
}
