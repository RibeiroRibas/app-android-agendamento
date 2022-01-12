package br.com.beautystyle.ui.adapter.recyclerview;

import android.content.Context;
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

import br.com.beautystyle.model.Expenses;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CoinUtil;

public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ExpenseViewHolder> {

    private final List<Expenses> expenseList;
    private final Context context;

    public ExpenseListAdapter(List<Expenses> expenseList, Context context) {
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
            Expenses expense = expenseList.get(position);
            holder.setTextView(expense);
        } else {
            holder.setLayoutParamCardView();
        }
    }

    @Override
    public int getItemCount() {
        return expenseList.size() + 2;
    }

    public void publishResultsNew(Expenses expense, int monthValue) {
        expenseList.add(expense);
        if (expense.getDate().getMonthValue() == monthValue)
            notifyItemInserted(expenseList.indexOf(expense));
    }

    public void publishResultsFilteredList(List<Expenses> filteredList) {
        if (filteredList.isEmpty()) {
            removeItemRange();
        } else {
            removeItemRange();
            insertItemRange(filteredList);
        }
    }

    private void insertItemRange(List<Expenses> filteredList) {
        expenseList.addAll(filteredList);
        int size2 = filteredList.size();
        notifyItemRangeInserted(0, size2);
    }

    private void removeItemRange() {
        if (!expenseList.isEmpty()) {
            int size = expenseList.size();
            expenseList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {

        private final TextView date, value, category, description;
        private final CardView cardView;
        private final View itemView;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.item_expense_date_tv);
            value = itemView.findViewById(R.id.item_expense_value_tv);
            category = itemView.findViewById(R.id.item_expense_category_tv);
            description = itemView.findViewById(R.id.item_expense_description_tv);
            cardView = itemView.findViewById(R.id.item_expense_cardView);
            this.itemView = itemView;
        }

        public void setTextView(Expenses expense) {
            String formatedDate = CalendarUtil.formatDate(expense.getDate());
            date.setText(formatedDate);
            String formatedValue = CoinUtil.formatBr(expense.getPrice());
            value.setText(formatedValue);
            category.setText(expense.getCategory().getDescription());
            description.setText(expense.getDescription());
        }

        private void setLayoutParamCardView() {
            itemView.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams cardViewParams = cardView.getLayoutParams();
            cardViewParams.height = 100;
            cardView.setLayoutParams(cardViewParams);
        }
    }
}
