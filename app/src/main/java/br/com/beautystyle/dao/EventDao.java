package br.com.beautystyle.dao;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import br.com.beautystyle.model.Event;

public class EventDao {
    private final static List<Event> listEvents = new ArrayList<>();
    private static int countId = 1;
    public static LocalTime reduzedEndTime;

    public void save(Event event) {
        event.setId(countId);
        listEvents.add(event);
        countId++;
    }

    public List<Event> listAll() {
        return new ArrayList<>(listEvents);
    }


    public void edit(Event newEvent) {
        Event event = finEventById(newEvent);
        if (event != null) {
            int eventAtPosition = listEvents.indexOf(event);
            listEvents.set(eventAtPosition, newEvent);
        }
    }

    private Event finEventById(Event newEvent) {
        for (Event event : listEvents) {
            if (event.getId() == newEvent.getId()) {
                return event;
            }
        }
        return null;
    }

    public boolean checkStartTime(Event newEvent) {
        return listEvents.stream()
                .filter(ev -> ev.getEventDate()
                        .equals(newEvent.getEventDate()) & ev.getId() != newEvent.getId())
                .anyMatch(event -> !newEvent.getStarTime().isBefore(event.getStarTime()) &&
                        newEvent.getStarTime().isBefore(event.getEndTime()));
    }

    public boolean checkEndTime(Event newEvent) {
        List<Event> listEvent = listEvents.stream()
                .filter(ev -> ev.getEventDate()
                        .equals(newEvent.getEventDate()))
                .sorted(Comparator.comparing(Event::getStarTime))
                .collect(Collectors.toList());
        boolean checkEndTime = false;
        for (Event event : listEvent) {
            if (event.getStarTime().isAfter(newEvent.getStarTime())
                    && newEvent.getEndTime().isAfter(event.getStarTime())) {
                checkEndTime = true;
                reduzedEndTime = event.getStarTime();
                break;
            }
        }
        return checkEndTime;
    }

    public void remove(Event event) {
        Event findedEvent = finEventById(event);
        if (findedEvent != null) {
            listEvents.remove(findedEvent);
        }
    }

    public List<Event> findEventByMonth(int position){
        List<Event> eventsByMonth = new ArrayList<>();
        for (Event event: listEvents) {
            if(event.getEventDate().getMonthValue()==position)
                eventsByMonth.add(event);
        }
        return eventsByMonth;
    }
}
