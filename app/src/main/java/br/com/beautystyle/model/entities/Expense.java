package br.com.beautystyle.model.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import br.com.beautystyle.model.enuns.Category;
import br.com.beautystyle.model.enuns.RepeatOrNot;

@Entity
public class Expense implements Serializable {

    @PrimaryKey
    private int id;
    private String description;
    private BigDecimal price;
    private LocalDate date;
    private Category category;
    private RepeatOrNot repeatOrNot;
    public RepeatOrNot getRepeatOrNot() {
        return repeatOrNot;
    }

    public Expense() {

    }

    @Ignore
    public Expense(String description, BigDecimal price, LocalDate date, Category category, RepeatOrNot repeatOrNot) {
        this.description = description;
        this.price = price;
        this.date = date;
        this.category = category;
        this.repeatOrNot = repeatOrNot;
    }

    public void setRepeatOrNot(RepeatOrNot repeatOrNot) {
        this.repeatOrNot = repeatOrNot;
    }

    public Category getCategory() {
        return category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }


}
