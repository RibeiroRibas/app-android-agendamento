package br.com.beautystyle.ui.adapter.listview;

import static android.content.ContentValues.TAG;
import static br.com.beautystyle.util.ConstantsUtil.DESIRED_FORMAT;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.beautystyle.R;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.Costumer;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.model.enuns.StatusPagamento;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.CreateListsUtil;
import br.com.beautystyle.util.SortByJobName;
import br.com.beautystyle.util.TimeUtil;

public class EventListAdapter extends BaseAdapter {

    private final Context context;
    private final List<EventWithClientAndJobs> events;

    public EventListAdapter(Context context) {
        this.context = context;
        this.events = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return events.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        return events.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position < events.size()) {
            return events.get(position).getEvent().getEventId();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View inflatedView = inflateView(parent);
        if (position < events.size()) { // listEventTime.size + 2
            onBindEvent(position, inflatedView);
        } else {// if position > listEventTime.size + 2 (increased size because list going to behind of navigation bottom).
            inflatedView.setVisibility(View.INVISIBLE);
            setLayoutParamCardView(inflatedView);
        }
        return inflatedView;
    }

    private View inflateView(ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
    }

    private void onBindEvent(int position, View inflatedView) {
        EventWithClientAndJobs event = events.get(position);
        if (event.getEvent().checkIsEmptyEvent()) {
            onBindClient(event.getClient(), inflatedView);
            onBindJob(event.getJobs(), inflatedView);
            SetBackgroundColor(event, inflatedView);
            setStartTime(event.getEvent(), inflatedView);
            setEventDuration(event.getEvent(), inflatedView);
            setEventValue(event.getEvent(), inflatedView);
        } else {
            setStartTime(event.getEvent(), inflatedView);
            setLayoutParamCardView(inflatedView);
        }
    }

    private void onBindClient(Costumer costumer, View inflatedView) {
        if (costumer != null) {
            setNameClient(costumer.getName(), inflatedView);
        }
    }

    private void SetBackgroundColor(EventWithClientAndJobs event, View viewCriada) {
        ImageView imageview = viewCriada.findViewById(R.id.item_event_toolbar);
        CardView cardView = viewCriada.findViewById(R.id.item_event_cardView);
        if (isEventNotComplete(event.getEvent())
                && isEventScheduleByClient(event.getClient())) {
            imageview.setBackgroundColor(Color.parseColor("#FACDEC"));
            cardView.setCardBackgroundColor(Color.parseColor("#FEE2F5"));
        } else if (isEventNotComplete(event.getEvent()) &&
                !isEventScheduleByClient(event.getClient())) {
            imageview.setBackgroundColor(Color.parseColor("#FFFF00"));
            cardView.setCardBackgroundColor(Color.parseColor("#FFFCBB"));
        }
    }

    private boolean isEventScheduleByClient(Costumer costumer) {
        try {
            return costumer.isClientAnUser();
        } catch (Exception e) {
            Log.i(TAG, "client user id is null" + e.getMessage());
            return false;
        }
    }

    private boolean isEventNotComplete(Event event) {
        return event.getEventDate().isAfter(LocalDate.now()) ||
                event.getEndTime().isAfter(LocalTime.now()) &&
                        event.getEventDate().equals(LocalDate.now());
    }

    private void setStartTime(Event event, View viewCriada) {
        TextView hourEvent = viewCriada.findViewById(R.id.item_event_start_time);
        String timeFormatted = TimeUtil.formatLocalTime(event.getStarTime());
        hourEvent.setText(timeFormatted);
    }

    private void setEventDuration(Event event, @NonNull View viewCriada) {
        TextView eventDuration = viewCriada.findViewById(R.id.item_event_duration);
        String endTime = TimeUtil.formatLocalTime(event.getEndTime());
        String startTime = TimeUtil.formatLocalTime(event.getStarTime());
        String concatenate = startTime + " - " + endTime;
        eventDuration.setText(concatenate);
    }

    private void setNameClient(String name, View inflatedView) {
        TextView nameClient = inflatedView.findViewById(R.id.item_event_name);
        nameClient.setText(name);
    }

    private void onBindJob(List<Job> jobs, @NonNull View viewCriada) {
        jobs.sort(new SortByJobName());
        TextView jobName = viewCriada.findViewById(R.id.item_event_service);
        concatenateJobsName(jobs, jobName);
    }

    private void concatenateJobsName(List<Job> jobList, TextView jobName) {
        if (!jobList.isEmpty()) {
            jobName.setText("*  ");
            String concatJobNames;
            for (Job job : jobList) {
                if (isLastIndex(jobList, job)) {
                    concatJobNames = jobName.getText() + job.getName() + "\n";
                } else {
                    concatJobNames = jobName.getText() + job.getName() + "\n \n" + "*  ";
                }
                jobName.setText(concatJobNames);
            }
        }
    }

    private boolean isLastIndex(List<Job> jobList, Job job) {
        return job == jobList.get(jobList.size() - 1);
    }

    private void setEventValue(Event event, View viewCriada) {
        TextView value = viewCriada.findViewById(R.id.item_event_status_cash);
        BigDecimal valueEvent = event.getValueEvent();
        String formattedValue = (CoinUtil.format(valueEvent, DESIRED_FORMAT));
        value.setText(formattedValue);
        checkPaymentStatus(event, value);
    }

    private void checkPaymentStatus(Event event, TextView value) {
        if (isReceived(event)) {
            value.setTextColor(Color.parseColor("#228C22"));
        } else {
            value.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    private boolean isReceived(Event event) {
        return event.getStatusPagamento().equals(StatusPagamento.RECEBIDO);
    }

    private void setLayoutParamCardView(@NonNull View viewCriada) {
        CardView cardView = viewCriada.findViewById(R.id.item_event_cardView);
        ViewGroup.LayoutParams cardViewParams = cardView.getLayoutParams();
        cardViewParams.height = 50;
        cardView.setLayoutParams(cardViewParams);
    }

    public void update(List<EventWithClientAndJobs> eventWithClientAndJobs) {
        CreateListsUtil.createEventList(eventWithClientAndJobs);
        this.events.clear();
        this.events.addAll(CreateListsUtil.events);
        notifyDataSetChanged();
    }
}
