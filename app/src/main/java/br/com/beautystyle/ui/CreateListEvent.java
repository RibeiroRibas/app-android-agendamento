package com.example.beautystyle.ui;

import androidx.annotation.NonNull;

import com.example.beautystyle.dao.EventDao;
import com.example.beautystyle.model.Event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CreateListEvent {
    private static List<Event> ListEventBySelectedDate = new ArrayList<>();
    public static List<Event> listEvent;
    private static LocalTime startTime;
    private static LocalTime endTime;

    public static void createListEventTest(LocalDate eventDate) {
        ListEventBySelectedDate = findEventByDate(eventDate);
        creatListEmptyEvent();
        if (ListEventBySelectedDate.size() >= 1) {
            createListEvent();
        }
    }

    @NonNull
    private static List<Event> findEventByDate(LocalDate eventDate) {
        EventDao dao = new EventDao();
        return dao.listAll().stream()
                .filter(ev -> ev.getEventDate()
                        .equals(eventDate))
                .sorted(Comparator.comparing(Event::getStarTime))
                .collect(Collectors.toList());
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

    private static void createListEvent() {
        for (Event findedEvent : ListEventBySelectedDate) {
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
}

