package br.com.beautystyle.database.converters;

import androidx.room.TypeConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeConverter {

    @TypeConverter
    public static LocalTime fromString(String time) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        return LocalTime.parse(time, timeFormatter);
    }

    @TypeConverter
    public static String fromLocalTime(LocalTime time) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        return time == null ? null : time.format(timeFormatter);
    }
}
