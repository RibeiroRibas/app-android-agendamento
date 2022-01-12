package br.com.beautystyle.util;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.beautystyle.dao.EventDao;
import br.com.beautystyle.model.Category;
import br.com.beautystyle.model.Event;
import br.com.beautystyle.model.Expenses;
import br.com.beautystyle.model.MonthsOfTheYear;
import br.com.beautystyle.model.TypeOfReport;

public class CreateListsUtil {

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

    @NonNull
    public static List<String> createCategoriesList() {
        return Stream.of(Category.values())
                .map(Category::getDescription)
                .collect(Collectors.toList());
    }

    @NonNull
    public static List<String> createTypeOfReportList() {
        return Stream.of(TypeOfReport.values())
                .map(TypeOfReport::getDescription)
                .collect(Collectors.toList());
    }

    @NonNull
    public static List<String> createMonthList() {
        return Stream.of(MonthsOfTheYear.values())
                .map(MonthsOfTheYear::getDescription)
                .collect(Collectors.toList());
    }

    public static List<Object> createMonthlyReportList(LocalDate date, List<Expenses> listExpense, List<Event> listEvent) {

        List<Object> reportList = new ArrayList<>();
        List<Expenses> collect = listExpense.stream()
                .filter(expense -> expense.getDate().getMonthValue() == date.getMonthValue()
                        && expense.getDate().getYear() == date.getYear())
                .collect(Collectors.toList());
        reportList.addAll(collect);

        reportList.addAll(listEvent.stream()
                .filter(event -> event.getEventDate().getMonthValue() == date.getMonthValue()
                        && event.getEventDate().getYear() == date.getYear())
                .collect(Collectors.toList()));

        return reportList;
    }

    public static List<String> CreateListYears(List<Event> listAll) {
        return listAll.stream()
                .map(Event::getEventDate)
                .map(LocalDate::getYear)
                .distinct()
                .sorted(Comparator.comparing(Integer::intValue))
                .map(Objects::toString)
                .collect(Collectors.toList());
    }

}

