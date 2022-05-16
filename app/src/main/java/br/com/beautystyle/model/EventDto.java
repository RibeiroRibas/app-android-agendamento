package br.com.beautystyle.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import br.com.beautystyle.model.entities.Client;
import br.com.beautystyle.model.entities.Event;
import br.com.beautystyle.model.entities.Job;
import br.com.beautystyle.model.enuns.StatusPagamento;

public class EventDto {

    private Long eventId;
    private Client client;
    private LocalDate eventDate;
    private LocalTime starTime;
    private LocalTime endTime;
    private BigDecimal valueEvent;
    private StatusPagamento statusPagamento;
    private List<Job> jobList;

    public EventDto(Event event,Client client,List<Job> jobList) {
        this.eventId=event.getEventId();
        this.client = client;
        this.eventDate = event.getEventDate();
        this.starTime = event.getStarTime();
        this.endTime = event.getEndTime();
        this.valueEvent = event.getValueEvent();
        this.statusPagamento = event.getStatusPagamento();
        this.jobList = jobList;
    }

    public Long getEventId() {
        return eventId;
    }

    public Client getClient() {
        return client;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public LocalTime getStarTime() {
        return starTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public BigDecimal getValueEvent() {
        return valueEvent;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public List<Job> getJobList() {
        return jobList;
    }

    public Event convert() {
        Event event = new Event();
        event.setEventId(this.getEventId());
        event.setEventDate(this.eventDate);
        event.setValueEvent(this.valueEvent);
        event.setClient(this.getClient().getClientId());
        event.setStarTime(this.starTime);
        event.setEndTime(this.endTime);
        event.setStatusPagamento(this.statusPagamento);
        return event;
    }

    public EventDto() {
    }


    public boolean checkId() {
        return eventId > 0;
    }
}


