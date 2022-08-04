package br.com.beautystyle.util;

import static br.com.beautystyle.util.ConstantsUtil.DD_MM_YYYY;
import static br.com.beautystyle.util.ConstantsUtil.MMMM;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class CalendarUtil {

    public static LocalDate selectedDate = LocalDate.now();

    public static String formatMonth(LocalDate date) {
        String formatDate = formatLocalDate(date, MMMM);
        return formatDate.substring(0, 1).toUpperCase().concat(formatDate.substring(1));
    }

    public static String formatLocalDate(LocalDate date, String format) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return df.format(date);
    }

    public static String formatDateLong() {
        DateTimeFormatter df = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        return df.format(selectedDate);
    }

    public static LocalDate fromStringToLocalDate(String date){
        DateTimeFormatter df = DateTimeFormatter.ofPattern(DD_MM_YYYY);
        return LocalDate.parse(date,df);
    }
}

