package br.com.beautystyle.model.entity;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;

import java.util.Objects;


@Entity(primaryKeys = {"eventId", "jobId"},
        foreignKeys = {
                @ForeignKey(onDelete = CASCADE, entity = Event.class,
                        parentColumns = "eventId", childColumns = "eventId")})
public class EventJobCroosRef {

    @ColumnInfo(index = true)
    public long eventId;
    @ColumnInfo(index = true)
    public long jobId;

    @Ignore
    public EventJobCroosRef(long eventId, long jobId) {
        this.eventId = eventId;
        this.jobId = jobId;
    }

    public EventJobCroosRef() {
    }

    public long getEventId() {
        return eventId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventJobCroosRef that = (EventJobCroosRef) o;
        return eventId == that.eventId && jobId == that.jobId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, jobId);
    }
}
