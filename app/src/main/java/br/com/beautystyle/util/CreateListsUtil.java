package br.com.beautystyle.util;

import static br.com.beautystyle.util.ConstantsUtil.MMMM;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.database.room.references.EventWithClientAndJobs;

public class CreateListsUtil {

    public static List<EventWithClientAndJobs> events;
    private static LocalTime startTime;
    private static LocalTime endTime;

    public static void createEventList(List<EventWithClientAndJobs> eventWithJobs) {
        createEmptyEventList();
        if (eventWithJobs.size() >= 1) {
            updateEventList(eventWithJobs);
        }
    }

    private static void createEmptyEventList() {
        setFirstAnLastTimeDefault();
        events = new ArrayList<>();
        do {
            EventWithClientAndJobs event = new EventWithClientAndJobs(startTime);
            events.add(event);
            startTime = startTime.plusMinutes(30);
        } while (!startTime.equals(endTime));
    }

    private static void updateEventList(List<EventWithClientAndJobs> eventWithJobsByDate) {
        for (EventWithClientAndJobs findedEvent : eventWithJobsByDate) {
            boolean matched = false;
            for (int i = 0; i < events.size(); i++) {
                if (findedEvent.getEvent().getStarTime()
                        .equals(events.get(i).getEvent().getStarTime())) {
                    events.set(i, findedEvent);
                    events.removeIf(ev -> ev.getEvent().getStarTime().isAfter(findedEvent.getEvent().getStarTime())
                            & ev.getEvent().getStarTime().isBefore(findedEvent.getEvent().getEndTime()));
                    matched = true;
                }

            }
            if (!matched) {
                events.add(findedEvent);
                events.removeIf(ev -> ev.getEvent().getStarTime().isAfter(findedEvent.getEvent().getStarTime())
                        & ev.getEvent().getStarTime().isBefore(findedEvent.getEvent().getEndTime()));
            }
        }
        events.sort(new SortByEventStartTime());
    }

    private static void setFirstAnLastTimeDefault() {
        startTime = LocalTime.of(7, 30);
        endTime = LocalTime.of(20, 0);
    }

    public static List<String> createMonthsList() {
        List<String> months = new ArrayList<>();
        for (int month = 1; month < 12; month++) {
            months.add(CalendarUtil.formatLocalDate(
                    LocalDate.of(2022,month,1),MMMM));
        }
        return months;
    }
}

