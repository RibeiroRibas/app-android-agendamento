package br.com.beautystyle.database.references;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.model.entity.BlockTime;
import br.com.beautystyle.model.entity.Customer;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.EventJobCrossRef;
import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.retrofit.model.form.EventForm;

public class EventWithClientAndJobs implements Serializable {

    @Embedded
    public Event event = new Event();

    @Relation(
            parentColumn = "customerCreatorId",
            entityColumn = "id"
    )
    private Customer customer = new Customer();

    @Relation(
            parentColumn = "eventId",
            entityColumn = "jobId",
            associateBy = @Junction(EventJobCrossRef.class)
    )
    public List<Job> jobs = new ArrayList<>();

    @Ignore
    private BlockTime blockTime;

    public EventWithClientAndJobs() {
    }

    @Ignore
    public EventWithClientAndJobs(LocalTime startTime) {
        this.event = new Event(startTime);
    }

    @Ignore
    public EventWithClientAndJobs(EventForm eventForm) {
        this.event = getEvent();
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
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
        return event.isEventIdNotNull();
    }

    public boolean isApiIdEquals(EventWithClientAndJobs eventFromApi) {
        return event.isApiIdEquals(eventFromApi.getEvent().getApiId());
    }

    public boolean isNotOver() {
        return event.isNotOver();
    }

    public boolean isUserCustomer() {
        return customer.isUser();
    }

    public BlockTime getBlockTime() {
        return blockTime;
    }

    public void setBlockTime(BlockTime blockTime) {
        this.blockTime = blockTime;
    }

    public boolean isBlockTimeNotNull() {
        return blockTime!=null;
    }
}
