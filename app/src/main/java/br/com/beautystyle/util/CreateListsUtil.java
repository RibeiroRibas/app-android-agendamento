package br.com.beautystyle.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.beautystyle.database.references.EventWithClientAndJobs;

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
        for (EventWithClientAndJobs foundEvent : eventWithJobsByDate) {
            boolean matched = false;
            for (int i = 0; i < events.size(); i++) {
                if (foundEvent.getEvent().getStarTime()
                        .equals(events.get(i).getEvent().getStarTime())) {
                    events.set(i, foundEvent);
                    events.removeIf(ev -> ev.getEvent().getStarTime().isAfter(foundEvent.getEvent().getStarTime())
                            & ev.getEvent().getStarTime().isBefore(foundEvent.getEvent().getEndTime()));
                    matched = true;
                }

            }
            if (!matched) {
                events.add(foundEvent);
                events.removeIf(ev -> ev.getEvent().getStarTime().isAfter(foundEvent.getEvent().getStarTime())
                        & ev.getEvent().getStarTime().isBefore(foundEvent.getEvent().getEndTime()));
            }
        }
        events.sort(new SortByEventStartTime());
    }

    private static void setFirstAnLastTimeDefault() {
        startTime = LocalTime.of(7, 30);
        endTime = LocalTime.of(20, 0);
    }

    public static LiveData<List<String>> createMonthsList() {
        MutableLiveData<List<String>> liveData = new MutableLiveData<>();
        List<String> months = new ArrayList<>();
        for (int monthValue = 1; monthValue <= 12; monthValue++) {
            String month = Month.of(monthValue).toString().toUpperCase(Locale.ROOT);
            months.add(month);
        }
        liveData.setValue(months);
        return liveData;
    }
}

