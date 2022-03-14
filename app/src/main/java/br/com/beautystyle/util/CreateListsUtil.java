package br.com.beautystyle.util;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import br.com.beautystyle.data.database.references.EventServiceCroosRef;
import br.com.beautystyle.model.Event;
import br.com.beautystyle.model.Services;

public class CreateListsUtil   {

    public static List<Event> listEvent;
    private static LocalTime startTime;
    private static LocalTime endTime;
    public static boolean reportListRef;

    public static void createListEventTest(List<Event> eventList) {
        creatListEmptyEvent();
        if (eventList.size() >= 1) {
            createListEvent(eventList);
        }
    }

    public static void creatListEmptyEvent() {
        setFirstAnLastTimeDefault();
        listEvent = new ArrayList<>();
        do {
            Event event = new Event(startTime);
            listEvent.add(event);
            startTime = startTime.plusMinutes(30);
        } while (!startTime.equals(endTime));
    }

    private static void setFirstAnLastTimeDefault() {
        startTime = LocalTime.of(7, 30);
        endTime = LocalTime.of(20, 0);
    }

    private static void createListEvent(List<Event> eventList) {
        for (Event findedEvent : eventList) {
            boolean matched = false;
            for (int i = 0; i < listEvent.size(); i++) {
                if (findedEvent.getStarTime().equals(listEvent.get(i).getStarTime())) {
                    listEvent.set(i, findedEvent);
                    listEvent.removeIf(ev -> ev.getStarTime().isAfter(findedEvent.getStarTime())
                            & ev.getStarTime().isBefore(findedEvent.getEndTime()));
                    matched = true;
                }

            }
            if (!matched) {
                listEvent.add(findedEvent);
                listEvent.removeIf(ev -> ev.getStarTime().isAfter(findedEvent.getStarTime())
                        & ev.getStarTime().isBefore(findedEvent.getEndTime()));
            }
        }
        listEvent.sort(Comparator.comparing(Event::getStarTime));
    }

    public static List<EventServiceCroosRef> createNewListServices(Long id, List<Services> servicesList) {
            List<EventServiceCroosRef> eventServiceCroosNewList = new ArrayList<>();
            for (Services service : servicesList) {
                eventServiceCroosNewList.add(new EventServiceCroosRef(id, service.getServiceId()));
            }
            return eventServiceCroosNewList;
    }
}

