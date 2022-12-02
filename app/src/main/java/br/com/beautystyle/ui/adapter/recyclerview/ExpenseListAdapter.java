package br.com.beautystyle.ui.adapter.recyclerview;

import static br.com.beautystyle.ui.adapter.ConstantsAdapter.ITEM_MENU_REMOVE;
import static br.com.beautystyle.util.ConstantsUtil.DD_MM_YYYY;
import static br.com.beautystyle.util.ConstantsUtil.DESIRED_FORMAT;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.ui.adapter.recyclerview.listener.AdapterListener;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CoinUtil;

public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder> {

    private final List<Expense> expenseList;
    private final Context context;
    private AdapterListener.OnExpenseClickListener onItemClickListener;

    public ExpenseListAdapter(Context context) {
        this.expenseList = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        if (position < expenseList.size()) {
            Expense expense = expenseList.get(position);
            holder.onBindExpense(expense);
            holder.setLayoutParamCardView(View.VISIBLE, 250);
        } else {
            holder.setLayoutParamCardView(View.INVISIBLE, 100);
        }
    }

    @Override
    public int getItemCount() {
        return expenseList.size() + 2;
    }

    public void publishResultsChangedList(List<Expense> expenseList) {
        removeItemRange();
        if (!expenseList.isEmpty())
            insertItemRange(expenseList);
    }

    private void insertItemRange(List<Expense> expenses) {
        expenseList.addAll(expenses);
        notifyItemRangeInserted(0, expenses.size());
    }

    private void removeItemRange() {
        if (!expenseList.isEmpty()) {
            int size = expenseList.size();
            expenseList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    public void setOnItemClickListener(AdapterListener.OnExpenseClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public Expense getItem(int position) {
        return expenseList.get(position);
    }

    public void publishResultsRemoved( int position) {
        expenseList.remove(position);
        notifyItemRemoved(position);
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView date, value, category, description;
        private CardView cardView;
        private final View itemView;
        private Expense expense;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            initWidgets();
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(v ->
                    onItemClickListener.onItemClick(expense, getAdapterPosition())
            );
        }

        private void initWidgets() {
            date = itemView.findViewById(R.id.item_expense_date_tv);
            value = itemView.findViewById(R.id.item_expense_value_tv);
            category = itemView.findViewById(R.id.item_expense_category_tv);
            description = itemView.findViewById(R.id.item_expense_description_tv);
            cardView = itemView.findViewById(R.id.item_expense_cardView);
        }

        public void onBindExpense(Expense expense) {
            this.expense = expense;
            String formattedDate = CalendarUtil.formatLocalDate(expense.getExpenseDate(), DD_MM_YYYY);
            date.setText(formattedDate);
            String formattedValue = CoinUtil.format(expense.getValue(), DESIRED_FORMAT);
            value.setText(formattedValue);
            category.setText(expense.getCategory());
            description.setText(expense.getDescription());
        }

        public void setLayoutParamCardView(int visibility, int height) {
            itemView.setVisibility(visibility);
            ViewGroup.LayoutParams cardViewParams = cardView.getLayoutParams();
            cardViewParams.height = height;
            cardView.setLayoutParams(cardViewParams);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 1, 1, ITEM_MENU_REMOVE);
        }
    }
}
