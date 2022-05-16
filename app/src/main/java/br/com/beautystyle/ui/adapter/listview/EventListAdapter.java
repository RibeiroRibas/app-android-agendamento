package br.com.beautystyle.ui.adapter.listview;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.example.beautystyle.R;

import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.database.room.references.EventWithJobs;
import br.com.beautystyle.model.entities.Client;
import br.com.beautystyle.model.entities.Job;
import br.com.beautystyle.model.enuns.StatusPagamento;
import br.com.beautystyle.repository.ClientRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.CreateListsUtil;
import br.com.beautystyle.util.TimeUtil;

public class EventListAdapter extends BaseAdapter {

    private final Context context;
    private final List<EventWithJobs> eventListWithJobs;

    public EventListAdapter(Context context) {
        this.context = context;
        this.eventListWithJobs = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return eventListWithJobs.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        return eventListWithJobs.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position < eventListWithJobs.size()) {
            return eventListWithJobs.get(position).getEvent().getEventId();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View inflatedView = inflateView(parent);
        if (position < eventListWithJobs.size()) { // listEventTime.size + 2
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
        if (eventListWithJobs.get(position).getEvent().checkId()) {
            onBindClient(position, inflatedView);
            onBindJob(position, inflatedView);
            SetBackgroundColor(inflatedView);
            setStartTime(position, inflatedView);
            setEventDuration(position, inflatedView);
            setValue(position, inflatedView);
        } else {
            setStartTime(position, inflatedView);
            setLayoutParamCardView(inflatedView);
        }
    }

    private void onBindClient(int position, View inflatedView) {
        ClientRepository repository = new ClientRepository(context);
        repository.getById(eventListWithJobs.get(position).getEvent().getClient(), new ResultsCallBack<Client>() {
            @Override
            public void onSuccess(Client client) {
                setNameClient(client.getName(), inflatedView);
            }

            @Override
            public void onError(String erro) {
                showError(erro);
            }
        });
    }

    private void showError(String message) {
        Toast.makeText(context,
                message,
                Toast.LENGTH_LONG).show();
    }

    private void SetBackgroundColor(View viewCriada) {
        ImageView imageview = viewCriada.findViewById(R.id.item_event_toolbar);
        imageview.setBackgroundColor(Color.parseColor("#FFFF00"));

        CardView cardView = viewCriada.findViewById(R.id.item_event_cardView);
        cardView.setBackgroundColor(Color.parseColor("#FFFCBB"));
    }

    private void setStartTime(int position, View viewCriada) {
        TextView hourEvent = viewCriada.findViewById(R.id.item_event_start_time);
        String timeFormated = TimeUtil.formatLocalTime(eventListWithJobs.get(position).getEvent().getStarTime());
        hourEvent.setText(timeFormated);
    }

    private void setEventDuration(int position, @NonNull View viewCriada) {
        TextView eventDuration = viewCriada.findViewById(R.id.item_event_duration);
        String endTime = TimeUtil.formatLocalTime(eventListWithJobs.get(position).getEvent().getEndTime());
        String startTime = TimeUtil.formatLocalTime(eventListWithJobs.get(position).getEvent().getStarTime());
        String concatenate = startTime + " - " + endTime;
        eventDuration.setText(concatenate);
    }

    private void setNameClient(String name, View inflatedView) {
        TextView nameClient = inflatedView.findViewById(R.id.item_event_name);
        nameClient.setText(name);
    }

    private void onBindJob(int position, @NonNull View viewCriada) {
        List<Job> jobList = eventListWithJobs.get(position).getJobList();
        TextView jobName = viewCriada.findViewById(R.id.item_event_service);
        jobName.setText("*  ");
        String concatenate;
        for (Job service : jobList) {
            if (service != jobList.get(jobList.size() - 1)) {
                concatenate = jobName.getText() + service.getName() + "\n \n" + "*  ";
            } else {
                concatenate = jobName.getText() + service.getName() + "\n";
            }
            jobName.setText(concatenate);
        }
    }

    private void setValue(int position, View viewCriada) {
        TextView value = viewCriada.findViewById(R.id.item_event_status_cash);
        String formatedValue = (CoinUtil.formatBr(eventListWithJobs.get(position).getEvent().getValueEvent()));
        value.setText(formatedValue);
        if (eventListWithJobs.get(position).getEvent().getStatusPagamento().equals(StatusPagamento.RECEBIDO)) {
            value.setTextColor(Color.parseColor("#228C22"));
        } else {
            value.setTextColor(Color.parseColor("#FF0000"));
        }
    }

    private void setLayoutParamCardView(@NonNull View viewCriada) {
        CardView cardView = viewCriada.findViewById(R.id.item_event_cardView);
        ViewGroup.LayoutParams cardViewParams = cardView.getLayoutParams();
        cardViewParams.height = 50;
        cardView.setLayoutParams(cardViewParams);
    }

    public void update(List<EventWithJobs> eventWithJobs) {
        CreateListsUtil.createEventList(eventWithJobs);
        this.eventListWithJobs.clear();
        this.eventListWithJobs.addAll(CreateListsUtil.listEventWithJobs);
        notifyDataSetChanged();
    }
}
