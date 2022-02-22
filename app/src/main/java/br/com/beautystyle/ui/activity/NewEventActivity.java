package br.com.beautystyle.ui.activity;

import static android.content.ContentValues.TAG;
import static br.com.beautystyle.ui.activity.ContantsActivity.KEY_INSERT_EVENT;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_NEW_EVENT;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_UPDATE_EVENT;
import static br.com.beautystyle.ui.activity.ContantsActivity.TAG_EVENT_DURATION;
import static br.com.beautystyle.ui.activity.ContantsActivity.TAG_EVENT_START_TIME;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATED_SERVICE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_EVENT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_SERVICE;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.beautystyle.R;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.beautystyle.ViewModel.CalendarViewModel;
import br.com.beautystyle.ViewModel.ClientViewModel;
import br.com.beautystyle.ViewModel.EventViewModel;
import br.com.beautystyle.ViewModel.EventWithServicesViewModel;
import br.com.beautystyle.data.db.references.EventWithServices;
import br.com.beautystyle.domain.model.Client;
import br.com.beautystyle.domain.model.Event;
import br.com.beautystyle.domain.model.Services;
import br.com.beautystyle.ui.fragment.ClientListFragment;
import br.com.beautystyle.ui.fragment.ServiceListFragment;
import br.com.beautystyle.ui.fragment.TimePickerFragment;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.TimeUtil;
import io.reactivex.rxjava3.disposables.Disposable;
import me.abhinay.input.CurrencyEditText;
import me.abhinay.input.CurrencySymbols;

