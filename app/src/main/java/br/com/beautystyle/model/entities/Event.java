package br.com.beautystyle.model.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import br.com.beautystyle.database.room.references.EventWithJobs;
import br.com.beautystyle.model.EventDto;
import br.com.beautystyle.model.enuns.StatusPagamento;

@Entity(foreignKeys = {
        @ForeignKey(onDelete = CASCADE, entity = Client.class,
                parentColumns = "clientId", childColumns = "client")
}, indices = {
                @Index("client")
        })
public class Event implements Serializable {

    @PrimaryKey
    private Long eventId = 0L;
    private Long client;
    private LocalDate eventDate;
    private LocalTime starTime;
    private LocalTime endTime;
    private BigDecimal valueEvent;
    private StatusPagamento statusPagamento;

    @Ignore
    public Event(EventDto eventDto) {
        this.eventId = eventDto.getEventId();
        this.client = eventDto.getClient().getClientId();
        this.eventDate = eventDto.getEventDate();
        this.starTime = eventDto.getStarTime();
        this.endTime = eventDto.getEndTime();
        this.valueEvent = eventDto.getValueEvent();
        this.statusPagamento = eventDto.getStatusPagamento();
    }

    public static List<Event> convert(List<EventDto> eventListDto) {
        return eventListDto.stream().map(Event::new).collect(Collectors.toList());
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getClient() {
        return client;
    }

    public void setClient(Long client) {
        this.client = client;
    }

    @Ignore
    public Event(LocalTime starTime) {
        this.starTime = starTime;
    }

    public Event() {
    }

    public BigDecimal getValueEvent() {
        return valueEvent;
    }

    public void setValueEvent(BigDecimal valueEvent) {
        this.valueEvent = valueEvent;
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public LocalTime getStarTime() {
        return starTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public void setStarTime(LocalTime starTime) {
        this.starTime = starTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean checkId() {
        return eventId > 0;
    }

    public LocalTime checkEndTime(List<EventWithJobs> eventList) {
        LocalTime reduzedEndTime = null;
        for (EventWithJobs ev : eventList) {
            if (ev.getEvent().getStarTime().isAfter(getStarTime())
                    && getEndTime().isAfter(ev.getEvent().getStarTime())
                    && !getEventId().equals(ev.getEvent().getClient())) {
                reduzedEndTime = ev.getEvent().getStarTime();
            }
        }
        return reduzedEndTime;
    }

    public boolean checkStartTime(List<EventWithJobs> eventList) {
        return eventList.stream()
                .filter(ev -> !ev.getEvent().getEventId().equals(getEventId()))
                .anyMatch(event1 -> !getStarTime().isBefore(event1.getEvent().getStarTime()) &&
                        getStarTime().isBefore(event1.getEvent().getEndTime()));
    }
}
