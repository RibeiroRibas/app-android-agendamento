package br.com.beautystyle.ui.adapter.listview;

import static br.com.beautystyle.ui.adapter.ConstantsAdapter.BLOCK_TIME;
import static br.com.beautystyle.util.ConstantsUtil.DESIRED_FORMAT;

import android.content.Context;
import android.graphics.Color;
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
import java.time.LocalTime;
import java.util.List;

import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.BlockTime;
import br.com.beautystyle.model.entity.Customer;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.model.entity.OpeningHours;
import br.com.beautystyle.retrofit.model.dto.EventWithClientAndJobsDto;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.EventListUtil;
import br.com.beautystyle.util.SortByJobName;
import br.com.beautystyle.util.TimeUtil;

public class EventListAdapter extends BaseAdapter {

    private final Context context;
    private EventWithClientAndJobsDto events;
    private View inflatedView;

    public EventListAdapter(Context context) {
        this.context = context;
        events = new EventWithClientAndJobsDto();
    }

    @Override
    public int getCount() {
        return events.getEvents().size() + 2;
    }

    @Override
    public Object getItem(int position) {
        return events.getEvents().get(position);
    }

    @Override
    public long getItemId(int position) {
        List<EventWithClientAndJobs> eventsWithClientAndJobs = this.events.getEvents();
        if (position < eventsWithClientAndJobs.size()) {
            if (eventsWithClientAndJobs.get(position).isEventNotNull())
                return eventsWithClientAndJobs.get(position).getEvent().getId();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        inflatedView = inflateView(parent);
        if (position < events.getEvents().size()) { // listEventTime.size + 2
            onBindEvent(position);
        } else { // if position > listEventTime.size + 2
            // (increased size because list going to behind of navigation bottom).
            inflatedView.setVisibility(View.INVISIBLE);
            setLayoutParamCardView();
        }
        return inflatedView;
    }

    private View inflateView(ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
    }

    private void onBindEvent(int position) {
        EventWithClientAndJobs event = events.getEvents().get(position);
        if (event.getEvent().isEventIdNotNull()) {
            onBindEvent(event);
        } else {
            boolean isNotTimeBlocked = true;
            for (BlockTime blockTime : events.getBlockTimes()) {
                if (blockTime.getStartTime().equals(event.getEvent().getStartTime())) {
                    event.setBlockTime(blockTime);
                    onBindBlockTime(blockTime);
                    isNotTimeBlocked = false;
                }
            }
            if (isNotTimeBlocked) {
                onBindAvailableTime(event);
            }
        }
    }

    private void onBindAvailableTime(EventWithClientAndJobs event) {
        setStartTime(event.getEvent().getStartTime());
        setLayoutParamCardView();
    }

    private void onBindBlockTime(BlockTime blockTime) {
        setTitle(BLOCK_TIME);
        setStartTime(blockTime.getStartTime());
        setDuration(blockTime.getStartTime(), blockTime.getEndTime());
        setReason(blockTime.getReason());
        setBackGroundColor("#CC99FF", "#E6E6FA");
    }

    private void onBindEvent(EventWithClientAndJobs event) {
        onBindClient(event.getCustomer(), inflatedView);
        onBindJob(event.getJobs(), inflatedView);
        setEventBackgroundColor(event);
        setStartTime(event.getEvent().getStartTime());
        setDuration(event.getEvent().getStartTime(), event.getEvent().getEndTime());
        setEventValue(event.getEvent(), inflatedView);
    }

    private void setReason(String reason) {
        reason = "* Motivo: " + reason + "\n";
        TextView reasonTextView = inflatedView.findViewById(R.id.item_event_service);
        reasonTextView.setText(reason);
    }

    private void onBindClient(Customer customer, View inflatedView) {
        if (customer != null) {
            setTitle(customer.getName());
        }
    }

    private void setEventBackgroundColor(EventWithClientAndJobs event) {
        if (event.isNotOver() && event.isUserCustomer()) {
            setBackGroundColor("#FACDEC", "#FEE2F5");
        } else if (event.isNotOver() && !event.isUserCustomer()) {
            setBackGroundColor("#FFFF00", "#FFFCBB");
        }
    }

    private void setBackGroundColor(String imageViewColor, String cardViewColor) {
        ImageView imageview = inflatedView.findViewById(R.id.item_event_toolbar);
        CardView cardView = inflatedView.findViewById(R.id.item_event_cardView);
        imageview.setBackgroundColor(Color.parseColor(imageViewColor));
        cardView.setCardBackgroundColor(Color.parseColor(cardViewColor));
    }

    private void setStartTime(LocalTime startTime) {
        TextView hourEvent = inflatedView.findViewById(R.id.item_event_start_time);
        String timeFormatted = TimeUtil.formatLocalTime(startTime);
        hourEvent.setText(timeFormatted);
    }

    private void setDuration(LocalTime startTime, LocalTime endTime) {
        TextView eventDuration = inflatedView.findViewById(R.id.item_event_duration);
        String endTimeFormatted = TimeUtil.formatLocalTime(endTime);
        String startTimeFormatted = TimeUtil.formatLocalTime(startTime);
        String concatenate = startTimeFormatted + " - " + endTimeFormatted;
        eventDuration.setText(concatenate);
    }

    private void setTitle(String name) {
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
        BigDecimal valueEvent = event.getValue();
        String formattedValue = (CoinUtil.format(valueEvent, DESIRED_FORMAT));
        value.setText(formattedValue);
        checkPaymentStatus(event, value);
    }

    private void checkPaymentStatus(Event event, TextView value) {
        if (event.isHasPaymentReceived()) {
            value.setTextColor(Color.parseColor("#228C22"));
        } else {
            value.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    private void setLayoutParamCardView() {
        CardView cardView = inflatedView.findViewById(R.id.item_event_cardView);
        ViewGroup.LayoutParams cardViewParams = cardView.getLayoutParams();
        cardViewParams.height = 50;
        cardView.setLayoutParams(cardViewParams);
    }

    public void update(EventWithClientAndJobsDto eventWithClientAndJobs,
                       List<OpeningHours> openingHours) {
        EventListUtil eventListUtil = new EventListUtil(eventWithClientAndJobs, openingHours);
        events = eventListUtil.createEventList();
        notifyDataSetChanged();
    }
}
