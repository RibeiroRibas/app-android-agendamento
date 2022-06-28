package br.com.beautystyle.ui;

import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;

import br.com.beautystyle.ui.adapter.recyclerview.ListDaysAdapter;

public class ListDaysView{

    private ListDaysAdapter adapter;
    private RecyclerView dayOfMonth;
    private int toPosition = 365;

    public void setAdapter(RecyclerView dayOfMonth, ListDaysAdapter.OnDayListener context) {
        this.adapter = new ListDaysAdapter(context);
        dayOfMonth.setAdapter(adapter);
        adapter.publishAllDays();
        this.dayOfMonth = dayOfMonth;
        this.dayOfMonth.scrollToPosition(toPosition);
    }

    public void changeScrollPosition(LocalDate eventDate) {
        int fromPosition = getPosition(eventDate);
        toPosition = toPosition < fromPosition ? fromPosition + 2 : fromPosition - 2;
        dayOfMonth.scrollToPosition(toPosition);
        toPosition = fromPosition;
    }

    public void toScrollPosition(LocalDate date) {
        adapter.onClickViewHolder(date);
    }
    public int getPosition(LocalDate date){
        return adapter.getPosition(date);
    }

}
