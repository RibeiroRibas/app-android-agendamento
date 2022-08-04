package br.com.beautystyle.util;

import java.util.Comparator;

import br.com.beautystyle.model.entity.Costumer;

public class SortByClientName implements Comparator<Costumer> {

    @Override
    public int compare(Costumer o1, Costumer o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
    }
}
