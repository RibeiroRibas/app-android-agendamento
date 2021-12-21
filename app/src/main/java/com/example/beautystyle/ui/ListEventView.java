package com.example.beautystyle.ui;

import android.content.Context;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.beautystyle.dao.EventDao;
import com.example.beautystyle.model.Event;
import com.example.beautystyle.ui.adapter.ListEventAdapter;
import com.example.beautystyle.util.CalendarUtil;

public class ListEventView {
    private final ListEventAdapter adapter;
    private final EventDao eventDao;
    private final Context context;

    public ListEventView(Context context) {
        this.adapter = new ListEventAdapter(context);
        this.eventDao = new EventDao();
        this.context = context;
    }

    public void eventUpdate() {
        adapter.update(CalendarUtil.selectedDate);
    }

    public void setAdapter(ListView listaDeEvento) {
        listaDeEvento.setAdapter(adapter);
    }

    public void checkRemove(final MenuItem itemId) {
        new AlertDialog
                .Builder(context)
                .setTitle("Removendo item da Agenda")
                .setMessage("Tem certeza que deseja remover o item selecionado?")
                .setPositiveButton("Sim", (dialog, which) -> {
                    AdapterView.AdapterContextMenuInfo menuInfo =
                            (AdapterView.AdapterContextMenuInfo) itemId.getMenuInfo();
                    Event chosedEvent = (Event) adapter.getItem(menuInfo.position);
                    if(chosedEvent.getEventDate()!=null){
                        remove(chosedEvent);
                    }else{
                        Toast.makeText(context, "Não é possível remover um horário vazio", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void remove(Event event) {
        eventDao.remove(event);
        adapter.update(event.getEventDate());
    }
}
