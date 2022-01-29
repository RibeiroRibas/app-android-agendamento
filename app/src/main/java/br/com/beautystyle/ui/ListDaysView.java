package br.com.beautystyle.ui;

import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;

import br.com.beautystyle.ui.adapter.recyclerview.ListDaysAdaper;

public class ListDaysView {

    private ListDaysAdaper adapter;
    private RecyclerView dayOfMonth;
    private static int toPosition;

    public void setAdapter(RecyclerView dayOfMonth, ListDaysAdaper.OnDayListener context) {
        this.adapter = new ListDaysAdaper(context);
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

    public void getScrollPosition(LocalDate date) {
        int position = adapter.getPosition(date);
        adapter.onClickViewHolder(date, position);
    }
}
