package br.com.beautystyle.ui.activity;

import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_INSERT_EVENT;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_UPDATE_EVENT;
import static br.com.beautystyle.ui.activity.ContantsActivity.TAG_EVENT_DURATION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_CALENDAR_VIEW;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_START_TIME;
import static br.com.beautystyle.util.ConstantsUtil.DD_MM_YYYY;
import static br.com.beautystyle.util.ConstantsUtil.REMOVE_SYMBOL;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.beautystyle.R;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.ViewModel.EventViewModel;
import br.com.beautystyle.ViewModel.factory.EventFactory;
import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.Customer;
import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.repository.EventRepository;
import br.com.beautystyle.repository.Resource;
import br.com.beautystyle.ui.fragment.CalendarViewFragment;
import br.com.beautystyle.ui.fragment.TimePickerFragment;
import br.com.beautystyle.ui.fragment.client.ClientListFragment;
import br.com.beautystyle.ui.fragment.job.JobListFragment;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.MoneyTextWatcher;
import br.com.beautystyle.util.SortByEventStartTime;
import br.com.beautystyle.util.TimeUtil;

public class NewEventActivity extends AppCompatActivity {

    private EditText searchClient, searchJob, eventDate, eventStartTime, eventDuration, valueOfTheJobs;
    private CheckBox statusNotReceived, statusReceived;
    private final List<Job> jobs = new ArrayList<>();
    private EventWithClientAndJobs event = new EventWithClientAndJobs();
    private LocalTime jobsDuration = LocalTime.of(0, 0);
    private final TimePickerDialog.OnTimeSetListener timePickerListener = timePickerDialogListener();
    private final CalendarViewFragment calendarViewFragment = new CalendarViewFragment();
    private EventViewModel eventViewModel;
    @Inject
    EventRepository eventRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        injectActivity();
        initWidgets();
        loadEvent();

        // LISTENER
        restoreTimePickerListener(savedInstanceState);
        eventDateListener();
        eventStartTimeListener();
        clientListener();
        jobListener();
        eventDurationListener();
        paymentStatusListener();
        onCalendarClickListener();

        liveData();

