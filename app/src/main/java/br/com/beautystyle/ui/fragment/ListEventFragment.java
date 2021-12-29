package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_EVENT;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import br.com.beautystyle.model.Event;
import br.com.beautystyle.ui.activity.NewEventActivity;
import br.com.beautystyle.ui.adapter.ListDaysAdaper;
import br.com.beautystyle.util.CalendarUtil;
import com.example.beautystyle.R;
import br.com.beautystyle.ui.ListEventView;

import java.time.LocalDate;

public class ListEventFragment extends Fragment implements ListDaysAdaper.OnDayListener {

    private View inflateView;
    private ListView listDeEventos;
    private ListEventView listaEventoView;
    private final ListDaysAdaper listDaysAdaper = new ListDaysAdaper(this);
    private TextView monthAndYear;
    private RecyclerView dayOfMonth;
    private Button buttonPrevious, buttonNext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listaEventoView = new ListEventView(this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflateView = inflater.inflate(R.layout.fragment_list_event, container, false);

        CalendarUtil.selectedDate = LocalDate.now();

        initWidgets();

        setListDaysAdapter();
        setListEventAdapter();
        listEventOnClickListener();
        previousWeekAction();
        nextWeekAction();

        return inflateView;
    }

    @Override
    public void onResume() {
        super.onResume();
        listaEventoView.eventUpdate();
        setDaysView();
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.fragment_list_event_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        listaEventoView.checkRemove(item);
        return super.onContextItemSelected(item);
    }

    private void initWidgets() {
        listDeEventos = inflateView.findViewById(R.id.list_event_listview);
        monthAndYear = inflateView.findViewById(R.id.day_of_month_textview);
        dayOfMonth = inflateView.findViewById(R.id.days_of_month_recycler_view);
        buttonNext = inflateView.findViewById(R.id.button_next);
        buttonPrevious = inflateView.findViewById(R.id.button_preview);
    }

    private void setDaysView() {
        listDaysAdaper.atualizaAdapter();
        monthAndYear.setText(CalendarUtil.formatMonthYear(CalendarUtil.selectedDate));
    }

    private void setListDaysAdapter() {
        dayOfMonth.setAdapter(listDaysAdaper);
    }

    private void setListEventAdapter() {
        registerForContextMenu(listDeEventos);
        listaEventoView.setAdapter(listDeEventos);
    }

    private void listEventOnClickListener() {
        listDeEventos.setOnItemClickListener((adapter, view, position, id) -> {
            Event event = (Event) adapter.getItemAtPosition(position);
            event.setEventDate(CalendarUtil.selectedDate);
            startNewEventActivityEditMode(event);
        });
    }


    private void startNewEventActivityEditMode(Event event) {
        Intent goToNewEventActivityEditMode = new Intent(getActivity(), NewEventActivity.class);
        goToNewEventActivityEditMode.putExtra(KEY_EVENT, event);
        startActivity(goToNewEventActivityEditMode);
    }

    public void previousWeekAction() {
        buttonPrevious.setOnClickListener(V -> {
            CalendarUtil.selectedDate = CalendarUtil.selectedDate.minusDays(5);
            setDaysView();
            listaEventoView.eventUpdate();
        });
    }

    public void nextWeekAction() {
        buttonNext.setOnClickListener(V -> {
            CalendarUtil.selectedDate = CalendarUtil.selectedDate.plusDays(5);
            setDaysView();
            listaEventoView.eventUpdate();
        });
    }

    @Override
    public void onDayClick(LocalDate date) {
        CalendarUtil.selectedDate = date;
        setDaysView();
        listaEventoView.eventUpdate();
    }
}