package br.com.beautystyle.database.references;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.model.entity.Costumer;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.EventJobCrossRef;
import br.com.beautystyle.model.entity.Job;

public class EventWithClientAndJobs implements Serializable {

    @Embedded
    public Event event = new Event();

    @Relation(
            parentColumn = "clientCreatorId",
            entityColumn = "clientId"
    )
    private Costumer client = new Costumer();

    @Relation(
            parentColumn = "eventId",
            entityColumn = "jobId",
            associateBy = @Junction(EventJobCrossRef.class)
    )
    public List<Job> jobs = new ArrayList<>();

    public EventWithClientAndJobs() {
    }

    @Ignore
    public EventWithClientAndJobs(LocalTime startTime) {
        this.event = new Event(startTime);
    }

    public Costumer getClient() {
        return client;
    }

    public void setClient(Costumer client) {
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

    public boolean isEventNotNull() {
        return event.getEventId() > 0;
    }
}
