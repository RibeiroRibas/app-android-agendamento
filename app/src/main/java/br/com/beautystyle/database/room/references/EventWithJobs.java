package br.com.beautystyle.database.room.references;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.beautystyle.model.EventDto;
import br.com.beautystyle.model.entities.Event;
import br.com.beautystyle.model.entities.EventJobCroosRef;
import br.com.beautystyle.model.entities.Job;

public class EventWithJobs implements Serializable {
    @Embedded
    public Event event;
    @Relation(
            parentColumn = "eventId",
            entityColumn = "jobId",
            associateBy = @Junction(EventJobCroosRef.class)
    )
    public List<Job> jobList = new ArrayList<>();

    public EventWithJobs() { }

    @Ignore
    public EventWithJobs(LocalTime startTime) {
        this.event = new Event(startTime);
    }

    @Ignore
    public EventWithJobs(EventDto eventDto) {
        this.event = new Event(eventDto);
        this.jobList = eventDto.getJobList();
    }

    @Ignore
    public EventWithJobs(Event event) {
        this.event=event;
    }

    public static List<EventWithJobs> convert(List<EventDto> eventListDto) {
        return eventListDto.stream().map(EventWithJobs::new).collect(Collectors.toList());
    }

    public Event getEvent() {
        return event;
    }

    public List<Job> getJobList() {
        return jobList;
    }

}
