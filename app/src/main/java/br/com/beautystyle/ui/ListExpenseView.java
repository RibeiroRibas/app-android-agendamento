package br.com.beautystyle.ui;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_EDIT_EXPENSE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.dao.ExpenseDao;
import br.com.beautystyle.model.Expense;
import br.com.beautystyle.ui.activity.NewExpenseActivity;
import br.com.beautystyle.ui.adapter.recyclerview.ExpenseListAdapter;
import br.com.beautystyle.ui.adapter.recyclerview.listener.OnItemClickListener;

public class ListExpenseView {

    private final ExpenseListAdapter adapter;
    private final ExpenseDao dao;
    private final Context context;

    public ListExpenseView(Context context) {
        this.context = context;
        this.dao = new ExpenseDao();
        List<Expense> filteredList = dao.listByDate(LocalDate.now().getMonthValue());
        this.adapter = new ExpenseListAdapter(filteredList, context);

    }

    public void setAdapter(RecyclerView expenseList) {
        expenseList.setAdapter(adapter);
        adapter.publishAll();
    }

    public void publishResultsChangedList(int monthValue, int year) {
        List<Expense> expensesList = new ArrayList<>(dao.listByDate(monthValue, year));
        adapter.publishResultsChangedList(expensesList);
    }

    public void checkRemove(MenuItem item) {
        new AlertDialog
                .Builder(context)
                .setTitle("Removendo o gasto selecionado")
                .setMessage("Tem certeza que deseja remover o item selecionado?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    Expense selectedExpense = adapter.getItem(item.getGroupId());
                    dao.remove(selectedExpense);
                    adapter.publishResultsRemoved(selectedExpense, item.getGroupId());
                })
                .setNegativeButton("Não", null)
                .show();
    }

    public void save(Expense expense, int monthValue, int year) {
        dao.save(expense);
        adapter.publishResultsNew(expense, monthValue, year);
    }

    public void edit(Expense expense, int position) {
        dao.edit(expense);
        adapter.publishResultsChanged(expense, position);
    }

    public void setOnItemClickListener(ActivityResultLauncher<Intent> activityResultLauncher) {
        adapter.setOnItemClickListener((expense, position) -> {
            Intent intent = new Intent(context, NewExpenseActivity.class);
            intent.putExtra(KEY_EDIT_EXPENSE, expense);
            intent.putExtra(KEY_POSITION, position);
            activityResultLauncher.launch(intent);
        });
    }
}
