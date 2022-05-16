package br.com.beautystyle.ui.adapter.recyclerview;

import android.app.Application;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.model.Report;
import br.com.beautystyle.model.entities.Client;
import br.com.beautystyle.model.entities.Event;
import br.com.beautystyle.model.entities.Expense;
import br.com.beautystyle.repository.ClientRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CoinUtil;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ReportViewHolder> {

    private final List<Report> reportList;
    private final Application context;

    public ReportListAdapter(Application context) {
        this.reportList = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
        return new ReportViewHolder(inflatedView, context);
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

    public void publishResultsFilteredList(List<Report> filteredList) {
        if (filteredList.isEmpty()) {
            removeItemRange();
        } else {
            removeItemRange();
            insertItemRange(filteredList);
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
        private final ClientRepository repository;
        private final Application context;

        public ReportViewHolder(@NonNull View itemView, Application context) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.item_report_cv);
            this.date = itemView.findViewById(R.id.item_report_date_tv);
            this.name = itemView.findViewById(R.id.item_report_name_tv);
            this.value = itemView.findViewById(R.id.item_report_value_tv);
            this.repository = new ClientRepository(context);
            this.context=context;
        }

        public void onBindReport(Report report) {
            if (report.getEvent() != null) {
                Event event = report.getEvent();
                onBindClient(event);
                onBindEvent(event);
                setTextColor("#228C22");
            } else {
                Expense expense = report.getExpense();
                onBindExpense(expense);
                setTextColor("#FF0000");
            }
        }

        private void onBindClient(Event event) {
            repository.getById(event.getClient(), new ResultsCallBack<Client>() {
                @Override
                public void onSuccess(Client client) {
                    name.setText(client.getName());
                }

                @Override
                public void onError(String erro) {
                    showError(erro);
                }
            });
        }

        private void showError(String message) {
            Toast.makeText(context,
                    message,
                    Toast.LENGTH_LONG).show();
        }

        private void onBindEvent(Event event) {
            String formatedDate = CalendarUtil.formatDate(event.getEventDate());
            date.setText(formatedDate);
            String formatedValue = CoinUtil.formatBr(event.getValueEvent());
            value.setText(formatedValue);
        }

        private void onBindExpense(Expense expense) {
            String formatedDate = CalendarUtil.formatDate(expense.getDate());
            date.setText(formatedDate);
            name.setText(expense.getCategory().getDescription());
            String formatedValue = CoinUtil.formatBr(expense.getPrice());
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
