package br.com.beautystyle.model.util;

import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.Expense;

public class Report {

    private String clientName;
    private LocalDate date;
    private BigDecimal eventValue;
    private String expenseCategory;
    private BigDecimal expenseValue;

    public Report(EventWithClientAndJobs event) {
        clientName = event.getCustomer().getName();
        date = event.getEvent().getEventDate();
        eventValue = event.getEvent().getValue();
    }

    public Report(Expense expense) {
        date = expense.getExpenseDate();
        expenseCategory = expense.getCategory();
        expenseValue = expense.getValue();
    }

    public Report() {
    }

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

    public String getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(String expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public BigDecimal getExpenseValue() {
        return expenseValue;
    }

    public void setExpenseValue(BigDecimal expenseValue) {
        this.expenseValue = expenseValue;
    }
}
