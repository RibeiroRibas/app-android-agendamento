package br.com.beautystyle.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CalendarUtil {

    public static LocalDate selectedDate;
    private static String formatDate;
    private static DateTimeFormatter df;

    public static List<LocalDate> createDaysList() {
        List<LocalDate> listDays = new ArrayList<>();
        listDays.add(selectedDate);
        for (int i = 1; i <= 365; i++) {
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

    public static String formatMonth(LocalDate date) {
        df = DateTimeFormatter.ofPattern("MMMM");
        formatDate = df.format(date);
        formatDate = formatDate.substring(0, 1).toUpperCase().concat(formatDate.substring(1));
        return formatDate;
    }

    public static String formatYear(LocalDate date) {
        df = DateTimeFormatter.ofPattern("yyyy");
        formatDate = df.format(date);
        return formatDate;
    }

    public static String formatDate(LocalDate date) {
        df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        formatDate = df.format(date);
        return formatDate;
    }

    public static String formatDateLong(LocalDate date) {
        df = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        formatDate = df.format(date);
        return formatDate;
    }

    public static String formatDayWeek(LocalDate date) {
        df = DateTimeFormatter.ofPattern("E");
        formatDate = df.format(date);
        return formatDate;
    }
}

