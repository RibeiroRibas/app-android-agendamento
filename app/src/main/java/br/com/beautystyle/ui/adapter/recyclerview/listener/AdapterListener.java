package br.com.beautystyle.ui.adapter.recyclerview.listener;

import br.com.beautystyle.model.entities.Client;
import br.com.beautystyle.model.entities.Expense;

public class AdapterListener {

    public interface OnExpenseClickListener{
        void onItemClick(Expense expense, int position);
    }

    public interface OnClientClickListener{
        void onItemClick(Client client, int position);
    }
}
