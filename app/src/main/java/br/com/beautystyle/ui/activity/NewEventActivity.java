package br.com.beautystyle.ui.activity;

import static br.com.beautystyle.ui.activity.ContantsActivity.KEY_NEW_EVENT;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_EDIT_EVENT;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_NEW_EVENT;
import static br.com.beautystyle.ui.activity.ContantsActivity.TAG_EVENT_DURATION;
import static br.com.beautystyle.ui.activity.ContantsActivity.TAG_EVENT_START_TIME;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_EDIT_EVENT;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArraySet;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.beautystyle.R;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.beautystyle.ViewModel.CalendarViewModel;
import br.com.beautystyle.dao.EventDao;
import br.com.beautystyle.model.Client;
import br.com.beautystyle.model.Event;
import br.com.beautystyle.model.Services;
import br.com.beautystyle.ui.adapter.recyclerview.ClientListAdapter;
import br.com.beautystyle.ui.fragment.ClientListFragment;
import br.com.beautystyle.ui.fragment.ServiceListFragment;
import br.com.beautystyle.ui.fragment.TimePickerFragment;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.TimeUtil;
import me.abhinay.input.CurrencyEditText;
import me.abhinay.input.CurrencySymbols;

public class NewEventActivity extends AppCompatActivity implements ClientListAdapter.OnClientListener, ServiceListFragment.OnServiceListener, TimePickerDialog.OnTimeSetListener {

    private EditText searchClient, searchService, eventDate, eventStartTime, servicesDuration;
    public CurrencyEditText valueOfTheServices;
    private CheckBox statusNaoRecebido, statusRecebido;
    private Set<Services> listServiceSet = new ArraySet<>();
    private Event event = new Event();
    private LocalTime eventDuration;
    private DialogFragment timePicker = null;
    private CalendarViewModel calendarViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        initWidgets();
        loadEvent();
        setEventDateListener();
        calendarObserve();
        setEventStartTimeListener();
        startClientFragment();
        startServiceFragment();
        setDurationEventListener();
        formatInputEditTextValueService();
        setPaymentStatusListener();

