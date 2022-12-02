package br.com.beautystyle.model.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Expense implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private String description;
    private BigDecimal value;
    private LocalDate expenseDate;
    private String category;
    private boolean repeat;
    private Long tenant;
    private Long apiId;

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public Long getTenant() {
        return tenant;
    }

    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public boolean isNotExistOnApi(List<Expense> expensesFromApi) {
        if(this.apiId == null) return true;

        for (Expense fromApi : expensesFromApi) {
            if (this.apiId.equals(fromApi.getApiId())) {
                return false;
            }
        }
        return true;
    }

    public boolean isApiIdEquals(Expense expenseFromApi) {
        return apiId != null && apiId.equals(expenseFromApi.getApiId());
    }
}
