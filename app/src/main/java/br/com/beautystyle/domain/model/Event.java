package br.com.beautystyle.domain.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
public class Event  implements Serializable {

    public enum StatusPagamento{
        RECEBIDO,NAORECEBIDO
    }
    @PrimaryKey(autoGenerate = true)
    private int eventId = 0;
    private int clientId;
    private LocalDate eventDate;
    private LocalTime starTime;
    private LocalTime endTime;
    private BigDecimal valueEvent;
    private StatusPagamento statusPagamento;

    @Ignore
    public Event(LocalTime starTime) {
        this.starTime = starTime;
    }

    public Event() {
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
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

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
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

    public boolean checkId(){
        return eventId > 0;
    }

    public LocalTime checkEndTime(List<Event> eventList) {
        LocalTime reduzedEndTime = null;
        for (Event ev : eventList) {
            if (ev.getStarTime().isAfter(getStarTime())
                    && getEndTime().isAfter(ev.getStarTime())
                    && getEventId() != ev.getEventId()) {
                reduzedEndTime = ev.getStarTime();
            }
        }
        return reduzedEndTime;
    }

    public boolean checkStartTime(List<Event> eventList) {
        return eventList.stream()
                .filter(ev -> ev.getEventId() != getEventId())
                .anyMatch(event1 -> !getStarTime().isBefore(event1.getStarTime()) &&
                        getStarTime().isBefore(event1.getEndTime()));
    }
}
