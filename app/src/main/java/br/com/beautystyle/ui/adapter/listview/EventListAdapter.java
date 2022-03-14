package br.com.beautystyle.ui.adapter.listview;

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

import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.ViewModel.ClientViewModel;
import br.com.beautystyle.ViewModel.EventWithServicesViewModel;
import br.com.beautystyle.data.database.references.EventWithServices;
import br.com.beautystyle.model.Event;
import br.com.beautystyle.model.Services;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.CreateListsUtil;
import br.com.beautystyle.util.TimeUtil;

public class EventListAdapter extends BaseAdapter {

    private final Context context;
    private final List<Event> eventList;
    private String concatenate;
    private final ClientViewModel clientViewModel;
    private final EventWithServicesViewModel eventWithServicesViewModel;

    public EventListAdapter(Context context, ClientViewModel clientViewModel, EventWithServicesViewModel eventWithServicesViewModel) {
        this.context = context;
        this.clientViewModel = clientViewModel;
        this.eventWithServicesViewModel = eventWithServicesViewModel;
        this.eventList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return eventList.size() + 2;
    }

    @Override
    public Object getItem(int position) {
        return eventList.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (position < eventList.size()) {
            return eventList.get(position).getEventId();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View inflatedView = inflateView(parent);

        if (position < eventList.size()) { // listEventTime.size + 2
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
        if (eventList.get(position).checkId()) {
            onBindClient(position,inflatedView);
            onBindService(position, inflatedView);
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
        clientViewModel.getClientById(eventList.get(position).getClientId())
                .doOnSuccess(client -> setNameClient(client.getName(), inflatedView))
                .subscribe();
    }

    private void SetBackgroundColor(View viewCriada) {
        ImageView imageview = viewCriada.findViewById(R.id.item_event_toolbar);
        imageview.setBackgroundColor(Color.parseColor("#FFFF00"));

        CardView cardView = viewCriada.findViewById(R.id.item_event_cardView);
        cardView.setBackgroundColor(Color.parseColor("#FFFCBB"));
    }

    private void setStartTime(int position, View viewCriada) {
        TextView hourEvent = viewCriada.findViewById(R.id.item_event_start_time);
        String timeFormated = TimeUtil.formatLocalTime(eventList.get(position).getStarTime());
        hourEvent.setText(timeFormated);
    }

    private void setEventDuration(int position, @NonNull View viewCriada) {
        TextView eventDuration = viewCriada.findViewById(R.id.item_event_duration);
        String endTime = TimeUtil.formatLocalTime(eventList.get(position).getEndTime());
        String startTime = TimeUtil.formatLocalTime(eventList.get(position).getStarTime());
        concatenate = startTime + " - " + endTime;
        eventDuration.setText(concatenate);
    }

    private void setNameClient(String name, View inflatedView) {
        TextView nameClient = inflatedView.findViewById(R.id.item_event_name);
        nameClient.setText(name);
    }

    private void onBindService(int position, @NonNull View viewCriada) {
        eventWithServicesViewModel.getEventWithServices().doOnSuccess(eventWithServices -> {
            List<Services> serviceList = getServiceList(eventWithServices,position);

            TextView nameService = viewCriada.findViewById(R.id.item_event_service);
            nameService.setText("*  ");
            for (Services service : serviceList) {
                if (service != serviceList.get(serviceList.size() - 1)) {
                    concatenate = nameService.getText() + service.getName() + "\n \n" + "*  ";
                } else {
                    concatenate = nameService.getText() + service.getName() + "\n";
                }
                nameService.setText(concatenate);
            }
        }).subscribe();
    }

    private List<Services> getServiceList(List<EventWithServices> eventWithServices, int position) {
        return eventWithServices.stream().filter(ev ->
                ev.getEvent().getEventId() == (eventList.get(position).getEventId()))
                .map(EventWithServices::getServiceList)
                .findFirst()
                .orElse(new ArrayList<>());
    }

    private void setValue(int position, View viewCriada) {
        TextView value = viewCriada.findViewById(R.id.item_event_status_cash);
        String formatedValue = (CoinUtil.formatBr(eventList.get(position).getValueEvent()));
        value.setText(formatedValue);
        if (eventList.get(position).getStatusPagamento().equals(Event.StatusPagamento.RECEBIDO)) {
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

    public void update(List<Event> eventList) {
        CreateListsUtil.createListEventTest(eventList);
        this.eventList.clear();
        this.eventList.addAll(CreateListsUtil.listEvent);
        notifyDataSetChanged();
    }

}
