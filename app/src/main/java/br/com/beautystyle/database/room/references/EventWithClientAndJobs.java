package br.com.beautystyle.database.room.references;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.model.entity.Client;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.EventJobCroosRef;
import br.com.beautystyle.model.entity.Job;

public class EventWithClientAndJobs implements Serializable {

    @Embedded
    public Event event = new Event();

    @Relation(
            parentColumn = "clientCreatorId",
            entityColumn = "clientId"
    )
    private Client client = new Client();

    @Relation(
            parentColumn = "eventId",
            entityColumn = "jobId",
            associateBy = @Junction(EventJobCroosRef.class)
    )
    public List<Job> jobs = new ArrayList<>();

    public EventWithClientAndJobs() {
    }

    @Ignore
    public EventWithClientAndJobs(LocalTime startTime) {
        this.event = new Event(startTime);
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Event getEvent() {
        return event;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "EventWithClientAndJobs{" +
                "event=" + event +
                ", client=" + client +
                ", jobs=" + jobs +
                '}';
    }
}
