package br.com.beautystyle.data.db.references;

import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

import br.com.beautystyle.domain.model.Event;
import br.com.beautystyle.domain.model.Services;

public class EventWithServices {
    @Embedded public Event event;
    @Relation(
            parentColumn = "eventId",
            entityColumn = "serviceId",
            associateBy = @Junction(EventServiceCroosRef.class)
    )
    public List<Services> serviceList;

    public Event getEvent() {
        return event;
    }
    
    public List<Services> getServiceList() {
        return serviceList;
    }

}
