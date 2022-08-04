package br.com.beautystyle.ui.fragment.event;

import static br.com.beautystyle.ui.activity.ContantsActivity.KEY_CLICK_FAB_NAVIGATION;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_INSERT_EVENT;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_UPDATE_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_EVENT;
import static br.com.beautystyle.util.ConstantsUtil.MMMM_YYYY;

import android.content.Context;
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

import androidx.activity.result.ActivityResult;
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

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.ViewModel.EventViewModel;
import br.com.beautystyle.ViewModel.factory.EventFactory;
import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.repository.EventRepository;
import br.com.beautystyle.repository.Resource;
import br.com.beautystyle.ui.ListDaysView;
import br.com.beautystyle.ui.activity.NewEventActivity;
import br.com.beautystyle.ui.adapter.listview.EventListAdapter;
import br.com.beautystyle.ui.adapter.recyclerview.ListDaysAdapter;
import br.com.beautystyle.util.CalendarUtil;

public class EventListFragment extends Fragment implements ListDaysAdapter.OnDayListener {

    private ListView eventsListView;
    private ListDaysView daysList;
    private TextView monthAndYear;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private EventListAdapter adapterEvents;
    private EventViewModel eventViewModel;
    @Inject
    EventRepository eventRepository;

    @Override
    public void onAttach(@NonNull Context context) {
        injectFragment();
        super.onAttach(context);
    }

    private void injectFragment() {
        ((BeautyStyleApplication) requireActivity().getApplicationContext())
                .applicationComponent.injectEventFrag(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventFactory factory = new EventFactory(eventRepository);
        eventViewModel = new ViewModelProvider(this, factory).get(EventViewModel.class);
        daysList = new ListDaysView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_list_event, container, false);

        registerActivityResult();

        setMonthAndYearTextView(inflatedView);
        setDaysRecyclerViewAdapter(inflatedView);// onClickListener in ListDaysAdapter
        setEventListViewAdapter(inflatedView);

        // LISTENER
        eventListOnClickListener();
        fabNavigationClickListener();

        return inflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();
        eventsLiveData();
    }

    private void fabNavigationClickListener() {
        Bundle arguments = getArguments();
        if (isFabOnClickListener(arguments)) {
            launchNewEventActivity(new EventWithClientAndJobs(), KEY_INSERT_EVENT);
            arguments.remove(KEY_CLICK_FAB_NAVIGATION);
            setArguments(arguments);
        }
    }

    private boolean isFabOnClickListener(Bundle arguments) {
        return arguments != null && arguments.containsKey(KEY_CLICK_FAB_NAVIGATION);
    }

    private void setMonthAndYearTextView(View inflatedView) {
        monthAndYear = inflatedView.findViewById(R.id.fragment_list_event_month_and_year);
        monthAndYear.setText(CalendarUtil.formatLocalDate(CalendarUtil.selectedDate, MMMM_YYYY));
    }

    private void getEventsByDateFromApiLiveData() {
        eventViewModel.getAllByDateFromApiLiveData(CalendarUtil.selectedDate)
                .observe(requireActivity(), this::updateAdapters);
    }

