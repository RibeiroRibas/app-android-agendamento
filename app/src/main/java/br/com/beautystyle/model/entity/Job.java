package br.com.beautystyle.model.entity;

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
    private Long jobId =0L;
    private String name;
    private BigDecimal valueOfJob;
    private LocalTime durationTime;
    private Long companyId;
    private Long apiId;

    @Ignore
    public Job(String name, BigDecimal valueOfJob, LocalTime durationTime) {
        this.name = name;
        this.valueOfJob = valueOfJob;
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
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

    public boolean checkId() {
        return jobId > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Job job = (Job) o;
        return Objects.equals(jobId, job.jobId) && Objects.equals(name, job.name) && Objects.equals(valueOfJob, job.valueOfJob) && Objects.equals(durationTime, job.durationTime) && Objects.equals(companyId, job.companyId) && Objects.equals(apiId, job.apiId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(jobId, name, valueOfJob, durationTime, companyId, apiId);
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
