package br.com.beautystyle.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Event  implements Serializable {
    public enum StatusPagamento{
        RECEBIDO,NAORECEBIDO
    }
    private int id = 0;
    private LocalDate eventDate;
    private LocalTime starTime;
    private LocalTime endTime;
    private BigDecimal valueEvent;
    private List<Services> listOfServices;
    private Client client;
    private StatusPagamento statusPagamento;

    public Event(LocalDate eventDate, LocalTime starTime, LocalTime endTime, List<Services> listOfServices, Client client,StatusPagamento statusPagamento,BigDecimal valueEvent) {
        this.eventDate = eventDate;
        this.starTime = starTime;
        this.endTime = endTime;
        this.listOfServices = listOfServices;
        this.client = client;
        this.statusPagamento = statusPagamento;
        this.valueEvent = valueEvent;
    }

    public BigDecimal getValueEvent() {
        return valueEvent;
    }

    public void setValueEvent(BigDecimal valueEvent) {
        this.valueEvent = valueEvent;
    }

    public Event() {
    }

    public StatusPagamento getStatusPagamento() {
        return statusPagamento;
    }

    public void setStatusPagamento(StatusPagamento statusPagamento) {
        this.statusPagamento = statusPagamento;
    }

    public Event(LocalTime starTime) {
        this.starTime = starTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Client getClient() {
        return client;
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

    public void setListOfServices(List<Services> listOfServices) {
        this.listOfServices = listOfServices;
    }

    public List<Services> getListOfServices() {
        return listOfServices;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public boolean checkId(){
        return id > 0;
    }
}
