package br.com.beautystyle.ui.adapter.recyclerview;

import static br.com.beautystyle.ui.adapter.ConstantsAdapter.ITEM_MENU_DELETE;

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

import br.com.beautystyle.model.Expense;
import br.com.beautystyle.ui.adapter.recyclerview.listener.OnItemClickListener;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CoinUtil;

public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder> {

    private final List<Expense> expenseList;
    private final Context context;
    private OnItemClickListener onItemClickListener;

    public ExpenseListAdapter(List<Expense> expenseList, Context context) {
        this.expenseList = new ArrayList<>(expenseList);
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
            holder.setTextView(expense);
            holder.setLayoutParamCardView(View.VISIBLE,250);
        } else {
            holder.setLayoutParamCardView(View.INVISIBLE,100);
        }
    }

    @Override
    public int getItemCount() {
        return expenseList.size() + 2;
    }

    public void publishResultsNew(Expense expense, int monthValue, int year) {
        expenseList.add(expense);
        if (expense.getDate().getMonthValue() == monthValue && expense.getDate().getYear()==year)
            notifyItemInserted(expenseList.indexOf(expense));
    }

    public void publishResultsChangedList(List<Expense> expenseList) {
        removeItemRange();
        if (!expenseList.isEmpty())
            insertItemRange(expenseList);
    }

    private void insertItemRange(List<Expense> filteredList) {
        expenseList.addAll(filteredList);
        notifyItemRangeInserted(0, filteredList.size());
    }

    private void removeItemRange() {
        if (!expenseList.isEmpty()) {
            int size = expenseList.size();
            expenseList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void publishResultsChanged(Expense expense, int position) {
        expenseList.set(position,expense);
        notifyItemChanged(position);
    }

    public Expense getItem(int position) {
        return expenseList.get(position);
    }

    public void publishResultsRemoved(Expense selectedExpense, int position) {
        expenseList.remove(selectedExpense);
        notifyItemRemoved(position);
    }

    public void publishAll(){
        notifyItemRangeInserted(0, expenseList.size());
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder implements  View.OnCreateContextMenuListener{

        private final TextView date, value, category, description;
        private final CardView cardView;
        private final View itemView;
        private Expense expense;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.item_expense_date_tv);
            value = itemView.findViewById(R.id.item_expense_value_tv);
            category = itemView.findViewById(R.id.item_expense_category_tv);
            description = itemView.findViewById(R.id.item_expense_description_tv);
            cardView = itemView.findViewById(R.id.item_expense_cardView);
            this.itemView = itemView;
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick(expense,getAdapterPosition()));
        }

        public void setTextView(Expense expense) {
            this.expense = expense;
            String formatedDate = CalendarUtil.formatDate(expense.getDate());
            date.setText(formatedDate);
            String formatedValue = CoinUtil.formatBr(expense.getPrice());
            value.setText(formatedValue);
            category.setText(expense.getCategory().getDescription());
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
            menu.add(this.getAdapterPosition(), 1, 1, ITEM_MENU_DELETE);
        }
    }
}
