package br.com.beautystyle.ui.fragment.event;

import static br.com.beautystyle.retrofit.callback.CallbackMessages.DURATION_TIME_IS_NOT_AVAILABLE;
import static br.com.beautystyle.ui.activity.ContantsActivity.KEY_CLICK_FAB_NAVIGATION;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_INSERT_EVENT;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_UPDATE_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_BLOCK_TIME;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_BLOCK_TIME;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_START_TIME;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_BLOCK_TIME;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_INSERT_BLOCK_TIME;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_UPDATE_BLOCK_TIME;
import static br.com.beautystyle.util.ConstantsUtil.MMMM_YYYY;

import android.app.AlertDialog;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.ViewModel.BlockTimeViewModel;
import br.com.beautystyle.ViewModel.EventViewModel;
import br.com.beautystyle.ViewModel.factory.BlockTimeFactory;
import br.com.beautystyle.ViewModel.factory.EventFactory;
import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.BlockTime;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.repository.BlockTimeRepository;
import br.com.beautystyle.repository.EventRepository;
import br.com.beautystyle.repository.OpeningHoursRepository;
import br.com.beautystyle.repository.Resource;
import br.com.beautystyle.retrofit.model.dto.EventTimeNotAvailableDto;
import br.com.beautystyle.retrofit.model.dto.EventWithClientAndJobsDto;
import br.com.beautystyle.ui.ListDaysView;
import br.com.beautystyle.ui.activity.NewEventActivity;
import br.com.beautystyle.ui.adapter.listview.EventListAdapter;
import br.com.beautystyle.ui.adapter.recyclerview.ListDaysAdapter;
import br.com.beautystyle.ui.fragment.BlockTimeFragment;
import br.com.beautystyle.util.CalendarUtil;

public class EventListFragment extends Fragment implements ListDaysAdapter.OnDayListener {

    private ListView eventsListView;
    private ListDaysView daysList;
    private TextView monthAndYear;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private EventListAdapter adapterEvents;
    private EventViewModel eventViewModel;
    private BlockTimeViewModel blockTimeViewModel;
    @Inject
    EventRepository eventRepository;
    @Inject
    OpeningHoursRepository openingHoursRepository;
    @Inject
    BlockTimeRepository blockTimeRepository;
    @Inject
    ObjectMapper mapper;
    private EventWithClientAndJobs event = new EventWithClientAndJobs();

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
        EventFactory eventFactory = new EventFactory(eventRepository);
        BlockTimeFactory blockTimeFactory = new BlockTimeFactory(blockTimeRepository);
        eventViewModel = new ViewModelProvider(this, eventFactory)
                .get(EventViewModel.class);
        blockTimeViewModel = new ViewModelProvider(this, blockTimeFactory)
                .get(BlockTimeViewModel.class);
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
        setFragmentResultListener();

