package br.com.beautystyle.util;

import java.util.Comparator;

import br.com.beautystyle.model.entity.Job;

public class SortByJobName implements Comparator<Job> {
    @Override
    public int compare(Job o1, Job o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
    }
}
