package br.com.beautystyle.model;

import java.time.LocalDate;

import br.com.beautystyle.model.entities.Event;
import br.com.beautystyle.model.entities.Expense;

public class Report {

    private LocalDate date;
    private Event event;
    private Expense expense;


    public Report(LocalDate date, Event event) {
        this.date = date;
        this.event = event;
    }

    public Report(LocalDate date, Expense expense) {
        this.date = date;
        this.expense = expense;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }
}

