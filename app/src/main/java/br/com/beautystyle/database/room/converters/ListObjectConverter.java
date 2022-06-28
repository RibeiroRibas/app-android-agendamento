package br.com.beautystyle.database.room.converters;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import br.com.beautystyle.database.room.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.Client;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.EventJobCroosRef;
import br.com.beautystyle.model.entity.Job;

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
    public String fromJobs(List<Job> jobs) {
        if (jobs == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Job>>() {}.getType();
        return gson.toJson(jobs, type);
    }

    @TypeConverter
    public List<Job> toJobs(String jobs) {
        if (jobs == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Job>>() {}.getType();
        return gson.fromJson(jobs, type);
    }

    @TypeConverter
    public String fromEventList(List<Event> eventList) {
        if (eventList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Event>>() {}.getType();
        return gson.toJson(eventList, type);
    }

    @TypeConverter
    public List<Event> toEventList(String eventString) {
        if (eventString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Event>>() {}.getType();
        return gson.fromJson(eventString, type);
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

    @TypeConverter
    public String fromEventWithJobs(List<EventWithClientAndJobs> eventWithClientAndJobs) {
        if (eventWithClientAndJobs == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<EventWithClientAndJobs>>() {}.getType();
        return gson.toJson(eventWithClientAndJobs, type);
    }

    @TypeConverter
    public List<EventWithClientAndJobs> toEventWithJobs(String eventWithJobs) {
        if (eventWithJobs == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<EventWithClientAndJobs>>() {}.getType();
        return gson.fromJson(eventWithJobs, type);
    }

}