        return inflatedView;
    }

    private void setFragmentResultListener() {
        getChildFragmentManager().setFragmentResultListener(
                KEY_BLOCK_TIME,
                this,
                (requestKey, result) -> {
                    if (result.containsKey(KEY_INSERT_BLOCK_TIME)) {
                        BlockTime blockTime =
                                (BlockTime) result.getSerializable(KEY_INSERT_BLOCK_TIME);
                        blockTimeViewModel.insert(blockTime)
                                .observe(requireActivity(), this::checkResourceResponse);
                    } else {
                        BlockTime blockTime =
                                (BlockTime) result.getSerializable(KEY_UPDATE_BLOCK_TIME);
                        blockTimeViewModel.update(blockTime)
                                .observe(requireActivity(), this::checkResourceResponse);
                    }
                });
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

    private void updateAdapters(Resource<EventWithClientAndJobsDto> resource) {
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
        EventWithClientAndJobs eventWithClientAndJobs =
                getEventByAdapterPosition(item);
        if (item.getItemId() == R.id.delete_menu) {
            deleteAlertDialog(eventWithClientAndJobs);
        } else {
            startBlockTimeFragment(eventWithClientAndJobs.getEvent().getStartTime());
        }
        return super.onContextItemSelected(item);
    }

    private void startBlockTimeFragment(LocalTime startTime) {
        BlockTimeFragment fragment = new BlockTimeFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_START_TIME, startTime);
        fragment.setArguments(bundle);
        fragment.show(getChildFragmentManager(), TAG_INSERT_BLOCK_TIME);
    }

    public void deleteAlertDialog(EventWithClientAndJobs eventWithClientAndJobs) {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("Removendo item da Agenda")
                .setMessage("Tem certeza que deseja remover o item selecionado?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    if (eventWithClientAndJobs.isEventNotNull()) {
                        deleteEvent(eventWithClientAndJobs.getEvent());
                    } else if (eventWithClientAndJobs.isBlockTimeNotNull()) {
                        deleteBlockTime(eventWithClientAndJobs.getBlockTime());
                    } else {
                        showErrorMessage("Não é possível remover um horário vazio");
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void deleteBlockTime(BlockTime blockTime) {
        blockTimeViewModel.delete(blockTime)
                .observe(requireActivity(), this::checkResourceResponse);
    }

    private EventWithClientAndJobs getEventByAdapterPosition(MenuItem itemId) {
        AdapterView.AdapterContextMenuInfo menuInfo =
                (AdapterView.AdapterContextMenuInfo) itemId.getMenuInfo();
        return (EventWithClientAndJobs) adapterEvents.getItem(menuInfo.position);
    }

    private void deleteEvent(Event selectedEvent) {
        eventViewModel.delete(selectedEvent)
                .observe(requireActivity(), this::checkResourceResponse);
    }

    private void checkResourceResponse(Resource<?> resource) {
        if (resource.isErrorNotNull()) {
            if (resource.isDurationTimeNotAvailable()) {
                durationTimeNotAvailableAlertDialog(resource);
            } else {
                errorAlertDialog(resource);
            }
        } else {
            daysList.toScrollPosition(CalendarUtil.selectedDate);
        }
    }

    private void errorAlertDialog(Resource<?> resource) {
        new AlertDialog
                .Builder(requireActivity())
                .setMessage(resource.getError())
                .setPositiveButton("ok", null)
                .show();
    }

    private void durationTimeNotAvailableAlertDialog(Resource<?> resource) {
        try {
            EventTimeNotAvailableDto eventTimeNotAvailableDto = mapper.readValue(
                    resource.getError(), EventTimeNotAvailableDto.class);
            durationTimeNotAvailableAlertDialog(eventTimeNotAvailableDto);
        } catch (JsonProcessingException e) {
            showErrorMessage(DURATION_TIME_IS_NOT_AVAILABLE);
        }
    }

    private void durationTimeNotAvailableAlertDialog(EventTimeNotAvailableDto eventTimeNotAvailableDto) {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle(DURATION_TIME_IS_NOT_AVAILABLE)
                .setMessage("Deseja reduzir o tempo de duração?")
                .setPositiveButton("sim", (dialog, which) -> {
                    event.getEvent().setEndTime(eventTimeNotAvailableDto.getData());
                    eventViewModel.insert(event)
                            .observe(requireActivity(), this::checkResourceResponse);
                })
                .setNegativeButton("Não", null)
                .show();
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
            String key = getKey(eventWithClientAndJobs);
            if (key.equals(TAG_UPDATE_BLOCK_TIME)) {
                BlockTime blockTime = eventWithClientAndJobs.getBlockTime();
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEY_UPDATE_BLOCK_TIME, blockTime);
                BlockTimeFragment blockTimeFragment = new BlockTimeFragment();
                blockTimeFragment.setArguments(bundle);
                blockTimeFragment.show(getChildFragmentManager(), TAG_UPDATE_BLOCK_TIME);
            } else {
                launchNewEventActivity(eventWithClientAndJobs, key);
            }
        });
    }

    private String getKey(EventWithClientAndJobs eventWithClientAndJobs) {
        if (eventWithClientAndJobs.isEventNotNull()) return KEY_UPDATE_EVENT;
        if (eventWithClientAndJobs.isBlockTimeNotNull()) return TAG_UPDATE_BLOCK_TIME;
        return KEY_INSERT_EVENT;
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
            eventViewModel.update(event)
                    .observe(requireActivity(), this::checkResourceResponse);
        }
    }

    private void isNewEvent(ActivityResult result, Intent intent) {
        if (result.getResultCode() == REQUEST_CODE_INSERT_EVENT) {
            event = (EventWithClientAndJobs) intent.getSerializableExtra(KEY_INSERT_EVENT);
            eventViewModel.insert(event)
                    .observe(requireActivity(), this::checkResourceResponse);
        }
    }

    private void getEventsByDateFromRoomLiveData() {
        eventViewModel.getByDateLiveData()
                .observe(requireActivity(), this::updateAdapters);
    }

    private void updateAdapters(EventWithClientAndJobsDto events) {
        openingHoursRepository.getAll().doOnSuccess(openingHours -> {
            adapterEvents.update(events, openingHours);
            daysList.changeScrollPosition();
        }).subscribe();

    }

    private void showErrorMessage(String error) {
        if (this.getActivity() != null)
            Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
    }
}