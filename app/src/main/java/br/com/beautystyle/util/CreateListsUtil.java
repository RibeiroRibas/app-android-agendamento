package br.com.beautystyle.util;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.time.LocalTime;
import java.time.Month;
import java.time.format.TextStyle;
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

    private static void setFirstAnLastTimeDefault() {
        startTime = LocalTime.of(7, 30);
        endTime = LocalTime.of(20, 0);
    }

    public static LiveData<List<String>> createMonthsList() {
        MutableLiveData<List<String>> liveData = new MutableLiveData<>();
        List<String> months = getMonths();
        liveData.setValue(months);
        return liveData;
    }

    public static List<String> getMonths(){
        List<String> months = new ArrayList<>();
        for (int monthValue = 1; monthValue <= 12; monthValue++) {
            String month =
                    Month.of(monthValue).getDisplayName(TextStyle.FULL,new Locale("pt","br"));
            months.add(month);
        }
        return  months;
    }
}

