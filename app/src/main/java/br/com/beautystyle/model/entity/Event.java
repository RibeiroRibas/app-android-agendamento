package br.com.beautystyle.model.entity;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.model.enuns.StatusPagamento;

@Entity
public class Event implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Long eventId =0L;
    private Long clientCreatorId =0L;
    private LocalDate eventDate;
    private LocalTime starTime;
    private LocalTime endTime;
    private BigDecimal valueEvent;
    private StatusPagamento statusPagamento;
    private Long companyId;
    private Long apiId;

    @Ignore
    public Event(LocalTime starTime) {
        this.starTime = starTime;
    }

    public Event() {
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getClientCreatorId() {
        return clientCreatorId;
    }

    public void setClientCreatorId(Long clientCreatorId) {
        this.clientCreatorId = clientCreatorId;
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

    public boolean checkIsEmptyEvent() {
        return endTime != null;
    }

    public LocalTime checkEndTime(List<EventWithClientAndJobs> eventList) {
        LocalTime reducedEndTime = null;
        for (EventWithClientAndJobs ev : eventList) {
            if (ev.getEvent().getStarTime().isAfter(getStarTime())
                    && getEndTime().isAfter(ev.getEvent().getStarTime())
                    && !getEventId().equals(ev.getEvent().getEventId())) {
                reducedEndTime = ev.getEvent().getStarTime();
            }
        }
        return reducedEndTime;
    }

    public boolean checkStartTime(List<EventWithClientAndJobs> eventList) {
        return eventList.stream()
                .filter(ev -> !ev.getEvent().getEventId().equals(getEventId()))
                .anyMatch(event1 -> !getStarTime().isBefore(event1.getEvent().getStarTime()) &&
                        getStarTime().isBefore(event1.getEvent().getEndTime()));
    }

    public boolean checkId() {
        return eventId>0;
    }

    public boolean isNotExistOnApi(List<Event> events) {
        for (Event fromApi : events) {
            if (this.apiId.equals(fromApi.getApiId())) {
                return false;
            }
        }
        return true;
    }
}
