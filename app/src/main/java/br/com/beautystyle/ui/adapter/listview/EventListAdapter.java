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

import br.com.beautystyle.model.Event;
import br.com.beautystyle.model.Services;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.CreateListsUtil;
import br.com.beautystyle.util.TimeUtil;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EventListAdapter extends BaseAdapter {
    private final Context context;
    private final List<Event> eventList = new ArrayList<>();
    private String concatenate;

    public EventListAdapter(Context context) {
        this.context = context;
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
            return eventList.get(position).getId();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewCriada = createView(parent);

        if (position < eventList.size()) { // listEventTime.size + 2
            setEvent(position, viewCriada);
        } else {// if position > listEventTime.size + 2 (increased size because list going to behind of navigation bottom).
            viewCriada.setVisibility(View.INVISIBLE);
            setLayoutParamCardView(viewCriada);
        }

        return viewCriada;
    }

    private View createView(ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
    }

    private void setEvent(int position, View viewCriada) {
        if (eventList.get(position).getEventDate() != null) {
            SetBackgroundColor(viewCriada);
            setStartTime(position, viewCriada);
            setStartAndEndTime(position, viewCriada);
            setNameClient(position, viewCriada);
            setListServices(position, viewCriada);
            setValue(position, viewCriada);
        } else {
            setStartTime(position, viewCriada);
            setLayoutParamCardView(viewCriada);
        }
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

    private void setStartAndEndTime(int position, @NonNull View viewCriada) {
        TextView timeOfEvent = viewCriada.findViewById(R.id.item_event_duration);
        String endTime = TimeUtil.formatLocalTime(eventList.get(position).getEndTime());
        String startTime = TimeUtil.formatLocalTime(eventList.get(position).getStarTime());
        concatenate = startTime + " - " + endTime;
        timeOfEvent.setText(concatenate);
    }

    private void setNameClient(int position, View viewCriada) {
        TextView nameClient = viewCriada.findViewById(R.id.item_event_name);
        nameClient.setText(eventList.get(position).getClient().getName());
    }

    private void setListServices(int position, @NonNull View viewCriada) {
        List<Services> listService = eventList.get(position).getListOfServices();
        TextView nameService = viewCriada.findViewById(R.id.item_event_service);
        nameService.setText("*  ");
        for (Services service : listService) {
            if (service != listService.get(listService.size() - 1)) {
                concatenate = nameService.getText() + service.getName() + "\n \n" + "*  ";
            } else {
                concatenate = nameService.getText() + service.getName() + "\n";
            }
            nameService.setText(concatenate);
        }
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

    public void update(LocalDate dataDoEvento) {
        CreateListsUtil.createListEventTest(dataDoEvento);
        this.eventList.clear();
        this.eventList.addAll(CreateListsUtil.listEvent);
        notifyDataSetChanged();
    }


}
