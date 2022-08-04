package br.com.beautystyle.model;

import java.time.LocalTime;
import java.util.List;

public class ScheduleConfig {

    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<LocalTime> restTime;
    private List<String> workDays;
}
