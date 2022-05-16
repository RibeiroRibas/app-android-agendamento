package br.com.beautystyle.ui.fragment.event;

import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_UPDATE_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_JOB;
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
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import br.com.beautystyle.ViewModel.EventViewModel;
import br.com.beautystyle.database.room.references.EventWithJobs;
import br.com.beautystyle.model.EventDto;
import br.com.beautystyle.model.entities.Client;
import br.com.beautystyle.model.entities.Event;
import br.com.beautystyle.model.entities.Job;
import br.com.beautystyle.repository.ClientRepository;
import br.com.beautystyle.repository.EventRepository;
import br.com.beautystyle.repository.EventWithJobRepository;
import br.com.beautystyle.repository.JobRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.ui.ListDaysView;
import br.com.beautystyle.ui.activity.NewEventActivity;
import br.com.beautystyle.ui.adapter.listview.EventListAdapter;
import br.com.beautystyle.ui.adapter.recyclerview.ListDaysAdapter;
import br.com.beautystyle.util.CalendarUtil;

public class EventListFragment extends Fragment implements ListDaysAdapter.OnDayListener {

    private ListView eventList;
    private ListDaysView listDaysView;
    private TextView monthAndYear;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private EventListAdapter adapterEventList;
    private EventRepository eventRepository;
    private EventViewModel eventViewModel;
    private JobRepository jobRepository;
    private ClientRepository clientRepository;
    private EventWithJobRepository eventWithJobRepository;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listDaysView = new ListDaysView();
        eventViewModel = new ViewModelProvider(requireActivity()).get(EventViewModel.class);
        eventRepository = new EventRepository(requireActivity());
        jobRepository = new JobRepository(requireActivity());
        clientRepository = new ClientRepository(requireActivity());
        eventWithJobRepository = new EventWithJobRepository(requireActivity());
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

        eventViewModel.add(CalendarUtil.selectedDate);

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
                    EventWithJobs eventWithJobs = (EventWithJobs) adapterEventList.getItem(menuInfo.position);
                    deleteEvent(eventWithJobs.getEvent());
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void deleteEvent(Event selectedEvent) {
        if (selectedEvent.getEventId() >= 0) {
            eventRepository.delete(selectedEvent, new ResultsCallBack<Void>() {
                @Override
                public void onSuccess(Void resultado) {
                    listDaysView.toScrollPosition(selectedEvent.getEventDate());
                }

                @Override
                public void onError(String erro) {
                    showErrorMessage(erro);
                }
            });
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
        adapterEventList = new EventListAdapter(requireActivity());
        eventList.setAdapter(adapterEventList);
        registerForContextMenu(eventList);
    }

    private void eventListOnClickListener() {
        eventList.setOnItemClickListener((adapter, view, position, id) -> {
            Intent goToNewEventActivityEditMode = new Intent(requireActivity(), NewEventActivity.class);
            EventWithJobs eventWithJobs = (EventWithJobs) adapter.getItemAtPosition(position);
            goToNewEventActivityEditMode.putExtra(KEY_UPDATE_EVENT, eventWithJobs);
            activityResultLauncher.launch(goToNewEventActivityEditMode);
        });
    }

    @Override
    public void onDayClick(LocalDate date, int position) {
        CalendarUtil.selectedDate = date;
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
                List<Job> jobList = (List<Job>) intent.getSerializableExtra(KEY_JOB);
                Client client = (Client) intent.getSerializableExtra(KEY_CLIENT);
                if (result.getResultCode() == REQUEST_CODE_UPDATE_EVENT) {
                    updateEvent(client, jobList, intent);
                } else {
                    insertEvent(intent, jobList, client);
                }
            }
        });
    }

    private void updateEvent(Client client, List<Job> jobList, Intent intent) {
        EventWithJobs eventWithJobs = (EventWithJobs) intent.getSerializableExtra(KEY_UPDATE_EVENT);
        eventRepository.update(eventWithJobs.getEvent(), client, jobList, new ResultsCallBack<Event>() {
            @Override
            public void onSuccess(Event event) {
                listDaysView.toScrollPosition(event.getEventDate());
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });

    }

    private void insertEvent(Intent intent, List<Job> jobList, Client client) {
        EventWithJobs eventWithJobs = (EventWithJobs) intent.getSerializableExtra(KEY_INSERT_EVENT);
        eventRepository.insert(eventWithJobs.getEvent(), jobList, client, new ResultsCallBack<Event>() {
            @Override
            public void onSuccess(Event event) {
                    listDaysView.toScrollPosition(event.getEventDate());
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void observeEventList() {
        eventViewModel.getEventDate().observe(requireActivity(), this::getListByDate);
    }

    private void getListByDate(LocalDate date) {
        eventRepository.getByDateFromRooom(date)
                .doOnSuccess(eventList -> {
                    updateAdapterEventList(eventList, date);
                    getByDateFromApi(date);
                })
                .subscribe();
    }

    private void updateAdapterEventList(List<EventWithJobs> eventWithJobs, LocalDate date) {
        adapterEventList.update(eventWithJobs);
        int position = listDaysView.getPosition(date);
        listDaysView.changeScrollPosition(position);
    }

    private void getByDateFromApi(LocalDate date) {
        eventRepository.getByDateFromApi(date, new ResultsCallBack<List<EventDto>>() {
            @Override
            public void onSuccess(List<EventDto> eventListDto) {
                updateDatabaseAndAdapter(eventListDto,date);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void updateDatabaseAndAdapter(List<EventDto> eventListDto, LocalDate date) {
        List<EventWithJobs> eventWithJobs = EventWithJobs.convert(eventListDto);
        updateJobList(eventListDto,date,eventWithJobs);

    }

    private void updateJobList(List<EventDto> eventListDto, LocalDate date, List<EventWithJobs> eventWithJobs) {
        List<Job> jobList = getJobList(eventWithJobs);
        jobRepository.insertAllOnRoom(jobList)
                .doOnComplete(() -> updateClientList(eventListDto, date, eventWithJobs))
                .subscribe();
    }

    private void updateClientList(List<EventDto> eventListDto, LocalDate date, List<EventWithJobs> eventWithJobs) {
        List<Client> clientList = getClientList(eventListDto);
        clientRepository.insertAllOnRoom(clientList)
                .doOnComplete(() -> updateEventList(eventListDto, date, eventWithJobs))
                .subscribe();
    }

    private void updateEventList(List<EventDto> eventListDto, LocalDate date, List<EventWithJobs> eventWithJobs) {
        eventRepository.insertAllOnRoom(eventListDto)
                .doOnComplete(() -> updateEventWithJobs(date, eventWithJobs))
                .subscribe();
    }

    private void updateEventWithJobs(LocalDate date, List<EventWithJobs> eventWithJobs) {
        eventWithJobRepository.insertAll(eventWithJobs, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void resultado) {
                updateAdapterEventList(eventWithJobs, date);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    @NonNull
    private List<Client> getClientList(List<EventDto> eventListDto) {
        return eventListDto.stream()
                .map(EventDto::getClient)
                .collect(Collectors.toList());
    }

    @NonNull
    private List<Job> getJobList(List<EventWithJobs> eventWithJobs) {
        return eventWithJobs.stream()
                .map(EventWithJobs::getJobList)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private void showErrorMessage(String erro) {
        Toast.makeText(requireActivity(), erro, Toast.LENGTH_LONG).show();
    }

}