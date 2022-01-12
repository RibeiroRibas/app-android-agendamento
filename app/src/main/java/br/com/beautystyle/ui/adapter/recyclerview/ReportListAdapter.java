package br.com.beautystyle.ui.adapter.recyclerview;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
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

import br.com.beautystyle.model.Event;
import br.com.beautystyle.model.Expenses;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CoinUtil;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ReportViewHolder> {

    private final List<Object> reportList;
    private final Context context;

    public ReportListAdapter(List<Object> reportList, Context context) {
        this.reportList = new ArrayList<>(reportList);
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

            Object report = reportList.get(position);
            holder.setTextView(report);
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }
    public void publishResultsFilteredList(List<Object> filteredList) {
        if(filteredList.isEmpty()){
            removeItemRange();
        }else{
            removeItemRange();
            insertItemRange(filteredList);
        }
    }

    private void insertItemRange(List<Object> filteredList) {
        reportList.addAll(filteredList);
        int size2 = filteredList.size();
        notifyItemRangeInserted(0,size2);
    }

    private void removeItemRange() {
        if(!reportList.isEmpty()){
            int size = reportList.size();
            reportList.clear();
            notifyItemRangeRemoved(0,size);
        }
    }
    static class ReportViewHolder extends RecyclerView.ViewHolder {

        private final CardView cardView;
        private final TextView date, name,value;

        public ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            this.cardView = itemView.findViewById(R.id.item_report_cv);
            this.date = itemView.findViewById(R.id.item_report_date_tv);
            this.name = itemView.findViewById(R.id.item_report_name_tv);
            this.value = itemView.findViewById(R.id.item_report_value_tv);

        }


        public void setTextView(Object report) {
            Log.i(TAG, "setTextView: " + report);

            if(report instanceof Event){
                Event event = (Event) report;
                String formatedDate = CalendarUtil.formatDate(event.getEventDate());
                date.setText(formatedDate);
                name.setText(event.getClient().getName());
                String formatedValue = CoinUtil.formatBr(event.getValueEvent());
                value.setText(formatedValue);
            }else{
                Expenses expense = (Expenses) report;
                String formatedDate = CalendarUtil.formatDate(expense.getDate());
                date.setText(formatedDate);
                name.setText(expense.getCategory().getDescription());
                String formatedValue = CoinUtil.formatBr(expense.getPrice());
                value.setText(formatedValue);
                setTextColorRed();
            }

        }

        private void setTextColorRed() {
            value.setTextColor(Color.parseColor("#FF0000"));
            date.setTextColor(Color.parseColor("#FF0000"));
            name.setTextColor(Color.parseColor("#FF0000"));
        }

        public void setLayoutParamCardView() {
                itemView.setVisibility(View.INVISIBLE);
                ViewGroup.LayoutParams cardViewParams = cardView.getLayoutParams();
                cardViewParams.height = 100;
                cardView.setLayoutParams(cardViewParams);
        }
    }
}
