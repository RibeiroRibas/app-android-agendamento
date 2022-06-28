package br.com.beautystyle.util;

import java.util.Comparator;

import br.com.beautystyle.model.entity.Client;

public class SortByClientName implements Comparator<Client> {

    @Override
    public int compare(Client o1, Client o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
    }
}
