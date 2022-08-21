package br.com.beautystyle.ui.adapter.recyclerview;

import static android.content.ContentValues.TAG;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static br.com.beautystyle.ui.adapter.ConstantsAdapter.ITEM_MENU_REMOVE;
import static br.com.beautystyle.util.ConstantsUtil.DESIRED_FORMAT;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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
import br.com.beautystyle.ui.adapter.recyclerview.listener.AdapterListener;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.CreateListsUtil;
import br.com.beautystyle.util.SortByJobName;
import br.com.beautystyle.util.TimeUtil;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventHolder> {

    private final Context context;
    private final List<EventWithClientAndJobs> events;
    private AdapterListener.OnEventClickListener onItemClickListener;

    public EventAdapter(Context context) {
        this.context = context;
        this.events = new ArrayList<>();
    }

    @NonNull
    @Override
    public EventHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View createdView = inflateLayout(parent);
        return new EventHolder(createdView);
    }

    private View inflateLayout(@NonNull ViewGroup parent) {
        return LayoutInflater.from(context)
                .inflate(R.layout.item_event, parent, false);
    }

    @Override
    public void onBindViewHolder(@NonNull EventHolder holder, int position) {
        if (position < events.size()) {// listEventTime.size + 2
            EventWithClientAndJobs event = events.get(position);
            holder.onBindEvent(event);
        } else { // if position > listEventTime.size + 2 (increased size because list going to behind of navigation bottom).
            holder.setLayoutParamCardView(50, View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return events.size() + 2;
    }

    public void update(List<EventWithClientAndJobs> eventWithClientAndJobs) {
        CreateListsUtil.createEventList(eventWithClientAndJobs);
        notifyItemRangeRemoved(0, events.size());
        this.events.clear();
        this.events.addAll(CreateListsUtil.events);
        notifyItemRangeInserted(0, events.size());
    }

    public void setOnItemClickListener(AdapterListener.OnEventClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public EventWithClientAndJobs getItem(int position) {
        return events.get(position);
    }

    class EventHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        private TextView nameClient, jobName, hourEvent, eventDuration, value;
        private ImageView imageview;
        private CardView cardView;
        private EventWithClientAndJobs event;

        public EventHolder(@NonNull View itemView) {
            super(itemView);
            initWidgets(itemView);
            itemView.setOnCreateContextMenuListener(this);
            itemView.setOnClickListener(v ->
                    onItemClickListener.onItemClick(event)
            );
        }

        private void initWidgets(View itemView) {
            nameClient = itemView.findViewById(R.id.item_event_name);
            jobName = itemView.findViewById(R.id.item_event_service);
            imageview = itemView.findViewById(R.id.item_event_toolbar);
            cardView = itemView.findViewById(R.id.item_event_cardView);
            hourEvent = itemView.findViewById(R.id.item_event_start_time);
            eventDuration = itemView.findViewById(R.id.item_event_duration);
            value = itemView.findViewById(R.id.item_event_status_cash);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(this.getAdapterPosition(), 1, 1, ITEM_MENU_REMOVE);
        }

        public void onBindEvent(EventWithClientAndJobs event) {
            this.event = event;
            if (event.getEvent().checkIsEmptyEvent()) {
                setLayoutParamCardView(WRAP_CONTENT, View.VISIBLE);
                onBindClient(event.getClient());
                onBindJob(event.getJobs());
                SetBackgroundColor(event);
                setStartTime(event.getEvent());
                setEventDuration(event.getEvent());
                setEventValue(event.getEvent());
            } else {
                setStartTime(event.getEvent());
                eventDuration.setText("");
                nameClient.setText("");
                value.setText("");
                imageview.setBackgroundColor(Color.parseColor("#F5F5F5"));
                setLayoutParamCardView(50, View.VISIBLE);
            }
        }

        private void onBindClient(Costumer costumer) {
            if (costumer != null) {
                nameClient.setText(costumer.getName());
            }
        }

        private void SetBackgroundColor(EventWithClientAndJobs event) {
            if (isEventNotComplete(event.getEvent())
                    && isEventScheduleByCostumer(event.getClient())) {
                imageview.setBackgroundColor(Color.parseColor("#FACDEC"));
                cardView.setCardBackgroundColor(Color.parseColor("#FEE2F5"));
            } else if (isEventNotComplete(event.getEvent()) &&
                    !isEventScheduleByCostumer(event.getClient())) {
                imageview.setBackgroundColor(Color.parseColor("#FFFF00"));
                cardView.setCardBackgroundColor(Color.parseColor("#FFFCBB"));
            }
        }

        private boolean isEventScheduleByCostumer(Costumer costumer) {
            try {
                return costumer.isClientAnUser();
            } catch (NullPointerException e) {
                Log.i(TAG, "client user id is null" + e.getMessage());
                return false;
            }
        }

        private boolean isEventNotComplete(Event event) {
            return event.getEventDate().isAfter(LocalDate.now()) ||
                    event.getEndTime().isAfter(LocalTime.now()) &&
                            event.getEventDate().equals(LocalDate.now());
        }

        private void setStartTime(Event event) {
            String timeFormatted = TimeUtil.formatLocalTime(event.getStarTime());
            hourEvent.setText(timeFormatted);
        }

        private void setEventDuration(Event event) {
            String endTime = TimeUtil.formatLocalTime(event.getEndTime());
            String startTime = TimeUtil.formatLocalTime(event.getStarTime());
            String concatenate = startTime + " - " + endTime;
            eventDuration.setText(concatenate);
        }


        private void onBindJob(List<Job> jobs) {
            jobs.sort(new SortByJobName());
            concatenateJobsName(jobs);
        }

        private void concatenateJobsName(List<Job> jobList) {
            if (!jobList.isEmpty()) {
                Log.i(TAG, "concatenateJobsName: ");
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

        private void setEventValue(Event event) {
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

        private void setLayoutParamCardView(int height, int visibility) {
            this.itemView.setVisibility(visibility);
            ViewGroup.LayoutParams cardViewParams = cardView.getLayoutParams();
            cardViewParams.height = height;
            cardView.setLayoutParams(cardViewParams);
        }
    }
}
