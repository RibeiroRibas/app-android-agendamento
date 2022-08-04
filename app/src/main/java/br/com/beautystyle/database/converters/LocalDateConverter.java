package br.com.beautystyle.database.converters;

import androidx.room.TypeConverter;

import java.time.LocalDate;

public class LocalDateConverter {

    @TypeConverter
    public static LocalDate fromLong(Long date) {
        return date == null ? null : LocalDate.ofEpochDay(date);
    }

    @TypeConverter
    public static Long fromLocalDate(LocalDate date) {
        return date == null ? null : date.toEpochDay();
    }
}
