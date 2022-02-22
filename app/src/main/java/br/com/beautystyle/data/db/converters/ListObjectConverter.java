package br.com.beautystyle.data.db.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import br.com.beautystyle.data.db.references.EventServiceCroosRef;
import br.com.beautystyle.domain.model.Client;

public class ListObjectConverter {

    @TypeConverter
    public String fromClientList(List<Client> clientList) {
        if (clientList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Client>>() {}.getType();
        return gson.toJson(clientList, type);
    }

    @TypeConverter
    public List<Client> toClientList(String clientString) {
        if (clientString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Client>>() {}.getType();
        return gson.fromJson(clientString, type);
    }

    @TypeConverter
    public String fromEventWithServicesList(List<EventServiceCroosRef> eventWithServiceList) {
        if (eventWithServiceList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<EventServiceCroosRef>>() {}.getType();
        return gson.toJson(eventWithServiceList, type);
    }

    @TypeConverter
    public List<EventServiceCroosRef> toEventWithServicesList(String eventServiceCroosRef) {
        if (eventServiceCroosRef == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<EventServiceCroosRef>>() {}.getType();
        return gson.fromJson(eventServiceCroosRef, type);
    }
}
