package br.com.beautystyle.util;

import java.util.Comparator;

import br.com.beautystyle.database.room.references.EventWithJobs;


public class SortByEventStartTime implements Comparator<EventWithJobs> {

    @Override
    public int compare(EventWithJobs o1, EventWithJobs o2) {
        return o1.getEvent().getStarTime().compareTo(o2.getEvent().getStarTime());
    }
}
