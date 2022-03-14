package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_UPDATE_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_SERVICE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATED_SERVICE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_EVENT;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.ViewModel.ClientViewModel;
import br.com.beautystyle.ViewModel.EventViewModel;
import br.com.beautystyle.ViewModel.EventWithServicesViewModel;
import br.com.beautystyle.model.Event;
import br.com.beautystyle.model.Services;
import br.com.beautystyle.ui.ListDaysView;
import br.com.beautystyle.ui.activity.NewEventActivity;
import br.com.beautystyle.ui.adapter.listview.EventListAdapter;
import br.com.beautystyle.ui.adapter.recyclerview.DaysListAdapter;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CreateListsUtil;
import io.reactivex.rxjava3.disposables.Disposable;

public class EventListFragment extends Fragment implements DaysListAdapter.OnDayListener {

    private ListView eventList;
    private ListDaysView listDaysView;
    private TextView monthAndYear;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private EventListAdapter adapterEventList;
    private  EventViewModel eventViewModel;
    private ClientViewModel clientViewModel;
    private  EventWithServicesViewModel eventWithServicesViewModel;
    private Disposable disposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        clientViewModel = new ViewModelProvider(requireActivity()).get(ClientViewModel.class);
        listDaysView = new ListDaysView();
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        eventWithServicesViewModel = new ViewModelProvider(requireActivity()).get(EventWithServicesViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_list_event, container, false);
        initWidgets(inflatedView);
        setDaysListAdapter(inflatedView);// onClickListener in ListDaysAdapter
        setEventListAdapter();
        eventListOnClickListener();

        monthAndYear.setText(CalendarUtil.formatMonthYear(CalendarUtil.selectedDate));

        registerActivityResult();

        observeEventList();

        return inflatedView;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        checkRemove(item);
        return super.onContextItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        eventViewModel.add(CalendarUtil.selectedDate);
    }

    public void checkRemove(final MenuItem itemId) {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("Removendo item da Agenda")
                .setMessage("Tem certeza que deseja remover o item selecionado?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    AdapterView.AdapterContextMenuInfo menuInfo =
                            (AdapterView.AdapterContextMenuInfo) itemId.getMenuInfo();
                    Event selectedEvent = (Event) adapterEventList.getItem(menuInfo.position);
                    removeEvent(selectedEvent);
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void removeEvent(Event selectedEvent) {
        if (selectedEvent.getEventId() >= 0) {
            eventViewModel.delete(selectedEvent).subscribe();
        } else {
            Toast.makeText(requireActivity(), "Não é possível remover um horário vazio", Toast.LENGTH_LONG).show();
        }
    }

    private void initWidgets(View inflatedView) {
        eventList = inflatedView.findViewById(R.id.fragment_list_event_list_view);
        monthAndYear = inflatedView.findViewById(R.id.fragment_list_event_month_and_year);
    }

    private void setDaysListAdapter(View inflatedView) {
        RecyclerView daysList = inflatedView.findViewById(R.id.fragment_list_event_days_list_rv);
        listDaysView.setAdapter(daysList, this);
    }

    private void setEventListAdapter() {
        adapterEventList = new EventListAdapter(requireActivity(), clientViewModel, eventWithServicesViewModel);
        eventList.setAdapter(adapterEventList);
        registerForContextMenu(eventList);
    }

    private void eventListOnClickListener() {
        eventList.setOnItemClickListener((adapter, view, position, id) -> {
            Intent goToNewEventActivityEditMode = new Intent(requireActivity(), NewEventActivity.class);
            Event event = (Event) adapter.getItemAtPosition(position);
            goToNewEventActivityEditMode.putExtra(KEY_UPDATE_EVENT, event);
            activityResultLauncher.launch(goToNewEventActivityEditMode);
            disposable.dispose();
        });
    }

    @Override
    public void onDayClick(LocalDate date, int position) {
        CalendarUtil.selectedDate = date;
        disposable.dispose();
        eventViewModel.add(date);
        listDaysView.changeScrollPosition(position);
    }

    @Override
    public void onDayBinding(LocalDate date) {
        monthAndYear.setText(CalendarUtil.formatMonthYear(date));
    }

    private void registerActivityResult() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            if (intent != null) {
                List<Services> servicesList = (List<Services>) intent.getSerializableExtra(KEY_SERVICE);
                if (result.getResultCode() == REQUEST_CODE_UPDATE_EVENT) {
                    Event event = (Event) intent.getSerializableExtra(KEY_UPDATE_EVENT);
                    int editedServices = intent.getIntExtra(KEY_UPDATED_SERVICE, -1);
                    if (editedServices != -1) {// editedServices == 1 ? was edited : was not
                        updateServiceAndEvent(event.getEventId(), servicesList, event);
                    } else {
                        eventViewModel.insert(event).subscribe();
                    }
                    listDaysView.toScrollPosition(event.getEventDate());
                } else {
                    Event event = (Event) intent.getSerializableExtra(KEY_INSERT_EVENT);
                    insertEvent(event, servicesList);
                }
            }
        });
    }

    private void updateServiceAndEvent(long editedEventId, List<Services> servicesList, Event event) {
        eventWithServicesViewModel.getByEventId(editedEventId).doOnSuccess(eventServiceCroosRefs ->
                eventWithServicesViewModel.delete(eventServiceCroosRefs)
                        .doOnComplete(() ->
                                eventWithServicesViewModel.insert(CreateListsUtil.createNewListServices(editedEventId, servicesList))
                                        .doOnComplete(() -> eventViewModel.update(event)
                                                .subscribe()
                                        ).subscribe()
                        ).subscribe()
        ).subscribe();
    }

    private void insertEvent(Event event, List<Services> servicesList) {
        eventViewModel.insert(event).doOnSuccess((id) ->
                eventWithServicesViewModel.insert(CreateListsUtil.createNewListServices(id, servicesList))
                    .doOnComplete(() -> listDaysView.toScrollPosition(event.getEventDate())).subscribe())
                .subscribe();
    }

    private void observeEventList() {
        eventViewModel.getEventDate().observe(requireActivity(), this::updateEventList);
    }

    private void updateEventList(LocalDate date) {
        disposable = eventViewModel.getByDate(date)
                .doOnNext(eventList -> adapterEventList.update(eventList))
                .subscribe();
    }
}