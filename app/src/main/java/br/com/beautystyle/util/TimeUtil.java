package br.com.beautystyle.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TimeUtil {

    private static DateTimeFormatter timeFormatter;

    public static String formatLocalTime(LocalTime time) {
        timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        return time.format(timeFormatter);
    }

    public static LocalTime sumTimeOfJobs(List<LocalTime> listTimeService) {
        LocalTime timeOfDuration = LocalTime.of(0, 0, 0);
        for (LocalTime timeOfService : listTimeService) {
            timeOfDuration = timeOfDuration.plusHours(timeOfService.getHour());
            timeOfDuration = timeOfDuration.plusMinutes(timeOfService.getMinute());
        }
        return timeOfDuration;
    }

    public static LocalTime formatDurationService(String duration) {
        timeFormatter = DateTimeFormatter.ofPattern("H:mm");
        return LocalTime.parse(duration, timeFormatter);
    }

}
