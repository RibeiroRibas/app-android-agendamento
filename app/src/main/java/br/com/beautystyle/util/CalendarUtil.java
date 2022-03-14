package br.com.beautystyle.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class CalendarUtil {

    public static LocalDate selectedDate;
    public static int monthValue;
    public static int year;
    private static String formatDate;
    private static DateTimeFormatter df;

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

    public static LocalDate fromStringToLocalDate(String date){
        df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDate.parse(date,df);
    }
}

