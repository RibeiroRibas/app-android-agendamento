package br.com.beautystyle.ui.activity;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_EVENT;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.ArraySet;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import br.com.beautystyle.dao.EventDao;
import br.com.beautystyle.model.Client;
import br.com.beautystyle.model.Event;
import br.com.beautystyle.model.Services;
import br.com.beautystyle.ui.fragment.ListClientFragment;
import br.com.beautystyle.ui.fragment.TimePickerFragment;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.TimeUtil;
import com.example.beautystyle.R;
import br.com.beautystyle.ui.adapter.ListClientAdapter;
import br.com.beautystyle.ui.fragment.ListServiceFragment;
import br.com.beautystyle.util.CoinUtil;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class NewEventActivity extends AppCompatActivity implements ListClientAdapter.OnClientListener, ListServiceFragment.OnServiceListener, View.OnClickListener,TimePickerDialog.OnTimeSetListener{

    private EditText searchClient, searchService, eventDate, eventStartTime, servicesDuration;
    public EditText valueOfTheServices;
    private CheckBox statusNaoRecebido, statusRecebido;
    private Set<Services> listServiceSet = new ArraySet<>();
    private Event event = new Event();
    private final EventDao eventDao = new EventDao();
    private LocalTime eventDuration;
    private DialogFragment timePicker=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        initWidgets();
        setStatusPagamento();
        loadEvent();
        setEventDateListener();
        setEventStartTimeListener();
        startClientFragment();
        startServiceFragment();
        setDurationEventListener();
        formatInputEditTextValueService();
        saveEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initWidgets() {
        eventDate = findViewById(R.id.et_date_of_event);
        searchService = findViewById(R.id.et_find_service);
        searchClient = findViewById(R.id.et_find_client);
        valueOfTheServices = findViewById(R.id.et_value_service);
        servicesDuration = findViewById(R.id.et_duration_services);
        eventStartTime = findViewById(R.id.et_event_start_time);
        statusRecebido = findViewById(R.id.checkBoxRecebido);
        statusNaoRecebido = findViewById(R.id.checkBoxNaoRecebido);
    }

    private void setStatusPagamento() {
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

    private void loadEvent() {
        Intent intentEvent = getIntent();
        if (intentEvent.hasExtra(KEY_EVENT)) {
            event = (Event) intentEvent.getSerializableExtra(KEY_EVENT);
            if (event.getEndTime() == null) { // new event mode (click list event)
                eventStartTime.setText(TimeUtil.formatLocalTime(event.getStarTime()));
                setValueStatusAndDateEvent();
            } else { // edit event mode (click list event)
                fillAllForm();
            }
        } else { // new event mode (click Fab button)
            setValueStatusAndDateEvent();
        }
    }

    private void setValueStatusAndDateEvent() {
        valueOfTheServices.setText("0,00"); // value default
        statusNaoRecebido.setChecked(true); // value default
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
        valueOfTheServices.setText(CoinUtil.formatBr(event.getValueEvent()));
        setStatusPagamentoEditMode();
    }

    private void setStatusPagamentoEditMode() {
        if (event.getStatusPagamento() == Event.StatusPagamento.NAORECEBIDO) {
            statusNaoRecebido.setChecked(true);
        } else {
            statusRecebido.setChecked(true);
        }
    }

    private void setEventDateListener() {
        eventDate.setOnClickListener(v -> {
            View inflateViewCalendar = getLayoutInflater().inflate(R.layout.dialog_calendar, null);
            AlertDialog dialogBuilderCalendar = createDiologBuilderCalendar(inflateViewCalendar);
            setOnDateChangeListener(dialogBuilderCalendar, inflateViewCalendar);
        });
    }

    private AlertDialog createDiologBuilderCalendar(View inflateViewCalendar) {
        AlertDialog.Builder dialogCalendar = new AlertDialog.Builder(this);
        dialogCalendar.setView(inflateViewCalendar);
        AlertDialog dialog = dialogCalendar.create();
        dialog.show();
        return dialog;
    }

    private void setOnDateChangeListener(AlertDialog dialogBuilderCalendar, View inflateViewCalendar) {
        CalendarView calendar = inflateViewCalendar.findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            LocalDate dateOfEvent = LocalDate.of(year, month + 1, dayOfMonth);
            event.setEventDate(dateOfEvent);
            String formatDateOfEvent = CalendarUtil.formatDate(dateOfEvent);
            eventDate.setText(formatDateOfEvent);
            dialogBuilderCalendar.dismiss();
        });
    }

    private void setEventStartTimeListener() {
        eventStartTime.setOnClickListener(v -> {
             timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(),"startTime");
        });
    }

    private void startClientFragment() {
        searchClient.setOnClickListener(v -> {
            if (getSupportFragmentManager().findFragmentById(R.id.fcv_find_client) == null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fcv_find_client, new ListClientFragment(this))
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
            if (getSupportFragmentManager().findFragmentById(R.id.fcv_find_service) == null)
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.fcv_find_service, new ListServiceFragment(this))
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
        valueOfTheServices.setText(CoinUtil.formatBr(sumValueOfServices));
    }

    private void setListServices() {
        List<Services> listService = new ArrayList<>(listServiceSet);
        event.setListOfServices(listService);
        listServiceSet = new ArraySet<>();
    }

    private void setDurationEventListener() {
        servicesDuration.setOnClickListener(v -> {
            timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(),"durationNewEvent");
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
        valueOfTheServices.addTextChangedListener(new CoinUtil(this));
    }

    private void saveEvent() {
        Button btnSaveEvent = findViewById(R.id.btn_save_event);
        btnSaveEvent.setOnClickListener(v -> {
            boolean checkRequiredFields = checkRequiredFields();
            boolean checkStartTime;
            boolean checkEndTime = false;
            if (checkRequiredFields) {
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
        BigDecimal valueService = new BigDecimal(CoinUtil.formatBrBigDecimal(valueOfTheServices.getText().toString()));
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
                .setNegativeButton("Não", (dialogInterface, i) -> {
                    servicesDuration.setBackgroundResource(R.drawable.custom_invalid_input);
                })
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
        boolean checkCamposObrigatorios = true;
        if (eventDate.getText().toString().isEmpty()) {
            checkCamposObrigatorios = false;
            eventDate.setBackgroundResource(R.drawable.custom_invalid_input);
        } else {
            eventDate.setBackgroundResource(R.drawable.custom_default_input);
        }

        if (eventStartTime.getText().toString().isEmpty()) {
            checkCamposObrigatorios = false;
            eventStartTime.setBackgroundResource(R.drawable.custom_invalid_input);
        } else {
            eventStartTime.setBackgroundResource(R.drawable.custom_default_input);
        }

        if (searchClient.getText().toString().isEmpty()) {
            checkCamposObrigatorios = false;
            searchClient.setBackgroundResource(R.drawable.custom_invalid_input);
        } else {
            searchClient.setBackgroundResource(R.drawable.custom_default_input);
        }

        if (searchService.getText().toString().isEmpty()) {
            checkCamposObrigatorios = false;
            searchService.setBackgroundResource(R.drawable.custom_invalid_input);
        } else {
            searchService.setBackgroundResource(R.drawable.custom_default_input);
        }

        if (servicesDuration.getText().toString().isEmpty()) {
            checkCamposObrigatorios = false;
            servicesDuration.setBackgroundResource(R.drawable.custom_invalid_input);
        } else {
            servicesDuration.setBackgroundResource(R.drawable.custom_default_input);
        }

        return checkCamposObrigatorios;
    }

    public void saveEventFinish() {
        if (event.checkId()) {
            eventDao.edit(event);
        } else {
            eventDao.save(event);
        }
        CalendarUtil.selectedDate = event.getEventDate();
        finish();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        LocalTime timeWatch = LocalTime.of(hour, minute);
        String timeFormated = TimeUtil.formatLocalTime(timeWatch);
        if(timePicker!=null){
            if(timePicker.getTag().equals("startTime")){
                eventStartTime.setText(timeFormated);
                event.setStarTime(timeWatch);
            }else if(timePicker.getTag().equals("durationNewEvent")) {
                servicesDuration.setText(timeFormated);
                eventDuration = timeWatch;
            }else{

            }
        }
        timePicker=null;
    }

}
