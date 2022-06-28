package br.com.beautystyle.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Report {

    private String clientName;
    private LocalDate date;
    private BigDecimal eventValue;
    private String expenceCategory;
    private BigDecimal expenseValue;

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getEventValue() {
        return eventValue;
    }

    public void setEventValue(BigDecimal eventValue) {
        this.eventValue = eventValue;
    }

    public String getExpenceCategory() {
        return expenceCategory;
    }

    public void setExpenceCategory(String expenceCategory) {
        this.expenceCategory = expenceCategory;
    }

    public BigDecimal getExpenseValue() {
        return expenseValue;
    }

    public void setExpenseValue(BigDecimal expenseValue) {
        this.expenseValue = expenseValue;
    }
}
