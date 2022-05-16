package br.com.beautystyle.ui.activity;

import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_NEW_EVENT;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_UPDATE_EVENT;
import static br.com.beautystyle.ui.activity.ContantsActivity.TAG_EVENT_DURATION;
import static br.com.beautystyle.ui.activity.ContantsActivity.TAG_EVENT_START_TIME;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_CLIENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_INSERT_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_JOB;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_EVENT;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.beautystyle.R;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.beautystyle.ViewModel.CalendarViewModel;
import br.com.beautystyle.ViewModel.EventViewModel;
import br.com.beautystyle.database.room.references.EventWithJobs;
import br.com.beautystyle.model.EventDto;
import br.com.beautystyle.model.entities.Client;
import br.com.beautystyle.model.entities.Event;
import br.com.beautystyle.model.entities.Job;
import br.com.beautystyle.model.enuns.StatusPagamento;
import br.com.beautystyle.repository.ClientRepository;
import br.com.beautystyle.repository.EventRepository;
import br.com.beautystyle.repository.ResultsCallBack;
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
    private CheckBox statusNaoRecebido, statusRecebido;
    private List<Job> jobList;
    private EventWithJobs event = new EventWithJobs(new Event());
    private LocalTime jobsDuration;
    private CalendarViewModel calendarViewModel;
    private EventViewModel eventViewModel;
    private EventRepository repository;
    private final TimePickerDialog.OnTimeSetListener listener = timePickerDialogListener();
    private Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        restoreTimePickerListener(savedInstanceState);
        initWidgets();
        loadEvent();

        // LISTENER
        eventDateListener();
        eventStartTimeListener();
        clientListener();
        jobListener();
        eventDurationListener();
        paymentStatusListener();

        calendarObserve();
        clientObserver();
        servicesObserver();

        checkRequiredFieldsAndSaveListener();
    }

    private void restoreTimePickerListener(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            TimePickerFragment tpf = (TimePickerFragment) getSupportFragmentManager().findFragmentByTag(TAG_EVENT_START_TIME);
            TimePickerFragment tpf2 = (TimePickerFragment) getSupportFragmentManager().findFragmentByTag(TAG_EVENT_DURATION);
            if (tpf != null) {
                tpf.setOnTimeSetListener(listener);
            } else if (tpf2 != null) {
                tpf2.setOnTimeSetListener(listener);
            }
        }
    }

    private void initWidgets() {
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        repository = new EventRepository(this);
        jobsDuration = LocalTime.of(0, 0);
        jobList = new ArrayList<>();
        eventDate = findViewById(R.id.activity_new_event_event_date);
        searchJob = findViewById(R.id.activity_new_event_service);
        searchClient = findViewById(R.id.activity_new_event_client);
        valueOfTheJobs = findViewById(R.id.activity_new_event_value);
        valueOfTheJobs.addTextChangedListener(new MoneyTextWatcher(valueOfTheJobs));
        eventDuration = findViewById(R.id.activity_new_event_duration);
        eventStartTime = findViewById(R.id.activity_new_event_start_time);
        statusRecebido = findViewById(R.id.activity_new_event_cb_received);
        statusNaoRecebido = findViewById(R.id.activity_new_event_cb_not_received);
    }


    private void loadEvent() {
        Intent intentEvent = getIntent();
        if (intentEvent.hasExtra(KEY_UPDATE_EVENT)) {
            event = (EventWithJobs) intentEvent.getSerializableExtra(KEY_UPDATE_EVENT);
            if (isUpdateEvent())
                fillAllForm();
        } else { // new event mode (click Fab button)
            setStatusAndDateEvent();
        }
    }

    private boolean isUpdateEvent() {
        if (event.getEvent().getEndTime() == null) { // new event mode (click event list)
            eventStartTime.setText(TimeUtil.formatLocalTime(event.getEvent().getStarTime()));
            setStatusAndDateEvent();
            return false;
        }
        return true;
    }

    private void setStatusAndDateEvent() {
        statusNaoRecebido.setChecked(true); // value default
        event.getEvent().setStatusPagamento(StatusPagamento.NAORECEBIDO);
        eventDate.setText(CalendarUtil.formatDate(CalendarUtil.selectedDate));
        event.getEvent().setEventDate(CalendarUtil.selectedDate);
    }


    private void fillAllForm() {
        eventDate.setText(CalendarUtil.formatDate(event.getEvent().getEventDate()));
        eventStartTime.setText(TimeUtil.formatLocalTime(event.getEvent().getStarTime()));
        fillClient();
        fillJobList();
        jobsDuration = getEventDuration();
        eventDuration.setText(TimeUtil.formatLocalTime(jobsDuration));
        valueOfTheJobs.setText(CoinUtil.formatBrWithoutSymbol(event.getEvent().getValueEvent()));
        setStatusPagamentoEditMode();
    }

    private LocalTime getEventDuration() {
        return event.getEvent().getEndTime().minusHours(event.getEvent().getStarTime().getHour())
                .minusMinutes(event.getEvent().getStarTime().getMinute());
    }

    private void fillClient() {
        ClientRepository repository = new ClientRepository(this);
        repository.getById(event.getEvent().getClient(), new ResultsCallBack<Client>() {
            @Override
            public void onSuccess(Client result) {
                client = result;
                searchClient.setText(result.getName());
            }

            @Override
            public void onError(String erro) {
                showError(erro);
            }
        });


    }

    private void showError(String message) {
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG).show();
    }

    private void fillJobList() {
        this.jobList.addAll(event.getJobList());
        setJobName();
    }

    private void setStatusPagamentoEditMode() {
        if (event.getEvent().getStatusPagamento() == StatusPagamento.NAORECEBIDO) {
            statusNaoRecebido.setChecked(true);
        } else {
            statusRecebido.setChecked(true);
        }
    }

    private void eventDateListener() {
        eventDate.setOnClickListener(v -> calendarViewModel.inflateCalendar(this));
    }

    private void eventStartTimeListener() {
        eventStartTime.setOnClickListener(v ->
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(TimePickerFragment.newInstance(listener), TAG_EVENT_START_TIME)
                        .commit()
        );
    }

    private void clientListener() {
        searchClient.setOnClickListener(v -> {
            if (getSupportFragmentManager().findFragmentById(R.id.activity_new_event_container_client) == null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_new_event_container_client, new ClientListFragment())
                        .commit();
        });
    }

    private void jobListener() {
        searchJob.setOnClickListener(v -> {
            if (getSupportFragmentManager().findFragmentById(R.id.activity_new_event_container_job) == null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_new_event_container_job, new JobListFragment())
                        .commit();
        });
    }

    private void eventDurationListener() {
        eventDuration.setOnClickListener(v ->
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(TimePickerFragment.newInstance(listener), TAG_EVENT_DURATION)
                        .commit()
        );
    }

    private void paymentStatusListener() {
        isReceivedPayment();
        isNotReceivedPayment();
    }

    private void isNotReceivedPayment() {
        statusNaoRecebido.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                statusRecebido.setChecked(false);
                event.getEvent().setStatusPagamento(StatusPagamento.NAORECEBIDO);
            }
        }));
    }

    private void isReceivedPayment() {
        statusRecebido.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                statusNaoRecebido.setChecked(false);
                event.getEvent().setStatusPagamento(StatusPagamento.RECEBIDO);
            }
        });
    }

    private void calendarObserve() {
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.getDate().observe(this, this::setDate);
    }

    private void setDate(LocalDate date) {
        event.getEvent().setEventDate(date);
        String formatDateOfEvent = CalendarUtil.formatDate(date);
        eventDate.setText(formatDateOfEvent);
    }

    private void clientObserver() {
        eventViewModel.getClientLive().observe(this, this::setClient);
    }

    private void setClient(Client client) {
        this.client = client;
        event.getEvent().setClient(client.getClientId());
        searchClient.setText(client.getName());
    }

    private void servicesObserver() {
        eventViewModel.getServiceListLiveData().observe(this, this::setServices);
    }

    private void setServices(List<Job> jobList) {
        searchJob.getText().clear();
        if (!jobList.isEmpty()) {
            this.jobList.clear();
            this.jobList.addAll(jobList);
            setJobName();
            setEventDuration();
            setValueOfTheJobs();
        }
    }

    private void setJobName() {
        StringBuilder builder = createStringBuilder();
        searchJob.setText(builder.toString());
    }

    private StringBuilder createStringBuilder() {
        StringBuilder builder = new StringBuilder();
        for (Job job : jobList) {
            builder.append(job.getName());
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length() - 1);
        return builder;
    }

    private void setEventDuration() {
        jobsDuration = TimeUtil.sumTimeOfServices(jobList.stream()
                .map(Job::getDurationTime)
                .collect(Collectors.toList()));
        eventDuration.setText(TimeUtil.formatLocalTime(jobsDuration));
    }

    private void setValueOfTheJobs() {
        BigDecimal sumValueOfServices = jobList.stream()
                .map(Job::getValueOfJob)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        valueOfTheJobs.setText(CoinUtil.formatBrWithoutSymbol(sumValueOfServices));
    }

    private void checkRequiredFieldsAndSaveListener() {
        Button btnSaveEvent = findViewById(R.id.activity_new_event_btn_save_event);
        btnSaveEvent.setOnClickListener(v -> {
            setEndTimeAndValueService();
            checkRequiredFields();
        });
    }

    private void checkRequiredFields() {
        if (checkFields()) {
            getByDateFromApi();
        } else {
            requiredFieldsAlertDialog();
        }
    }

    private void getByDateFromApi() {
        repository.getByDateFromApi(event.getEvent().getEventDate(), new ResultsCallBack<List<EventDto>>() {
            @Override
            public void onSuccess(List<EventDto> resultado) {
                checkTimeAndSetResult(EventWithJobs.convert(resultado));
            }

            @Override
            public void onError(String erro) {
                showError(erro);
            }
        });
    }

    private void setEndTimeAndValueService() {
        LocalTime eventEndTime = event.getEvent().getStarTime().plusHours(jobsDuration.getHour()).plusMinutes(jobsDuration.getMinute());
        event.getEvent().setEndTime(eventEndTime);
        BigDecimal valueService = new BigDecimal(CoinUtil.formatPriceSave(Objects.requireNonNull(valueOfTheJobs.getText()).toString()));
        event.getEvent().setValueEvent(valueService);
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
        int backgroundResource = editText.getText().toString().isEmpty() ? R.drawable.custom_invalid_input : R.drawable.custom_default_input;
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

    private void checkTimeAndSetResult(List<EventWithJobs> eventList) {
        eventList.sort(new SortByEventStartTime());
        if (checkStartAndEndTimeAlertDialog(eventList))
            setResultEvent();
    }

    private boolean checkStartAndEndTimeAlertDialog(List<EventWithJobs> eventList) {
        return isStartTimeAvaliable(eventList) && isEndTimeAvaliable(eventList);
    }

    private boolean isEndTimeAvaliable(List<EventWithJobs> eventList) {
        LocalTime reduzedEndTime = event.getEvent().checkEndTime(eventList);
        if (reduzedEndTime != null) {
            eventEndTimeAlertDialog(reduzedEndTime);
            return false;
        }
        return true;
    }

    private boolean isStartTimeAvaliable(List<EventWithJobs> eventList) {
        if (event.getEvent().checkStartTime(eventList)) {
            eventStartTimeAlerDialog();
            eventStartTime.setBackgroundResource(R.drawable.custom_invalid_input);
            return false;
        }
        return true;
    }

    public void eventStartTimeAlerDialog() {
        new AlertDialog
                .Builder(this)
                .setTitle("Horário Inicial Indisponível")
                .setPositiveButton("Ok", null)
                .show();
    }

    public void eventEndTimeAlertDialog(LocalTime reduzedEndTime) {
        new AlertDialog
                .Builder(this)
                .setTitle("Tempo de duração excede o tempo disponível na agenda!")
                .setMessage("Deseja reduzir o tempo de duração?")
                .setPositiveButton("Sim", (dialogInterface, i) -> {
                    event.getEvent().setEndTime(reduzedEndTime);
                    setResultEvent();
                })
                .setNegativeButton("Não", (dialogInterface, i) ->
                        eventDuration.setBackgroundResource(R.drawable.custom_invalid_input))
                .show();
    }

    private void setResultEvent() {
        Intent intent = newIntent();
        setResult(intent);
        CalendarUtil.selectedDate = event.getEvent().getEventDate();
        finish();
    }

    private void setResult(Intent intent) {
        if (getIntent().hasExtra(KEY_UPDATE_EVENT)) {
            isUpdateEvent(intent);
            isNewEvent(intent);
        } else {//new event click fab button
            isNewEvent(intent);
        }
    }

    private Intent newIntent() {
        Intent intent = new Intent();
        intent.putExtra(KEY_JOB, (Serializable) jobList);
        intent.putExtra(KEY_CLIENT, client);
        return intent;
    }

    private void isNewEvent(Intent intent) {
        if (!event.getEvent().checkId()) {
            intent.putExtra(KEY_INSERT_EVENT, event);
            setResult(REQUEST_CODE_NEW_EVENT, intent);
        }
    }

    private void isUpdateEvent(Intent intent) {
        if (event.getEvent().checkId()) {
            intent.putExtra(KEY_UPDATE_EVENT, event);
            setResult(REQUEST_CODE_UPDATE_EVENT, intent);
        }
    }

    private TimePickerDialog.OnTimeSetListener timePickerDialogListener() {
        return (view, hour, minute) -> {
            LocalTime timeWatch = LocalTime.of(hour, minute);
            String timeFormated = TimeUtil.formatLocalTime(timeWatch);
            isStartTimeDiaolog(timeWatch, timeFormated);
            isEventDurationDialog(timeWatch, timeFormated);
        };
    }

    private void isEventDurationDialog(LocalTime timeWatch, String timeFormated) {
        if (getSupportFragmentManager().findFragmentByTag(TAG_EVENT_DURATION) != null) {
            eventDuration.setText(timeFormated);
            jobsDuration = timeWatch;
        }
    }


    private void isStartTimeDiaolog(LocalTime timeWatch, String timeFormated) {
        if (getSupportFragmentManager().findFragmentByTag(TAG_EVENT_START_TIME) != null) {
            eventStartTime.setText(timeFormated);
            event.getEvent().setStarTime(timeWatch);
        }
    }
}