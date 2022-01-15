package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.activity.ContantsEventActivity.REQUEST_CODE_EDIT_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_EVENT;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.time.LocalDate;

import br.com.beautystyle.model.Event;
import br.com.beautystyle.ui.ListDaysView;
import br.com.beautystyle.ui.ListEventView;
import br.com.beautystyle.ui.activity.NewEventActivity;
import br.com.beautystyle.ui.adapter.recyclerview.ListDaysAdaper;
import br.com.beautystyle.util.CalendarUtil;

public class EventListFragment extends Fragment implements ListDaysAdaper.OnDayListener {

    private ListView eventList;
    private ListEventView listEventView;
    private ListDaysView listDaysView;
    private TextView monthAndYear;
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listEventView = new ListEventView(this.getActivity());
        listDaysView = new ListDaysView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_list_event, container, false);

        CalendarUtil.selectedDate = LocalDate.now();

        initWidgets(inflatedView);

        setListDaysAdapter(inflatedView);// onClickListener in ListDaysAdapter
        setEventListAdapter();
        eventListOnClickListener(inflatedView);

        monthAndYear.setText(CalendarUtil.formatMonthYear(CalendarUtil.selectedDate));

        registerActivityResult();

        return inflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();
        listEventView.eventUpdate();

    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.fragment_list_event_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        listEventView.checkRemove(item);
        return super.onContextItemSelected(item);
    }

    private void initWidgets(View inflatedView) {
        eventList = inflatedView.findViewById(R.id.list_event_listview);
        monthAndYear = inflatedView.findViewById(R.id.day_of_month_textview);
    }

    private void setListDaysAdapter(View inflatedView) {
        RecyclerView dayOfMonth = inflatedView.findViewById(R.id.days_of_month_recycler_view);
        listDaysView.setAdapter(dayOfMonth);
    }

    private void setEventListAdapter() {
        registerForContextMenu(eventList);
        listEventView.setAdapter(eventList);
    }

    private void eventListOnClickListener(View inflatedView) {
        eventList.setOnItemClickListener((adapter, view, position, id) -> {
            Intent goToNewEventActivityEditMode = new Intent(requireActivity(), NewEventActivity.class);
            Event event = (Event) adapter.getItemAtPosition(position);
            goToNewEventActivityEditMode.putExtra(KEY_EVENT, event);
            activityResultLauncher.launch(goToNewEventActivityEditMode);
        });
    }

    @Override
    public void onDayClick(LocalDate date, int position) {
        CalendarUtil.selectedDate = date;
        listDaysView.changeScrollPosition(position);
        listEventView.eventUpdate();
    }

    @Override
    public void onDayBinding(LocalDate date) {
        monthAndYear.setText(CalendarUtil.formatMonthYear(date));
    }

    private void registerActivityResult() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            if (intent != null) {
                if (result.getResultCode() == REQUEST_CODE_EDIT_EVENT) {
                    Event event = (Event) intent.getSerializableExtra("editEvent");
                    listEventView.edit(event);
                } else {
                    Event event = (Event) intent.getSerializableExtra("newEvent");
                    listEventView.save(event);
                }
            }
        });
    }
}