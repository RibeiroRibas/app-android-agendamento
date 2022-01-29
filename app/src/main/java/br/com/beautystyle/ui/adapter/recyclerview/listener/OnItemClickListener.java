package br.com.beautystyle.ui.adapter.recyclerview.listener;

import br.com.beautystyle.model.Expense;

public interface OnItemClickListener {
    void onItemClick(Expense expense, int position);
}
