package br.com.beautystyle.ui;

import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;

import br.com.beautystyle.ui.adapter.recyclerview.ListDaysAdapter;

public class ListDaysView{

    private ListDaysAdapter adapter;
    private RecyclerView dayOfMonth;
    private static int toPosition;

    public void setAdapter(RecyclerView dayOfMonth, ListDaysAdapter.OnDayListener context) {
        this.adapter = new ListDaysAdapter(context);
        this.dayOfMonth = dayOfMonth;
        dayOfMonth.setAdapter(adapter);
        adapter.publishAllDays();
        toPosition = 363; //start position default
        dayOfMonth.scrollToPosition(toPosition);
    }

    public void changeScrollPosition(int fromPosition) {
        int position = toPosition < fromPosition ? fromPosition + 2 : fromPosition - 2;
        dayOfMonth.scrollToPosition(position);
        toPosition = fromPosition;
    }

    public void toScrollPosition(LocalDate date) {
        int position = getPosition(date);
        adapter.onClickViewHolder(date, position);
    }
    public int getPosition(LocalDate date){
        return adapter.getPosition(date);
    }

}
