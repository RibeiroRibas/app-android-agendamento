package br.com.beautystyle.data.db.references;

import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"eventId","serviceId"})
public class EventServiceCroosRef {

    @ColumnInfo(index = true)
    public long eventId;
    @ColumnInfo(index = true)
    public long serviceId;

    public EventServiceCroosRef(long eventId, long serviceId) {
        this.eventId = eventId;
        this.serviceId = serviceId;
    }
}
