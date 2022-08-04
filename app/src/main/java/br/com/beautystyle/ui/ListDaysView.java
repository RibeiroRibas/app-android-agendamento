package br.com.beautystyle.ui;

import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;

import br.com.beautystyle.ui.adapter.recyclerview.ListDaysAdapter;
import br.com.beautystyle.util.CalendarUtil;

public class ListDaysView {

    private ListDaysAdapter adapter;
    private RecyclerView dayOfMonth;
    private int toPosition = 365;

    public void setAdapter(RecyclerView dayOfMonth, ListDaysAdapter.OnDayListener context) {
        this.adapter = new ListDaysAdapter(context);
        dayOfMonth.setAdapter(adapter);
        this.dayOfMonth = dayOfMonth;
        adapter.update();
        changeScrollPosition();
    }

    public void changeScrollPosition() {
        int fromPosition = getPosition(CalendarUtil.selectedDate);
        toPosition = toPosition < fromPosition ? fromPosition + 2 : fromPosition - 2;
        dayOfMonth.scrollToPosition(toPosition);
        toPosition = fromPosition;
    }

    // simula um click na lista de dias
    public void toScrollPosition(LocalDate date) {
        adapter.onClickViewHolder(date);
    }

    public int getPosition(LocalDate date) {
        return adapter.getPosition(date);
    }

}