        saveEvent();
    }

    private void initWidgets() {
        eventDate = findViewById(R.id.activity_new_event_event_date);
        searchService = findViewById(R.id.activity_new_event_service);
        searchClient = findViewById(R.id.activity_new_event_client);
        valueOfTheServices = findViewById(R.id.activity_new_event_value);
        servicesDuration = findViewById(R.id.activity_new_event_duration);
        eventStartTime = findViewById(R.id.activity_new_event_start_time);
        statusRecebido = findViewById(R.id.activity_new_event_cb_received);
        statusNaoRecebido = findViewById(R.id.activity_new_event_cb_not_received);
    }


    private void loadEvent() {
        Intent intentEvent = getIntent();
        if (intentEvent.hasExtra(KEY_EDIT_EVENT)) {
            event = (Event) intentEvent.getSerializableExtra(KEY_EDIT_EVENT);
            if (event.getEndTime() == null) { // new event mode (click list event)
                eventStartTime.setText(TimeUtil.formatLocalTime(event.getStarTime()));
                setStatusAndDateEvent();
            } else { // edit event mode (click list event)
                fillAllForm();
            }
        } else { // new event mode (click Fab button)
            setStatusAndDateEvent();
        }
    }

    private void setStatusAndDateEvent() {
        statusNaoRecebido.setChecked(true); // value default
        event.setStatusPagamento(Event.StatusPagamento.NAORECEBIDO);
        eventDate.setText(CalendarUtil.formatDate(CalendarUtil.selectedDate));
        event.setEventDate(CalendarUtil.selectedDate);
    }


    private void fillAllForm() {
        eventDate.setText(CalendarUtil.formatDate(event.getEventDate()));
        eventStartTime.setText(TimeUtil.formatLocalTime(event.getStarTime()));
        searchClient.setText(event.getClient().getName());
        listServiceSet.addAll(event.getListOfServices());
        setServicesName();
        eventDuration = event.getEndTime().minusHours(event.getStarTime().getHour())
                .minusMinutes(event.getStarTime().getMinute());
        servicesDuration.setText(TimeUtil.formatLocalTime(eventDuration));
        valueOfTheServices.setText(CoinUtil.formatBrWithoutSymbol(event.getValueEvent()));
        setStatusPagamentoEditMode();
    }

    private void setStatusPagamentoEditMode() {
        if (event.getStatusPagamento() == Event.StatusPagamento.NAORECEBIDO) {
            statusNaoRecebido.setChecked(true);
        } else {
            statusRecebido.setChecked(true);
        }
    }

    private void calendarObserve() {
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.getDate().observe(this, this::setDate);
    }

    private void setEventDateListener() {
        eventDate.setOnClickListener(v -> calendarViewModel.inflateCalendar(this));
    }

    private void setDate(LocalDate date) {
        event.setEventDate(date);
        String formatDateOfEvent = CalendarUtil.formatDate(date);
        eventDate.setText(formatDateOfEvent);
    }

    private void setEventStartTimeListener() {
        eventStartTime.setOnClickListener(v -> {
            timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), TAG_EVENT_START_TIME);
        });
    }

    private void startClientFragment() {
        searchClient.setOnClickListener(v -> {
            if (getSupportFragmentManager().findFragmentById(R.id.activity_new_event_container_client) == null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_new_event_container_client, new ClientListFragment(this))
                        .commit();
        });
    }

    @Override
    public void onClientClickNewEvent(Client client) {
        searchClient.setText(client.getName());
        event.setClient(client);
    }

    @Override
    public void onClientClickRemoveFragment() {

    }

    private void startServiceFragment() {
        searchService.setOnClickListener(v -> {
            resetService();
            if (getSupportFragmentManager().findFragmentById(R.id.activity_new_event_container_service) == null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_new_event_container_service, new ServiceListFragment(this))
                        .commit();
        });
    }

    private void resetService() {
        searchService.getText().clear();
        if (!listServiceSet.isEmpty())
            listServiceSet.clear();
    }

    @Override
    public void onServiceClickFillForm() {
        setServicesName();
        setEventDuration();
        setValueOfTheServices();
        setListServices();
    }

    private void setServicesName() {
        StringBuilder builder = new StringBuilder();
        for (Services service : listServiceSet) {
            builder.append(service.getName());
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length() - 1);
        searchService.setText(builder.toString());
    }

    private void setEventDuration() {
        eventDuration = TimeUtil.sumTimeOfServices(listServiceSet.stream()
                .map(Services::getTimeOfDuration)
                .collect(Collectors.toList()));
        servicesDuration.setText(TimeUtil.formatLocalTime(eventDuration));
    }

    private void setValueOfTheServices() {
        BigDecimal sumValueOfServices = listServiceSet.stream()
                .map(Services::getValueOfService)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        valueOfTheServices.setText(CoinUtil.formatBrWithoutSymbol(sumValueOfServices));
    }

    private void setListServices() {
        List<Services> listService = new ArrayList<>(listServiceSet);
        event.setListOfServices(listService);
        listServiceSet = new ArraySet<>();
    }

    private void setDurationEventListener() {
        servicesDuration.setOnClickListener(v -> {
            timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), TAG_EVENT_DURATION);
        });
    }

    @Override
    public void onServiceClickAddItemList(Services service) {
        listServiceSet.add(service);
    }

    @Override
    public void onServiceClickRemoveItemList(Services service) {
        listServiceSet.remove(service);
    }


    private void formatInputEditTextValueService() {
        valueOfTheServices.setCurrency(CurrencySymbols.NONE);
        valueOfTheServices.setDelimiter(false);
        valueOfTheServices.setSpacing(true);
        valueOfTheServices.setDecimals(true);
        valueOfTheServices.setSeparator(".");
    }

    private void setPaymentStatusListener() {
        statusRecebido.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                statusNaoRecebido.setChecked(false);
                event.setStatusPagamento(Event.StatusPagamento.RECEBIDO);
            }
        });
        statusNaoRecebido.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                statusRecebido.setChecked(false);
                event.setStatusPagamento(Event.StatusPagamento.NAORECEBIDO);
            }
        }));
    }

    private void saveEvent() {
        EventDao eventDao = new EventDao();
        Button btnSaveEvent = findViewById(R.id.activity_new_event_btn_save_event);
        btnSaveEvent.setOnClickListener(v -> {
            if (checkRequiredFields()) {
                boolean checkStartTime;
                boolean checkEndTime = false;
                setEndTimeAndValueService();
                if (checkStartTime = eventDao.checkStartTime(event)) {
                    eventStartTimeAlerDialog();
                    eventStartTime.setBackgroundResource(R.drawable.custom_invalid_input);
                } else if (checkEndTime = eventDao.checkEndTime(event)) {
                    eventEndTimeAlertDialog();
                }
                if (!checkStartTime && !checkEndTime) {
                    saveEventFinish();
                }
            } else {
                requiredFieldsAlertDialog();
            }
        });
    }

    private void setEndTimeAndValueService() {
        LocalTime eventEndTime = event.getStarTime().plusHours(eventDuration.getHour()).plusMinutes(eventDuration.getMinute());
        event.setEndTime(eventEndTime);
        BigDecimal valueService = new BigDecimal(CoinUtil.formatBrBigDecimal(Objects.requireNonNull(valueOfTheServices.getText()).toString()));
        event.setValueEvent(valueService);
    }

    public void eventStartTimeAlerDialog() {
        new AlertDialog
                .Builder(this)
                .setTitle("Horário Inicial Indisponível")
                .setPositiveButton("Ok", null)
                .show();
    }

    public void eventEndTimeAlertDialog() {
        new AlertDialog
                .Builder(this)
                .setTitle("Tempo de duração excede o tempo disponível na agenda!")
                .setMessage("Deseja reduzir o tempo de duração?")
                .setPositiveButton("Sim", (dialogInterface, i) -> {
                    event.setEndTime(EventDao.reduzedEndTime);
                    saveEventFinish();
                })
                .setNegativeButton("Não", (dialogInterface, i) -> servicesDuration.setBackgroundResource(R.drawable.custom_invalid_input))
                .show();
    }

    public void requiredFieldsAlertDialog() {
        new AlertDialog
                .Builder(this)
                .setTitle("Todos os campos são obrigatórios")
                .setPositiveButton("Ok", null)
                .show();
    }

    private boolean checkRequiredFields() {

        List<Boolean> check = new ArrayList<>();
        check.add(checkFields(eventStartTime));
        check.add(checkFields(searchClient));
        check.add(checkFields(searchService));
        check.add(checkFields(servicesDuration));

        for (Boolean checkInput : check) {
            if (!checkInput)
                return false;
        }

        return true;
    }

    private boolean checkFields(EditText editText) {
        int backgroundResource = editText.getText().toString().isEmpty() ? R.drawable.custom_invalid_input : R.drawable.custom_default_input;
        editText.setBackgroundResource(backgroundResource);
        return backgroundResource == R.drawable.custom_default_input;
    }

    public void saveEventFinish() {
        Intent intent = new Intent();
        if (event.checkId()) {
            intent.putExtra(KEY_EDIT_EVENT, event);
            setResult(REQUEST_CODE_EDIT_EVENT, intent);
        } else {
            intent.putExtra(KEY_NEW_EVENT, event);
            setResult(REQUEST_CODE_NEW_EVENT, intent);
        }
        CalendarUtil.selectedDate = event.getEventDate();
        finish();
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        LocalTime timeWatch = LocalTime.of(hour, minute);
        String timeFormated = TimeUtil.formatLocalTime(timeWatch);
        switch (Objects.requireNonNull(timePicker.getTag())) {
            case (TAG_EVENT_START_TIME):
                eventStartTime.setText(timeFormated);
                event.setStarTime(timeWatch);
                break;
            case (TAG_EVENT_DURATION):
                servicesDuration.setText(timeFormated);
                eventDuration = timeWatch;
                break;
        }
        timePicker = null;
    }
}