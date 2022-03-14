package br.com.beautystyle.ui.adapter.recyclerview.listener;

import br.com.beautystyle.model.Client;
import br.com.beautystyle.model.Expense;

public class AdapterListener {

    public interface OnExpenseClickListener{
        void onItemClick(Expense expense, int position);
    }

    public interface OnClientClickListener{
        void onItemClick(Client client, int position);
    }
}
