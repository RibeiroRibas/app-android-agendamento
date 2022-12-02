package br.com.beautystyle.util;

import java.util.Comparator;

import br.com.beautystyle.model.entity.Customer;

public class SortByClientName implements Comparator<Customer> {

    @Override
    public int compare(Customer o1, Customer o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
    }
}
