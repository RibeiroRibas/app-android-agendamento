package br.com.beautystyle.model.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalTime;

@Entity
public class Job implements Serializable {

    @PrimaryKey
    private Long jobId;
    private String name;
    private BigDecimal valueOfJob;
    private LocalTime durationTime;

    @Ignore
    public Job(Long jobId, String name, BigDecimal valueOfJob, LocalTime durationTime) {
        this.jobId = jobId;
        this.name = name;
        this.valueOfJob = valueOfJob;
        this.durationTime = durationTime;
    }

    public Job() {

    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getValueOfJob() {
        return valueOfJob;
    }

    public LocalTime getDurationTime() {
        return durationTime;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValueOfJob(BigDecimal valueOfJob) {
        this.valueOfJob = valueOfJob;
    }

    public void setDurationTime(LocalTime durationTime) {
        this.durationTime = durationTime;
    }

}
