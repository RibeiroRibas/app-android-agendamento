package com.example.beautystyle.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalendarUtil {

    public static LocalDate selectedDate;
    private static String formatDate;
    private static DateTimeFormatter df;

    public static List<LocalDate> fiveDays() {
        List<LocalDate> listDays = new ArrayList<>();
        listDays.add(selectedDate);
        for (int i = 1; i <= 2; i++) {
            listDays.add(selectedDate.minusDays(i));
            listDays.add(selectedDate.plusDays(i));
        }
        Collections.sort(listDays);
        return listDays;
    }

    public static String formatDay(LocalDate date) {
        df = DateTimeFormatter.ofPattern("dd");
        formatDate = df.format(date);
        return formatDate;
    }

    public static String formatMonthYear(LocalDate date) {
        df = DateTimeFormatter.ofPattern("MMMM / yyyy");
        formatDate = df.format(date);
        return formatDate;
    }

    public static String formatDate(LocalDate date) {
        df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        formatDate = df.format(date);
        return formatDate;
    }

    public static String formatDayWeek(LocalDate date) {
        df = DateTimeFormatter.ofPattern("E");
        formatDate = df.format(date);
        return formatDate;
    }
}
