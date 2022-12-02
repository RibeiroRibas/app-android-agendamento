package br.com.beautystyle.model.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Entity
public class Job implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "jobId")
    private Long id;
    private String name;
    private BigDecimal price;
    private LocalTime durationTime;
    private Long tenant;
    private Long apiId;

    @Ignore
    public Job(String name, BigDecimal valueOfJob, LocalTime durationTime) {
        this.name = name;
        this.price = valueOfJob;
        this.durationTime = durationTime;
    }

    public Job() {
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

    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalTime getDurationTime() {
        return durationTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setDurationTime(LocalTime durationTime) {
        this.durationTime = durationTime;
    }

    public boolean isIdNotNull() {
        return id != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equals(id, job.id) && Objects.equals(name, job.name) && Objects.equals(price, job.price) && Objects.equals(durationTime, job.durationTime) && Objects.equals(tenant, job.tenant) && Objects.equals(apiId, job.apiId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, durationTime, tenant, apiId);
    }

    public boolean isNotExistOnApi(List<Job> jobsFromApi) {
        for (Job jobFromApi : jobsFromApi) {
            if (this.apiId.equals(jobFromApi.getApiId())) {
                return false;
            }
        }
        return true;
    }

    public boolean isApiIdEquals(Job jobApi) {
        return apiId != null && apiId.equals(jobApi.getApiId());
    }
}
