package br.com.beautystyle.util;

import java.util.Comparator;

import br.com.beautystyle.database.references.EventWithClientAndJobs;


public class SortByEventStartTime implements Comparator<EventWithClientAndJobs> {

    @Override
    public int compare(EventWithClientAndJobs o1, EventWithClientAndJobs o2) {
        return o1.getEvent().getStartTime().compareTo(o2.getEvent().getStartTime());
    }
}
