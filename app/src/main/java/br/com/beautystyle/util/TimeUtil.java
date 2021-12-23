package br.com.beautystyle.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimeUtil {

    private static DateTimeFormatter timeFormatter;

    public static String formatLocalTime(LocalTime time) {
        timeFormatter = DateTimeFormatter.ofPattern("H:mm");
       return time.format(timeFormatter);
    }

    public static LocalTime sumTimeOfServices(List<LocalTime> listTimeService) {
        LocalTime timeOfDuration = LocalTime.of(0, 0, 0);
        for (LocalTime timeOfService : listTimeService) {
            timeOfDuration = timeOfDuration.plusHours(timeOfService.getHour());
            timeOfDuration = timeOfDuration.plusMinutes(timeOfService.getMinute());
        }
        return timeOfDuration;
    }

    public static List<Integer> createListHour() {
        List<Integer> listHour = new ArrayList<>();
        for(int i = 0 ; i<24;i++ ){
            listHour.add(i);
        }
        return listHour;
    }

    public static LocalTime formatDurationService(String duration){
        timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        return LocalTime.parse(duration,timeFormatter);
    }

    public static List<Integer> createlistMinute() {
        List<Integer> listMinute = new ArrayList<>();
        for(int i = 0; i < 60; i+=5){
            listMinute.add(i);
        }
        return listMinute;
    }
    public static String formatMinute(int min) {
        LocalTime minute = LocalTime.of(0,min);
        timeFormatter = DateTimeFormatter.ofPattern("mm");
       return minute.format(timeFormatter);
    }
}
