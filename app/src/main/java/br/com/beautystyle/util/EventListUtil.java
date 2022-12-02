package br.com.beautystyle.util;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.BlockTime;
import br.com.beautystyle.model.entity.OpeningHours;
import br.com.beautystyle.retrofit.model.dto.EventWithClientAndJobsDto;

public class EventListUtil {

    private final EventWithClientAndJobsDto eventsDto;
    private final List<EventWithClientAndJobs> events;
    private final List<OpeningHours> openingHours;

    public EventListUtil(EventWithClientAndJobsDto eventsDto, List<OpeningHours> openingHours) {
        this.eventsDto = eventsDto;
        this.openingHours = openingHours;
        events = new ArrayList<>();
    }

    public EventWithClientAndJobsDto createEventList() {
        List<OpeningHours> openingHoursByDay = openingHours.stream().filter(openingHour ->
                        openingHour.getDayOfWeek() == CalendarUtil.selectedDate.getDayOfWeek().getValue()
                ).sorted()
                .collect(Collectors.toList());
        if (!openingHoursByDay.isEmpty()) {
            for (OpeningHours openingHour : openingHoursByDay) {
                LocalTime startTime = openingHour.getStartTime();
                do {
                    EventWithClientAndJobs event = new EventWithClientAndJobs(startTime);
                    events.add(event);
                    startTime = startTime.plusMinutes(30);
                    if (startTime.isAfter(openingHour.getEndTime())) {
                        startTime = openingHour.getEndTime();
                        event = new EventWithClientAndJobs(startTime);
                        events.add(event);
                    }
                } while (!startTime.equals(openingHour.getEndTime()));
            }
            if (!eventsDto.getBlockTimes().isEmpty())
                for (BlockTime blockTime : eventsDto.getBlockTimes()) {
                    for (int i = 0; i < events.size(); i++) {
                        if (events.get(i).getEvent().getStartTime().equals(blockTime.getStartTime())) {
                            events.removeIf(ev -> ev.getEvent().getStartTime().isAfter(blockTime.getStartTime())
                                    & ev.getEvent().getStartTime().isBefore(blockTime.getEndTime()));
                        }
                    }
                }

            if (!eventsDto.getEvents().isEmpty())
                updateEventList(eventsDto.getEvents());


        }
        eventsDto.setEvents(events);
        return eventsDto;
    }

    private void updateEventList(List<EventWithClientAndJobs> eventWithJobsByDate) {
        for (EventWithClientAndJobs foundEvent : eventWithJobsByDate) {
            boolean matched = false;
            for (int i = 0; i < events.size(); i++) {
                if (foundEvent.getEvent().getStartTime()
                        .equals(events.get(i).getEvent().getStartTime())) {
                    events.set(i, foundEvent);
                    events.removeIf(ev -> ev.getEvent().getStartTime().isAfter(foundEvent.getEvent().getStartTime())
                            & ev.getEvent().getStartTime().isBefore(foundEvent.getEvent().getEndTime()));
                    matched = true;
                }

            }
            if (!matched) {
                events.add(foundEvent);
                events.removeIf(ev -> ev.getEvent().getStartTime().isAfter(foundEvent.getEvent().getStartTime())
                        & ev.getEvent().getStartTime().isBefore(foundEvent.getEvent().getEndTime()));
            }
        }
        events.sort(new SortByEventStartTime());
    }

}
