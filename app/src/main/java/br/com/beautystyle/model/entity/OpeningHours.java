package br.com.beautystyle.model.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalTime;

@Entity
public class OpeningHours implements Comparable<OpeningHours> {

    @PrimaryKey(autoGenerate = true)
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private int dayOfWeek;
    private Long tenant;
    private Long apiId;

    public OpeningHours() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Long getTenant() {
        return tenant;
    }

    public void setTenant(Long tenant) {
        this.tenant = tenant;
    }

    public Long getApiId() {
        return apiId;
    }

    public void setApiId(Long apiId) {
        this.apiId = apiId;
    }

    public boolean isApiIdEquals(OpeningHours fromApi) {
        return this.apiId.equals(fromApi.getApiId());
    }

    @Override
    public int compareTo(OpeningHours o) {
        if (startTime.equals(o.getStartTime())) return 0;
        if (startTime.isAfter(o.getStartTime())) return 1;
        return -1;
    }
}
