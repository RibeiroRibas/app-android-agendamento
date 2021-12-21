package com.example.beautystyle.ui.adapter;

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
import com.example.beautystyle.model.Event;
import com.example.beautystyle.model.Services;
import com.example.beautystyle.ui.CreateListEvent;
import com.example.beautystyle.util.TimeUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ListEventAdapter extends BaseAdapter {
    private final Context context;
    private final List<Event> listEvent = new ArrayList<>();
    private String concatenate;

    public ListEventAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return listEvent.size()+2;
    }

    @Override
    public Object getItem(int position) {
        return listEvent.get(position);
    }

    @Override
    public long getItemId(int position) {
        if(position<listEvent.size()){
            return listEvent.get(position).getId();
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View viewCriada = createView(parent);

        if(position< listEvent.size()){ // listEventTime.size + 2
            setEvent(position, viewCriada);
        }else{// if position > listEventTime.size + 2 (increased size because list going to behind of navigation bottom).
            viewCriada.setVisibility(View.INVISIBLE);
            setLayoutParamCardView(viewCriada);
        }

        return viewCriada;
    }

    private View createView(ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_schedule, parent, false);
    }

    private void setEvent(int position, View viewCriada) {
        if (listEvent.get(position).getEventDate() != null) {
            SetBackgroundColor(viewCriada);
            setStartTime(position,viewCriada);
            setStartAndEndTime(position, viewCriada);
            setNameClient(position, viewCriada);
            setListServices(position, viewCriada);
        } else {
            setStartTime(position,viewCriada);
            setLayoutParamCardView(viewCriada);
        }
    }

    private void setLayoutParamCardView(@NonNull View viewCriada) {
        CardView cardView = viewCriada.findViewById(R.id.item_schedule_cardview);
        ViewGroup.LayoutParams cardViewParams = cardView.getLayoutParams();
        cardViewParams.height = 50;
        cardView.setLayoutParams(cardViewParams);
    }

    private void SetBackgroundColor(View viewCriada) {
        ImageView imageview = viewCriada.findViewById(R.id.item_schedule_toolbar_imageview);
        imageview.setBackgroundColor(Color.parseColor("#FFFF00"));

        CardView cardView = viewCriada.findViewById(R.id.item_schedule_cardview);
        cardView.setBackgroundColor(Color.parseColor("#FFFCBB"));
    }

    private void setStartTime(int position, View viewCriada) {
        TextView hourEvent = viewCriada.findViewById(R.id.item_hour_text_view);
        String timeFormated = TimeUtil.formatLocalTime(listEvent.get(position).getStarTime());
        hourEvent.setText(timeFormated);
    }

    private void setStartAndEndTime(int position, @NonNull View viewCriada) {
        TextView timeOfEvent = viewCriada.findViewById(R.id.item_schedule_hour);
        String endTime = TimeUtil.formatLocalTime(listEvent.get(position).getEndTime());
        String startTime = TimeUtil.formatLocalTime(listEvent.get(position).getStarTime());
        concatenate = startTime + " - " + endTime;
        timeOfEvent.setText(concatenate);
    }

    private void setNameClient(int position, View viewCriada) {
        TextView nameClient = viewCriada.findViewById(R.id.item_schedule_client);
        nameClient.setText(listEvent.get(position).getClient().getName());
    }

    private void setListServices(int position, @NonNull View viewCriada) {
        List<Services> listService = listEvent.get(position).getListOfServices();
        TextView nameService = viewCriada.findViewById(R.id.name_service);
        nameService.setText("*  ");
        for (Services service : listService) {
            if (service != listService.get(listService.size() - 1)) {
                concatenate = nameService.getText() + service.getName() + "\n \n" + "*  ";
                nameService.setText(concatenate);
            } else {
                concatenate = nameService.getText() + service.getName() + "\n";
                nameService.setText(concatenate);
            }
        }
    }

    public void update(LocalDate dataDoEvento) {
        CreateListEvent.createListEventTest(dataDoEvento);
        this.listEvent.clear();
        this.listEvent.addAll(CreateListEvent.listEvent);
        notifyDataSetChanged();
    }


}
