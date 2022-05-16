package br.com.beautystyle.util;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.database.room.references.EventWithJobs;
import br.com.beautystyle.model.entities.Event;
import br.com.beautystyle.model.entities.EventJobCroosRef;
import br.com.beautystyle.model.entities.Job;

public class CreateListsUtil   {

    public static List<Event> listEvent;
    public static List<EventWithJobs> listEventWithJobs;
    private static LocalTime startTime;
    private static LocalTime endTime;

    public static void createEventList(List<EventWithJobs> eventWithJobs) {
        createEmptyEventList();
        if (eventWithJobs.size() >= 1) {
            updateEventList(eventWithJobs);
        }
    }

    private static void createEmptyEventList() {
        setFirstAnLastTimeDefault();
        listEventWithJobs = new ArrayList<>();
        do {
            EventWithJobs event = new EventWithJobs(startTime);
            listEventWithJobs.add(event);
            startTime = startTime.plusMinutes(30);
        } while (!startTime.equals(endTime));
    }

    private static void updateEventList(List<EventWithJobs> eventWithJobsByDate) {
        for (EventWithJobs findedEvent : eventWithJobsByDate) {
            boolean matched = false;
            for (int i = 0; i < listEventWithJobs.size(); i++) {
                if (findedEvent.getEvent().getStarTime()
                        .equals(listEventWithJobs.get(i).getEvent().getStarTime())) {
                    listEventWithJobs.set(i, findedEvent);
                    listEventWithJobs.removeIf(ev -> ev.getEvent().getStarTime().isAfter(findedEvent.getEvent().getStarTime())
                            & ev.getEvent().getStarTime().isBefore(findedEvent.getEvent().getEndTime()));
                    matched = true;
                }

            }
            if (!matched) {
                listEventWithJobs.add(findedEvent);
                listEventWithJobs.removeIf(ev -> ev.getEvent().getStarTime().isAfter(findedEvent.getEvent().getStarTime())
                        & ev.getEvent().getStarTime().isBefore(findedEvent.getEvent().getEndTime()));
            }
        }
            listEventWithJobs.sort(new SortByEventStartTime());
    }

    private static void setFirstAnLastTimeDefault() {
        startTime = LocalTime.of(7, 30);
        endTime = LocalTime.of(20, 0);
    }


    public static List<EventJobCroosRef> createNewJobList(Long eventId, List<Job> jobList) {
        List<EventJobCroosRef> eventList = new ArrayList<>();
        for (Job job : jobList) {
            EventJobCroosRef eventJobCroosRef = new EventJobCroosRef(eventId,job.getJobId());
            eventList.add(eventJobCroosRef);
        }
        return eventList;
    }
}