public class NewEventActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private EditText searchClient, searchService, eventDate, eventStartTime, servicesDuration;
    private CurrencyEditText valueOfTheServices;
    private CheckBox statusNaoRecebido, statusRecebido;
    private List<Services> serviceList;
    private Event event = new Event();
    private LocalTime eventDuration;
    private DialogFragment timePicker;
    private CalendarViewModel calendarViewModel;
    private EventViewModel eventViewModel;
    private int editedServices = 0;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);

        initWidgets();
        loadEvent();

        // LISTENER
        eventDateListener();
        eventStartTimeListener();
        clientListener();
        serviceListener();
        eventDurationListener();
        paymentStatusListener();

        formatInputEditTextValueService();

        calendarObserve();
        clientObserver();
        servicesObserver();

        checkRequiredFieldsAndSaveListener();
    }

    private void initWidgets() {
        eventViewModel = new ViewModelProvider(this).get(EventViewModel.class);
        eventDuration = LocalTime.of(0, 0);
        serviceList = new ArrayList<>();
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
        if (intentEvent.hasExtra(KEY_UPDATE_EVENT)) {
            event = (Event) intentEvent.getSerializableExtra(KEY_UPDATE_EVENT);
            if (event.getEndTime() == null) { // new event mode (click event list)
                eventStartTime.setText(TimeUtil.formatLocalTime(event.getStarTime()));
                setStatusAndDateEvent();
            } else { // update event mode (click event list)
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

        fillClient();
        fillServiceList();

        eventDuration = event.getEndTime().minusHours(event.getStarTime().getHour())
                .minusMinutes(event.getStarTime().getMinute());
        servicesDuration.setText(TimeUtil.formatLocalTime(eventDuration));
        valueOfTheServices.setText(CoinUtil.formatBrWithoutSymbol(event.getValueEvent()));

        setStatusPagamentoEditMode();
    }

    private void fillClient() {
        ClientViewModel clientViewModel = new ViewModelProvider(this).get(ClientViewModel.class);
        clientViewModel.getClientById(event.getClientId()).doOnSuccess(
                client -> searchClient.setText(client.getName())).subscribe();
    }

    private void fillServiceList() {
        EventWithServicesViewModel eventWithServicesViewModel = new ViewModelProvider(this).get(EventWithServicesViewModel.class);
        eventWithServicesViewModel.getEventWithServices().doOnSuccess(eventWithServices -> {
            List<Services> serviceList = eventWithServices.stream().filter(ev ->
                    ev.getEvent().getEventId() == (event.getEventId()))
                    .map(EventWithServices::getServiceList)
                    .findFirst()
                    .orElse(new ArrayList<>());
            this.serviceList.addAll(serviceList);
            setServicesName();
        }).subscribe();
    }

    private void setStatusPagamentoEditMode() {
        if (event.getStatusPagamento() == Event.StatusPagamento.NAORECEBIDO) {
            statusNaoRecebido.setChecked(true);
        } else {
            statusRecebido.setChecked(true);
        }
    }

    private void eventDateListener() {
        eventDate.setOnClickListener(v -> calendarViewModel.inflateCalendar(this));
    }

    private void eventStartTimeListener() {
        eventStartTime.setOnClickListener(v -> {
            timePicker = new TimePickerFragment(this);
            timePicker.show(getSupportFragmentManager(), TAG_EVENT_START_TIME);
        });
    }

    private void clientListener() {
        searchClient.setOnClickListener(v -> {
            if (getSupportFragmentManager().findFragmentById(R.id.activity_new_event_container_client) == null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_new_event_container_client, new ClientListFragment(eventViewModel))
                        .commit();
        });
    }

    private void serviceListener() {
        searchService.setOnClickListener(v -> {
            if (getSupportFragmentManager().findFragmentById(R.id.activity_new_event_container_service) == null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.activity_new_event_container_service, new ServiceListFragment(eventViewModel))
                        .commit();
        });
    }

    private void eventDurationListener() {
        servicesDuration.setOnClickListener(v -> {
            timePicker = new TimePickerFragment(this);
            timePicker.show(getSupportFragmentManager(), TAG_EVENT_DURATION);
        });
    }

    private void paymentStatusListener() {
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

    private void formatInputEditTextValueService() {
        valueOfTheServices.setCurrency(CurrencySymbols.NONE);
        valueOfTheServices.setDelimiter(false);
        valueOfTheServices.setSpacing(true);
        valueOfTheServices.setDecimals(true);
        valueOfTheServices.setSeparator(".");
    }

    private void calendarObserve() {
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.getDate().observe(this, this::setDate);
    }

    private void setDate(LocalDate date) {
        event.setEventDate(date);
        String formatDateOfEvent = CalendarUtil.formatDate(date);
        eventDate.setText(formatDateOfEvent);
    }

    private void clientObserver() {
        eventViewModel.getClientLive().observe(this, this::setClient);
    }

    private void setClient(Client client) {
        Log.i(TAG, "setClient: id " + client.getId());
        event.setClientId(client.getId());
        searchClient.setText(client.getName());
    }

    private void servicesObserver() {
        eventViewModel.getServiceListLiveData().observe(this, this::setServices);
    }

    private void setServices(List<Services> serviceList) {
        searchService.getText().clear();
        if (!serviceList.isEmpty()) {
            editedServices = 1; // 1 = true
            this.serviceList.clear();
            this.serviceList.addAll(serviceList);
            setServicesName();
            setEventDuration();
            setValueOfTheServices();
        }
    }

    private void setServicesName() {
        StringBuilder builder = new StringBuilder();
        for (Services service : serviceList) {
            builder.append(service.getName());
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length() - 1);
        searchService.setText(builder.toString());
    }

    private void setEventDuration() {
        eventDuration = TimeUtil.sumTimeOfServices(serviceList.stream()
                .map(Services::getTimeOfDuration)
                .collect(Collectors.toList()));
        servicesDuration.setText(TimeUtil.formatLocalTime(eventDuration));
    }

    private void setValueOfTheServices() {
        BigDecimal sumValueOfServices = serviceList.stream()
                .map(Services::getValueOfService)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        valueOfTheServices.setText(CoinUtil.formatBrWithoutSymbol(sumValueOfServices));
    }

    private void checkRequiredFieldsAndSaveListener() {
        Button btnSaveEvent = findViewById(R.id.activity_new_event_btn_save_event);
        btnSaveEvent.setOnClickListener(v -> {
            setEndTimeAndValueService();
            if (checkRequiredFields()) {
                disposable = eventViewModel.getByDate(event.getEventDate()).subscribe(this::checkTimeAndSetResult);
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

    public void requiredFieldsAlertDialog() {
        new AlertDialog
                .Builder(this)
                .setTitle("Todos os campos são obrigatórios")
                .setPositiveButton("Ok", null)
                .show();
    }

    private void checkTimeAndSetResult(List<Event> eventList) {
        eventList.sort(Comparator.comparing(Event::getStarTime));
        if (checkStartAndEndTimeAlertDialog(eventList))
            setResultEvent();
    }

    private boolean checkStartAndEndTimeAlertDialog(List<Event> eventList) {
        LocalTime reduzedEndTime = event.checkEndTime(eventList);
        if (event.checkStartTime(eventList)) {
            eventStartTimeAlerDialog();
            eventStartTime.setBackgroundResource(R.drawable.custom_invalid_input);
            return false;
        } else if (reduzedEndTime != null) {
            eventEndTimeAlertDialog(reduzedEndTime);
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
                    event.setEndTime(reduzedEndTime);
                    setResultEvent();
                })
                .setNegativeButton("Não", (dialogInterface, i) -> servicesDuration.setBackgroundResource(R.drawable.custom_invalid_input))
                .show();
    }

    private void setResultEvent() {
        Intent intent = new Intent();
        intent.putExtra(KEY_SERVICE, (Serializable) serviceList);
        if (event.checkId()) {
            intent.putExtra(KEY_UPDATED_SERVICE, editedServices);
            intent.putExtra(KEY_UPDATE_EVENT, event);
            setResult(REQUEST_CODE_UPDATE_EVENT, intent);
        } else {
            intent.putExtra(KEY_INSERT_EVENT, event);
            setResult(REQUEST_CODE_NEW_EVENT, intent);
        }
        CalendarUtil.selectedDate = event.getEventDate();
        finish();
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        LocalTime timeWatch = LocalTime.of(hour, minute);
        String timeFormated = TimeUtil.formatLocalTime(timeWatch);
        if(timePicker.getTag()!=null) {
            switch (timePicker.getTag()) {
                case (TAG_EVENT_START_TIME):
                    eventStartTime.setText(timeFormated);
                    event.setStarTime(timeWatch);
                    break;
                case (TAG_EVENT_DURATION):
                    servicesDuration.setText(timeFormated);
                    eventDuration = timeWatch;
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null)
            disposable.dispose();
    }
}