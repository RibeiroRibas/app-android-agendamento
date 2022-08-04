package br.com.beautystyle.ui.adapter.recyclerview.listener;

import br.com.beautystyle.model.entity.Category;
import br.com.beautystyle.model.entity.Costumer;
import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.model.entity.Job;

public class AdapterListener {

    public interface OnExpenseClickListener {
        void onItemClick(Expense expense, int position);
    }

    public interface OnClientClickListener {
        void onItemClick(Costumer costumer);
    }

    public interface OnJobClickListener {
        void onItemClick(Job job, boolean isSelected);
    }

    public interface OnCategoryClickListener {
        void onItemClick(String name);
    }

    public interface OnCategoryLongClickListener {
        boolean onItemClick(Category category);
    }
}
