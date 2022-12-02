package br.com.beautystyle.retrofit.model.dto;

import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.BlockTime;

public class EventWithClientAndJobsDto {

    private List<EventWithClientAndJobs> events;
    private List<BlockTime> blockTimes;

    public EventWithClientAndJobsDto() {
        events = new ArrayList<>();
        blockTimes = new ArrayList<>();
    }

    public EventWithClientAndJobsDto(List<EventWithClientAndJobs> events, List<BlockTime> blockTimes) {
        this.events = events;
        this.blockTimes = blockTimes;
    }

    public List<EventWithClientAndJobs> getEvents() {
        return events;
    }

    public void setEvents(List<EventWithClientAndJobs> events) {
        this.events = events;
    }

    public List<BlockTime> getBlockTimes() {
        return blockTimes;
    }

    public void setBlockTimes(List<BlockTime> blockTimes) {
        this.blockTimes = blockTimes;
    }
}
