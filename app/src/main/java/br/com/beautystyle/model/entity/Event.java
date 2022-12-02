package br.com.beautystyle.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import br.com.beautystyle.database.references.EventWithClientAndJobs;

@Entity
public class Event implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "eventId")
    @JsonIgnore
    private Long id;
    @JsonIgnore
    private Long customerCreatorId;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal value;
    private boolean hasPaymentReceived;
    private Long tenant;
    private Long apiId;

    @Ignore
    public Event(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Event() {
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public Long getTenant() {
        return tenant;
    }

    @JsonIgnore
    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

    public Long getId() {
        return id;
    }

    @JsonIgnore
    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerCreatorId() {
        return customerCreatorId;
    }

    public void setCustomerCreatorId(Long customerCreatorId) {
        this.customerCreatorId = customerCreatorId;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public LocalTime getStartTime() {
        return startTime;
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

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public boolean isHasPaymentReceived() {
        return hasPaymentReceived;
    }

    public void setHasPaymentReceived(boolean hasPaymentReceived) {
        this.hasPaymentReceived = hasPaymentReceived;
    }

    public LocalTime checkEndTime(List<EventWithClientAndJobs> eventList) {
        LocalTime reducedEndTime = null;
        for (EventWithClientAndJobs ev : eventList) {
            if (ev.getEvent().getStartTime().isAfter(startTime)
                    && endTime.isAfter(ev.getEvent().getStartTime())
                    && !id.equals(ev.getEvent().getId())) {
                reducedEndTime = ev.getEvent().getStartTime();
                break;
            }
        }
        return reducedEndTime;
    }

    @JsonIgnore
    public boolean checkStartTime(List<EventWithClientAndJobs> events) {
        return events.stream()
                .filter(ev -> !ev.getEvent().getId().equals(this.id))
                .anyMatch(ev -> !startTime.isBefore(ev.getEvent().getStartTime()) &&
                        startTime.isBefore(ev.getEvent().getEndTime()));
    }

    @JsonIgnore
    public boolean isEventIdNotNull() {
        return id != null;
    }

    @JsonIgnore
    public boolean isApiIdEquals(Long apiId) {
        return this.apiId != null && this.apiId.equals(apiId);
    }

    @JsonIgnore
    public boolean isNotOver() {
        return eventDate.isAfter(LocalDate.now()) ||
                endTime.isAfter(LocalTime.now()) &&
                        eventDate.equals(LocalDate.now());
    }
}
