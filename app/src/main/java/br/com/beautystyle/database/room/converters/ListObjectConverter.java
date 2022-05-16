package br.com.beautystyle.database.room.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import br.com.beautystyle.model.entities.EventJobCroosRef;
import br.com.beautystyle.model.entities.Client;

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
    public String fromEventWithServicesList(List<EventJobCroosRef> eventWithServiceList) {
        if (eventWithServiceList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<EventJobCroosRef>>() {}.getType();
        return gson.toJson(eventWithServiceList, type);
    }

    @TypeConverter
    public List<EventJobCroosRef> toEventWithServicesList(String eventServiceCroosRef) {
        if (eventServiceCroosRef == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<EventJobCroosRef>>() {}.getType();
        return gson.fromJson(eventServiceCroosRef, type);
    }

}
