package br.com.beautystyle.domain.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
public class Services implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private int serviceId = 0;
    private String name;
    private BigDecimal valueOfService;
    private LocalTime timeOfDuration;

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getValueOfService() {
        return valueOfService;
    }

    public LocalTime getTimeOfDuration() {
        return timeOfDuration;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValueOfService(BigDecimal valueOfService) {
        this.valueOfService = valueOfService;
    }

    public void setTimeOfDuration(LocalTime timeOfDuration) {
        this.timeOfDuration = timeOfDuration;
    }

}
