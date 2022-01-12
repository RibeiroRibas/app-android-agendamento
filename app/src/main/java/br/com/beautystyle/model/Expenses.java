package br.com.beautystyle.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Expenses implements Serializable {

    private String description;
    private BigDecimal price;
    private LocalDate date;
    private Category category;
    private int id = 0;

    public Expenses(String description, BigDecimal price, LocalDate date, Category category) {
        this.description = description;
        this.price = price;
        this.date = date;
        this.category = category;
    }

    @Override
    public String toString() {
        return "Expenses{" +
                "description='" + description + '\'' +
                ", price=" + price +
                ", date=" + date +
                ", category=" + category +
                ", id=" + id +
                '}';
    }

    public Category getCategory() {
        return category;
    }

    public Expenses() {
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
