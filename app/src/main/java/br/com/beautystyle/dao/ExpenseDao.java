package br.com.beautystyle.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.beautystyle.model.Expenses;

public class ExpenseDao {

    private final static List<Expenses> spendingList = new ArrayList<>();
    private static int countId = 1;

    public void save(Expenses spending){
        spending.setId(countId);
        spendingList.add(spending);
        countId++;
    }

    public List<Expenses> listAll(){
        return new ArrayList<>(spendingList);
    }

    public void remove (Expenses selectedSpend){
        Expenses spending = findSpendById(selectedSpend);
        if(spending!=null){
            spendingList.remove(spending);
        }
    }

    private Expenses findSpendById(Expenses selectedSpend) {
        for (Expenses spend : spendingList) {
            if(spend.getId()== selectedSpend.getId()){
                return spend;
            }
        }
        return null;
    }

    public List<Expenses> listByMonth(int monthValue) {
        return spendingList.stream()
                .filter(expense -> expense.getDate()
                        .getMonthValue()==monthValue)
                .collect(Collectors.toList());
    }
}
