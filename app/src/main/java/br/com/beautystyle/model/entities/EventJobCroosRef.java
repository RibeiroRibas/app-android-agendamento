package br.com.beautystyle.model.entities;

import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;


@Entity(primaryKeys = "eventId",
        foreignKeys = {
        @ForeignKey(onDelete = CASCADE, entity = Event.class,
                parentColumns = "eventId", childColumns = "eventId"),
        @ForeignKey(onDelete = CASCADE,entity = Job.class,
                parentColumns = "jobId",childColumns = "jobId")})
public class EventJobCroosRef {

    @ColumnInfo(index = true)
    public long eventId;
    @ColumnInfo(index = true)
    public long jobId;

    public EventJobCroosRef(long eventId, long jobId) {
        this.eventId = eventId;
        this.jobId = jobId;
    }
}