        checkRequiredFieldsAndSaveListener();
    }

    private void onCalendarClickListener() {
        calendarViewFragment.setOnCalendarClickListener((view, year, month, dayOfMonth) -> {
            LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
            CalendarUtil.selectedDate = selectedDate;
            setDate(selectedDate);
            calendarViewFragment.dismiss();
        });
    }

    private void injectActivity() {
        ((BeautyStyleApplication) getApplicationContext())
                .applicationComponent.injectNewEventAct(this);
    }

    private void liveData() {
        EventFactory factory = new EventFactory(eventRepository);
        eventViewModel = new ViewModelProvider(this, factory).get(EventViewModel.class);
        eventViewModel.getClientLiveData().observe(this, this::setClient);
        eventViewModel.getJobsLiveData().observe(this, this::setJobs);
    }

    private void restoreTimePickerListener(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            TimePickerFragment eventStartTimeFragment = getFragmentByTag(TAG_START_TIME);
            TimePickerFragment eventDurationFragment = getFragmentByTag(TAG_EVENT_DURATION);
            checkListener(eventStartTimeFragment, eventDurationFragment);
        }
    }

    private void checkListener(TimePickerFragment eventStartTimeFrag,
                               TimePickerFragment eventDurationFrag) {
        if (eventStartTimeFrag != null) {
            eventStartTimeFrag.setOnTimeSetListener(timePickerListener);
        } else if (eventDurationFrag != null) {
            eventDurationFrag.setOnTimeSetListener(timePickerListener);
        }
    }

    private TimePickerFragment getFragmentByTag(String tag) {
        return (TimePickerFragment) getSupportFragmentManager().findFragmentByTag(tag);
    }

    private void initWidgets() {
        eventDate = findViewById(R.id.activity_new_event_event_date);
        searchJob = findViewById(R.id.activity_new_event_service);
        searchClient = findViewById(R.id.activity_new_event_client);
        valueOfTheJobs = findViewById(R.id.activity_new_event_value);
        valueOfTheJobs.addTextChangedListener(new MoneyTextWatcher(valueOfTheJobs));
        eventDuration = findViewById(R.id.activity_new_event_duration);
        eventStartTime = findViewById(R.id.activity_new_event_start_time);
        statusReceived = findViewById(R.id.activity_new_event_cb_received);
        statusNotReceived = findViewById(R.id.activity_new_event_cb_not_received);
    }

    private void loadEvent() {
        Intent intentEvent = getIntent();
        if (isKeyUpdateEvent(intentEvent)) { // click event list
            event = (EventWithClientAndJobs) intentEvent.getSerializableExtra(KEY_UPDATE_EVENT);
            jobs.addAll(event.getJobs());
            jobsDuration = getEventDuration();
            fillAllForm();
        } else if (isKeyInsertEvent(intentEvent)) {
            event = (EventWithClientAndJobs) intentEvent.getSerializableExtra(KEY_INSERT_EVENT);
            fillForm();
            setStatusAndDateEvent();
        } else { // click Fab button
            setStatusAndDateEvent();
        }
    }

    private boolean isKeyInsertEvent(Intent intentEvent) {
        return intentEvent.hasExtra(KEY_INSERT_EVENT);
    }

    private boolean isKeyUpdateEvent(Intent intentEvent) {
        return intentEvent.hasExtra(KEY_UPDATE_EVENT);
    }


    private void fillForm() {
        statusNotReceived.setChecked(true); // value default
        eventDate.setText(formatEventDate(CalendarUtil.selectedDate));
        eventStartTime.setText(formatEventStartTime());
    }

    private boolean isEmptyEvent() {
        return event.getEvent().getEndTime() == null;
    }

    private void setStatusAndDateEvent() {
        event.getEvent().setHasPaymentReceived(false);
        statusNotReceived.setChecked(true);
        event.getEvent().setEventDate(CalendarUtil.selectedDate);
        eventDate.setText(formatEventDate(CalendarUtil.selectedDate));
    }


    private void fillAllForm() {
        fillEventForm();
        searchClient.setText(event.getCustomer().getName());
        fillJobForm();
        fillPaymentStatus();
    }

    private void fillEventForm() {
        eventDate.setText(formatEventDate(event.getEvent().getEventDate()));
        eventStartTime.setText(formatEventStartTime());
        eventDuration.setText(TimeUtil.formatLocalTime(jobsDuration));
    }

    private String formatEventStartTime() {
        return TimeUtil.formatLocalTime(event.getEvent().getStartTime());
    }

    private String formatEventDate(LocalDate eventDate) {
        return CalendarUtil.formatLocalDate(eventDate, DD_MM_YYYY);
    }

    private LocalTime getEventDuration() {
        return event.getEvent().getEndTime()
                .minusHours(event.getEvent().getStartTime().getHour())
                .minusMinutes(event.getEvent().getStartTime().getMinute());
    }

    private void showError(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }

    private void fillJobForm() {
        valueOfTheJobs.setText(getValueJob());
        setJobName();
    }

    @NonNull
    private String getValueJob() {
        return CoinUtil.format(event.getEvent().getValue(), REMOVE_SYMBOL);
    }

    private void fillPaymentStatus() {
        if (event.getEvent().isHasPaymentReceived()) {
            statusReceived.setChecked(true);
        } else {
            statusNotReceived.setChecked(true);
        }
    }

    private void eventDateListener() {
        eventDate.setOnClickListener(v ->
                calendarViewFragment.show(getSupportFragmentManager(), TAG_CALENDAR_VIEW)
        );
    }

    private void eventStartTimeListener() {
        eventStartTime.setOnClickListener(v ->
                startTimePickerFragment(TimePickerFragment.newInstance(timePickerListener,
                                event.getEvent().getStartTime())
                        , TAG_START_TIME
                )
        );
    }

    private void startTimePickerFragment(TimePickerFragment timePickerFragment, String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(timePickerFragment, tag)
                .commit();
    }

    private void clientListener() {
        searchClient.setOnClickListener(v -> {
            Fragment clientListFragment = getFragment(R.id.activity_new_event_container_client);
            if (clientListFragment == null) {
                beginTransaction(R.id.activity_new_event_container_client, new ClientListFragment());
            } else {
                removeFragmentContainer(clientListFragment);
            }
        });
    }

    private Fragment getFragment(int container) {
        return getSupportFragmentManager()
                .findFragmentById(container);
    }

    private void removeFragmentContainer(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(fragment)
                .commit();
    }

    private void beginTransaction(int container, Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .add(container, fragment)
                .commit();
    }

    private void jobListener() {
        searchJob.setOnClickListener(v -> {
            Fragment jobListFragment = getFragment(R.id.activity_new_event_container_job);
            if (jobListFragment == null) {
                beginTransaction(R.id.activity_new_event_container_job, new JobListFragment());
            } else {
                removeFragmentContainer(jobListFragment);
            }
        });
    }

    private void eventDurationListener() {
        eventDuration.setOnClickListener(v -> startTimePickerFragment(
                TimePickerFragment.newInstance(timePickerListener), TAG_EVENT_DURATION
        ));
    }

    private void paymentStatusListener() {
        receivedPaymentCheckListener();
        notReceivedPaymentListener();
    }

    private void notReceivedPaymentListener() {
        statusNotReceived.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                statusReceived.setChecked(false);
                event.getEvent().setHasPaymentReceived(false);
            }
        }));
    }

    private void receivedPaymentCheckListener() {
        statusReceived.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                statusNotReceived.setChecked(false);
                event.getEvent().setHasPaymentReceived(true);
            }
        });
    }

    private void setDate(LocalDate date) {
        event.getEvent().setEventDate(date);
        String formatDateOfEvent = formatEventDate(date);
        eventDate.setText(formatDateOfEvent);
    }

    private void setClient(Customer customer) {
        event.getEvent().setCustomerCreatorId(customer.getId());
        event.setCustomer(customer);
        searchClient.setText(customer.getName());
    }


    private void setJobs(List<Job> jobs) {
        searchJob.getText().clear();
        if (!jobs.isEmpty()) {
            this.jobs.clear();
            this.jobs.addAll(jobs);
            fillJobsForm();
        }
    }

    private void fillJobsForm() {
        setJobName();
        setEventDuration();
        setValueOfTheJobs();
    }

    private void setJobName() {
        StringBuilder jobsName = concatJobsName();
        searchJob.setText(jobsName.toString());
    }

    private StringBuilder concatJobsName() {
        StringBuilder builder = new StringBuilder();
        if (!jobs.isEmpty()) {
            jobs.forEach(job -> {
                builder.append(job.getName());
                builder.append(", ");
            });
            builder.delete(builder.length() - 2, builder.length() - 1);
        }
        return builder;
    }

    private void setEventDuration() {
        jobsDuration = getSumJobsDuration();
        eventDuration.setText(TimeUtil.formatLocalTime(jobsDuration));
    }

    private LocalTime getSumJobsDuration() {
        return TimeUtil.sumTimeOfJobs(jobs.stream()
                .map(Job::getDurationTime)
                .collect(Collectors.toList()));
    }

    private void setValueOfTheJobs() {
        BigDecimal valueJobs = getSumValueOfJobs();
        valueOfTheJobs.setText(CoinUtil.format(valueJobs, REMOVE_SYMBOL));
    }

    private BigDecimal getSumValueOfJobs() {
        return jobs.stream()
                .map(Job::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void checkRequiredFieldsAndSaveListener() {
        Button btnSaveEvent = findViewById(R.id.activity_new_event_btn_save_event);
        btnSaveEvent.setOnClickListener(v -> checkRequiredFields());
    }

    private void setEvent() {
        LocalTime eventEndTime = eventStartTimePlusJobsDuration();
        event.getEvent().setEndTime(eventEndTime);
        BigDecimal valueJobs = getValueJobs();
        event.getEvent().setValue(valueJobs);
        event.setJobs(jobs);
    }

    @NonNull
    private BigDecimal getValueJobs() {
        return new BigDecimal(CoinUtil.formatPriceSave((valueOfTheJobs.getText()).toString()));
    }

    private LocalTime eventStartTimePlusJobsDuration() {
        return event.getEvent().getStartTime().plusHours(jobsDuration.getHour()).plusMinutes(jobsDuration.getMinute());
    }

    private void checkRequiredFields() {
        if (checkFields()) {
            setEvent();
            setResultEvent();
            // getByDateLiveData();
        } else {
            requiredFieldsAlertDialog();
        }
    }

//    private void getByDateLiveData() {
//        eventViewModel.getByDateLiveData()
//                .observe(this, this::checkResourceResponse);
//    }

    private void checkResourceResponse(Resource<List<EventWithClientAndJobs>> resource) {
        if (resource.isDataNotNull()) {
            if (resource.getData().isEmpty()) {
                setResultEvent();
            } else {
                checkTimeAndSetResult(resource.getData());
            }
        } else {
            showError(resource.getError());
        }
    }

    private boolean checkFields() {
        List<Boolean> checkListFields = createCheckListFields();
        for (Boolean checkInput : checkListFields) {
            if (!checkInput)
                return false;
        }
        return true;
    }

    private List<Boolean> createCheckListFields() {
        List<Boolean> checkList = new ArrayList<>();
        checkList.add(checkFields(eventStartTime));
        checkList.add(checkFields(searchClient));
        checkList.add(checkFields(searchJob));
        checkList.add(checkFields(eventDuration));
        return checkList;
    }

    private boolean checkFields(EditText editText) {
        int backgroundResource = editText.getText().toString().isEmpty() ?
                R.drawable.custom_invalid_input : R.drawable.custom_default_input;
        editText.setBackgroundResource(backgroundResource);
        return backgroundResource == R.drawable.custom_default_input;
    }

    public void requiredFieldsAlertDialog() {
        new AlertDialog
                .Builder(this)
                .setTitle("Todos os campos são obrigatórios")
                .setPositiveButton("Ok", null)
                .show();
    }

    private void checkTimeAndSetResult(List<EventWithClientAndJobs> events) {
        events.sort(new SortByEventStartTime());
        if (checkStartAndEndTimeAlertDialog(events))
            setResultEvent();
    }

    private boolean checkStartAndEndTimeAlertDialog(List<EventWithClientAndJobs> events) {
        return isStartTimeAvailable(events) && isEndTimeAvailable(events);
    }

    private boolean isEndTimeAvailable(List<EventWithClientAndJobs> events) {
        LocalTime reducedEndTime = event.getEvent().checkEndTime(events);
        if (reducedEndTime != null) {
            eventEndTimeAlertDialog(reducedEndTime);
            return false;
        }
        return true;
    }

    private boolean isStartTimeAvailable(List<EventWithClientAndJobs> eventList) {
        if (event.getEvent().checkStartTime(eventList)) {
            eventStartTimeAlertDialog();
            return false;
        }
        return true;
    }

    public void eventStartTimeAlertDialog() {
        new AlertDialog
                .Builder(this)
                .setTitle("Horário Inicial Indisponível")
                .setPositiveButton("Ok", (dialogInterface, i) ->
                        eventStartTime.setBackgroundResource(R.drawable.custom_invalid_input)
                )
                .show();
    }

    public void eventEndTimeAlertDialog(LocalTime reducedEndTime) {
        new AlertDialog
                .Builder(this)
                .setTitle("Tempo de duração excede o tempo disponível na agenda!")
                .setMessage("Deseja reduzir o tempo de duração?")
                .setPositiveButton("Sim", (dialogInterface, i) -> {
                    event.getEvent().setEndTime(reducedEndTime);
                    setResultEvent();
                })
                .setNegativeButton("Não", (dialogInterface, i) ->
                        eventDuration.setBackgroundResource(R.drawable.custom_invalid_input))
                .show();
    }

    private void setResultEvent() {
        setResult();
        finish();
    }

    private void setResult() {
        Intent intent = new Intent();
        isUpdateEvent(intent);
        isNewEvent(intent);
    }

    private void isNewEvent(Intent intent) {
        if (!event.getEvent().isEventIdNotNull()) {
            intent.putExtra(KEY_INSERT_EVENT, event);
            setResult(REQUEST_CODE_INSERT_EVENT, intent);
        }
    }

    private void isUpdateEvent(Intent intent) {
        if (event.getEvent().isEventIdNotNull()) {
            intent.putExtra(KEY_UPDATE_EVENT, event);
            setResult(REQUEST_CODE_UPDATE_EVENT, intent);
        }
    }

    private TimePickerDialog.OnTimeSetListener timePickerDialogListener() {
        return (view, hour, minute) -> {
            LocalTime timeWatch = LocalTime.of(hour, minute);
            String timeFormatted = TimeUtil.formatLocalTime(timeWatch);
            TimePickerFragment eventStartTimeFragment = getFragmentByTag(TAG_START_TIME);
            if (eventStartTimeFragment != null) {
                setEventStartTime(timeWatch, timeFormatted);
            } else { // durationEventFragment
                setEventDuration(timeWatch, timeFormatted);
            }
        };
    }

    private void setEventStartTime(LocalTime timeWatch, String timeFormatted) {
        eventStartTime.setText(timeFormatted);
        event.getEvent().setStartTime(timeWatch);
    }

    private void setEventDuration(LocalTime timeWatch, String timeFormatted) {
        eventDuration.setText(timeFormatted);
        jobsDuration = timeWatch;
    }
}