    private void updateAdapters(Resource<List<EventWithClientAndJobs>> resource) {
        if (resource.isDataNotNull()) {
            updateAdapters(resource.getData());
        } else {
            showErrorMessage(resource.getError());
        }
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v,
                                    @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = requireActivity().getMenuInflater();
        inflater.inflate(R.menu.delete_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        checkDelete(item);
        return super.onContextItemSelected(item);
    }

    public void checkDelete(final MenuItem itemId) {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("Removendo item da Agenda")
                .setMessage("Tem certeza que deseja remover o item selecionado?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    EventWithClientAndJobs eventWithClientAndJobs =
                            getEventByAdapterPosition(itemId);
                    checkIsEventNotNull(eventWithClientAndJobs);
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private EventWithClientAndJobs getEventByAdapterPosition(MenuItem itemId) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) itemId.getMenuInfo();
        return (EventWithClientAndJobs) adapterEvents.getItem(menuInfo.position);
    }

    private void checkIsEventNotNull(EventWithClientAndJobs selectedEvent) {
        if (selectedEvent.isEventNotNull()) {
            delete(selectedEvent);
        } else {
            Toast.makeText(requireActivity(), "Não é possível remover um horário vazio",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void delete(EventWithClientAndJobs selectedEvent) {
        if(eventRepository.isUserPremium()){
            eventViewModel.deleteOnApi(selectedEvent.getEvent())
                    .observe(requireActivity(), this::checkResourceResponse);
        }else{
            eventViewModel.deleteOnRoom(selectedEvent.getEvent())
                    .observe(requireActivity(), this::checkResourceResponse);
        }
    }

    private void checkResourceResponse(Resource<?> resource) {
        if (resource.isErrorNotNull()) {
            showErrorMessage(resource.getError());
        } else {
            daysList.toScrollPosition(CalendarUtil.selectedDate);
        }
    }

    private void setDaysRecyclerViewAdapter(View inflatedView) {
        RecyclerView daysList = inflatedView.findViewById(R.id.fragment_list_event_days_list_rv);
        this.daysList.setAdapter(daysList, this);
    }

    private void setEventListViewAdapter(View inflatedView) {
        adapterEvents = new EventListAdapter(requireActivity());
        eventsListView = inflatedView.findViewById(R.id.fragment_list_event_list_view);
        eventsListView.setAdapter(adapterEvents);
        registerForContextMenu(eventsListView);
    }

    private void eventListOnClickListener() {
        eventsListView.setOnItemClickListener((adapter, view, position, id) -> {
            EventWithClientAndJobs eventWithClientAndJobs =
                    (EventWithClientAndJobs) adapter.getItemAtPosition(position);
            launchNewEventActivity(eventWithClientAndJobs, KEY_UPDATE_EVENT);
        });
    }

    private void launchNewEventActivity(EventWithClientAndJobs eventWithClientAndJobs,
                                        String keyUpdateEvent) {
        Intent intent = new Intent(requireActivity(), NewEventActivity.class)
                .putExtra(keyUpdateEvent, eventWithClientAndJobs);
        activityResultLauncher.launch(intent);
    }

    @Override
    public void onDayClick(LocalDate date, int position) {
        CalendarUtil.selectedDate = date;
        eventsLiveData();
    }

    private void eventsLiveData() {
        getEventsByDateFromRoomLiveData();
        if(eventRepository.isUserPremium()){
            getEventsByDateFromApiLiveData();
        }
    }

    @Override
    public void onDayBinding(LocalDate date) {
        monthAndYear.setText(CalendarUtil.formatLocalDate(date, MMMM_YYYY));
    }

    private void registerActivityResult() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    Intent intent = result.getData();
                    if (intent != null) {
                        isNewEvent(result, intent);
                        isUpdateEvent(result, intent);
                    }
                }
        );
    }

    private void isUpdateEvent(ActivityResult result, Intent intent) {
        if (result.getResultCode() == REQUEST_CODE_UPDATE_EVENT) {
            EventWithClientAndJobs event =
                    (EventWithClientAndJobs) intent.getSerializableExtra(KEY_UPDATE_EVENT);
            if(eventRepository.isUserPremium()){
                eventViewModel.updateOnApi(event)
                        .observe(requireActivity(), this::checkResourceResponse);
            }else{
                eventViewModel.updateOnRoom(event)
                        .observe(requireActivity(), this::checkResourceResponse);
            }
        }
    }

    private void isNewEvent(ActivityResult result, Intent intent) {
        if (result.getResultCode() == REQUEST_CODE_INSERT_EVENT) {
            EventWithClientAndJobs event =
                    (EventWithClientAndJobs) intent.getSerializableExtra(KEY_INSERT_EVENT);
            if(eventRepository.isUserPremium()){
                eventViewModel.insertOnApi(event)
                        .observe(requireActivity(), this::checkResourceResponse);
            }else{
                eventViewModel.insertOnRoom(event)
                        .observe(requireActivity(), this::checkResourceResponse);
            }
        }
    }

    private void getEventsByDateFromRoomLiveData() {
        eventViewModel.getByDateFromRoomLiveData()
                .observe(requireActivity(), this::updateAdapters);
    }

    private void updateAdapters(List<EventWithClientAndJobs> events) {
        adapterEvents.update(events);
        daysList.changeScrollPosition();
    }

    private void showErrorMessage(String error) {
        if (this.getActivity() != null)
            Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
    }
}