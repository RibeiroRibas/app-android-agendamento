package br.com.beautystyle.ui.adapter.recyclerview;

import static br.com.beautystyle.util.ConstantsUtil.DD_MM_YYYY;
import static br.com.beautystyle.util.ConstantsUtil.DESIRED_FORMAT;

import android.content.Context;
import android.graphics.Color;
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

import br.com.beautystyle.model.util.Report;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CoinUtil;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ReportViewHolder> {

    private final List<Report> reportList;
    private final Context context;

    public ReportListAdapter(Context context) {
        this.reportList = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        if (position < reportList.size()) {
            Report report = reportList.get(position);
            holder.onBindReport(report);
            holder.setLayoutParamCardView(View.VISIBLE, 60);
        } else {
            holder.setLayoutParamCardView(View.INVISIBLE, 100);
        }
    }

    @Override
    public int getItemCount() {
        return reportList.size() + 2;
    }

    public void update(List<Report> reportlist) {
        if (reportlist.isEmpty()) {
            removeItemRange();
        } else {
            removeItemRange();
            insertItemRange(reportlist);
        }
    }

    private void insertItemRange(List<Report> filteredList) {
        reportList.addAll(filteredList);
        notifyItemRangeInserted(0, filteredList.size());
    }

    public void removeItemRange() {
        if (!reportList.isEmpty()) {
            int size = reportList.size();
            reportList.clear();
            notifyItemRangeRemoved(0, size);
        }
    }

    static class ReportViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardView;
        private final TextView date, name, value;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.item_report_cv);
            this.date = itemView.findViewById(R.id.item_report_date_tv);
            this.name = itemView.findViewById(R.id.item_report_name_tv);
            this.value = itemView.findViewById(R.id.item_report_value_tv);
        }

        public void onBindReport(Report report) {
            if (report.getClientName() != null) {
                onBindClient(report);
                onBindEvent(report);
                setTextColor("#228C22");
            } else {
                onBindExpense(report);
                setTextColor("#FF0000");
            }
        }

        private void onBindClient(Report event) {
            name.setText(event.getClientName());
        }

        private void onBindEvent(Report event) {
            String formatedDate = CalendarUtil.formatLocalDate(event.getDate(),DD_MM_YYYY);
            date.setText(formatedDate);
            String formatedValue = CoinUtil.format(event.getEventValue(),DESIRED_FORMAT);
            value.setText(formatedValue);
        }

        private void onBindExpense(Report expense) {
            String formatedDate = CalendarUtil.formatLocalDate(expense.getDate(),DD_MM_YYYY);
            date.setText(formatedDate);
            name.setText(expense.getExpenseCategory());
            String formatedValue = CoinUtil.format(expense.getExpenseValue(),DESIRED_FORMAT);
            value.setText(formatedValue);
        }

        private void setTextColor(String color) {
            value.setTextColor(Color.parseColor(color));
            date.setTextColor(Color.parseColor(color));
            name.setTextColor(Color.parseColor(color));
        }

        public void setLayoutParamCardView(int visibility, int height) {
            itemView.setVisibility(visibility);
            ViewGroup.LayoutParams cardViewParams = cardView.getLayoutParams();
            cardViewParams.height = height;
            cardView.setLayoutParams(cardViewParams);
        }
    }
}
