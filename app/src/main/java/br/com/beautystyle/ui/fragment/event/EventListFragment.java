package br.com.beautystyle.ui.fragment.event;

import static android.content.ContentValues.TAG;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_INSERT_EVENT;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_UPDATE_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_EVENT;
import static br.com.beautystyle.util.ConstantsUtil.MMMM_YYYY;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.database.room.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.repository.EventRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.repository.RoomRepository;
import br.com.beautystyle.ui.ListDaysView;
import br.com.beautystyle.ui.activity.NewEventActivity;
import br.com.beautystyle.ui.adapter.listview.EventListAdapter;
import br.com.beautystyle.ui.adapter.recyclerview.ListDaysAdapter;
import br.com.beautystyle.util.CalendarUtil;
import io.reactivex.rxjava3.disposables.Disposable;

public class EventListFragment extends Fragment implements ListDaysAdapter.OnDayListener {

    private ListView eventsListView;
    private ListDaysView daysListView;
    private TextView monthAndYear;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private EventListAdapter adapterEvents;
    @Inject
    RoomRepository roomRepository;
    @Inject
    EventRepository eventRepository;
    private Disposable subscribe;

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
        daysListView = new ListDaysView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_list_event, container, false);

        initWidgets(inflatedView);
        setDaysListAdapter(inflatedView);// onClickListener in ListDaysAdapter
        setEventListAdapter();

        eventListOnClickListener();
        monthAndYear.setText(CalendarUtil.formatLocalDate(CalendarUtil.selectedDate, MMMM_YYYY));

        registerActivityResult();
        getEventsByDate(CalendarUtil.selectedDate);

        return inflatedView;
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
                    AdapterView.AdapterContextMenuInfo menuInfo =
                            (AdapterView.AdapterContextMenuInfo) itemId.getMenuInfo();
                    EventWithClientAndJobs eventWithClientAndJobs =
                            (EventWithClientAndJobs) adapterEvents.getItem(menuInfo.position);
                    deleteEvent(eventWithClientAndJobs.getEvent());
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void deleteEvent(Event selectedEvent) {
        if (selectedEvent.getEventId() > 0) {
            eventRepository.deleteOnApi(selectedEvent.getApiId(), new ResultsCallBack<Void>() {
                @Override
                public void onSuccess(Void resultado) {
                    deleteOnRoom(selectedEvent);
                }

                @Override
                public void onError(String erro) {
                    showErrorMessage(erro);
                }
            });
        } else {
            Toast.makeText(requireActivity(), "Não é possível remover um horário vazio",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void deleteOnRoom(Event selectedEvent) {
        eventRepository.deleteOnRoom(selectedEvent)
                .doOnComplete(() -> daysListView.toScrollPosition(selectedEvent.getEventDate()))
                .subscribe();
    }

    private void initWidgets(View inflatedView) {
        eventsListView = inflatedView.findViewById(R.id.fragment_list_event_list_view);
        monthAndYear = inflatedView.findViewById(R.id.fragment_list_event_month_and_year);
    }

    private void setDaysListAdapter(View inflatedView) {
        RecyclerView daysList = inflatedView.findViewById(R.id.fragment_list_event_days_list_rv);
        daysListView.setAdapter(daysList, this);
    }

    private void setEventListAdapter() {
        adapterEvents = new EventListAdapter(requireActivity());
        eventsListView.setAdapter(adapterEvents);
        registerForContextMenu(eventsListView);
    }

    private void eventListOnClickListener() {
        eventsListView.setOnItemClickListener((adapter, view, position, id) -> {
            EventWithClientAndJobs eventWithClientAndJobs = (EventWithClientAndJobs) adapter.getItemAtPosition(position);
            Intent intent = new Intent(requireActivity(), NewEventActivity.class)
                    .putExtra(KEY_UPDATE_EVENT, eventWithClientAndJobs);
            activityResultLauncher.launch(intent);
        });
    }

    @Override
    public void onDayClick(LocalDate date, int position) {
        CalendarUtil.selectedDate = date;
        getEventsByDate(CalendarUtil.selectedDate);
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
            updateEventOnApi(event);
        }
    }

    private void isNewEvent(ActivityResult result, Intent intent) {
        if (result.getResultCode() == REQUEST_CODE_INSERT_EVENT) {
            EventWithClientAndJobs event =
                    (EventWithClientAndJobs) intent.getSerializableExtra(KEY_INSERT_EVENT);
            insertEventOnApi(event);
        }
    }

    private void updateEventOnApi(EventWithClientAndJobs event) {
        eventRepository.updateOnApi(event, new ResultsCallBack<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        updateEventOnRoom(event);
                    }

                    @Override
                    public void onError(String erro) {
                        showErrorMessage(erro);
                    }
                }
        );
    }

    private void updateEventOnRoom(EventWithClientAndJobs event) {
        roomRepository.updateEvent(event,
                new ResultsCallBack<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        daysListView.toScrollPosition(CalendarUtil.selectedDate);
                    }

                    @Override
                    public void onError(String erro) {
                        showErrorMessage(erro);
                    }
                });
    }

    private void insertEventOnApi(EventWithClientAndJobs event) {
        eventRepository.insertOnApi(event, new ResultsCallBack<EventWithClientAndJobs>() {
            @Override
            public void onSuccess(EventWithClientAndJobs eventFromApi) {
                event.getEvent().setApiId(eventFromApi.getEvent().getApiId());
                insertEventOnRoom(event);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void insertEventOnRoom(EventWithClientAndJobs eventFromApi) {
        roomRepository.insertEvent(eventFromApi,
                new ResultsCallBack<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        daysListView.toScrollPosition(CalendarUtil.selectedDate);
                    }

                    @Override
                    public void onError(String erro) {
                        showErrorMessage(erro);
                    }
                });
    }

    private void getEventsByDate(LocalDate eventDate) {
        subscribe = eventRepository.getByDateFromRooom(eventDate)
                .doOnSuccess(events -> {
                    updateAdapters(events, eventDate);
                    getByDateFromApi(eventDate);
                })
                .subscribe();
    }

    private void updateAdapters(List<EventWithClientAndJobs> events, LocalDate eventDate) {
        adapterEvents.updateAdapterListView(events);
        daysListView.changeScrollPosition(eventDate);
    }

    private void getByDateFromApi(LocalDate eventDate) {
        eventRepository.getByDateFromApi(eventDate,
                new ResultsCallBack<List<EventWithClientAndJobs>>() {
                    @Override
                    public void onSuccess(List<EventWithClientAndJobs> events) {
                        //update local database -> clients -> jobs -> events
                        if (!events.isEmpty())
                            updateLocalDatabase(events, eventDate);
                    }

                    @Override
                    public void onError(String erro) {

                        showErrorMessage(erro);
                    }
                });


    }

    private void updateLocalDatabase(List<EventWithClientAndJobs> events, LocalDate eventDate) {
        roomRepository.updateLocalDatabase(events,
                new ResultsCallBack<List<EventWithClientAndJobs>>() {
                    @Override
                    public void onSuccess(List<EventWithClientAndJobs> events) {
                        updateAdapters(events, eventDate);
                    }

                    @Override
                    public void onError(String erro) {
                        showErrorMessage(erro);
                    }
                });
    }

    private void showErrorMessage(String erro) {
        if (this.getActivity() != null)
            Toast.makeText(requireActivity(), erro, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
        if (subscribe != null)
            subscribe.dispose();
    }
}