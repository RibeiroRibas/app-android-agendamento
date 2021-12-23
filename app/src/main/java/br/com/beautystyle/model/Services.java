package br.com.beautystyle.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;

public class Services implements Serializable {
    private String name;
    private BigDecimal valueOfService;
    private LocalTime timeOfDuration;
    private int id = 0;

    public Services(String name, BigDecimal valueOfService, LocalTime timeOfDuration) {
        this.name = name;
        this.valueOfService = valueOfService;
        this.timeOfDuration = timeOfDuration;
    }

    public Services() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
