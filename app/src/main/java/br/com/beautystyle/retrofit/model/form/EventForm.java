package br.com.beautystyle.retrofit.model.form;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.Job;

class JobForm {
    private Long apiId;

    public JobForm() {
    }

    public JobForm(Job job) {
        this.apiId = job.getApiId();
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }
}

public class EventForm {

    private Event event;
    private Long customerId;
    private Set<JobForm> jobs;

    public EventForm() {
    }

    public EventForm(Event event, Long customerId, List<Job> jobs) {
        this.event = event;
        this.customerId = customerId;
        this.jobs = jobs.stream().map(JobForm::new).collect(Collectors.toSet());
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Set<JobForm> getJobs() {
        return jobs;
    }

    public void setJobs(Set<JobForm> jobs) {
        this.jobs = jobs;
    }
}
