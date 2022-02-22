package br.com.beautystyle.ui.adapter.recyclerview.listener;

import br.com.beautystyle.domain.model.Client;
import br.com.beautystyle.domain.model.Expense;

public class AdapterListener {

    public interface OnExpenseClickListener{
        void onItemClick(Expense expense, int position);
    }

    public interface OnClientClickListener{
        void onItemClick(Client client, int position);
    }
}
