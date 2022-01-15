package br.com.beautystyle.ui;

import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;

import br.com.beautystyle.ui.adapter.recyclerview.ListDaysAdaper;
import br.com.beautystyle.util.CalendarUtil;

public class ListDaysView {

    private ListDaysAdaper.OnDayListener context;
    private final ListDaysAdaper adapter;
    private RecyclerView dayOfMonth;
    private static int toPosition;

    public ListDaysView(ListDaysAdaper.OnDayListener context) {
        this.context = context;
        this.adapter = new ListDaysAdaper(context);
    }


    public void setAdapter(RecyclerView dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
        dayOfMonth.setAdapter(adapter);
        CalendarUtil.selectedDate = LocalDate.now();
        adapter.publishAllDays();
        toPosition = 28;
        dayOfMonth.scrollToPosition(toPosition);
    }

    public void changeScrollPosition(int fromPosition) {
        int position = toPosition < fromPosition ? fromPosition + 2 : fromPosition - 2;
        dayOfMonth.scrollToPosition(position);
        toPosition = fromPosition;
    }
}
