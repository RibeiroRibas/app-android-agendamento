package br.com.beautystyle.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import br.com.beautystyle.model.Expense;

public class ExpenseDao {

    private final static List<Expense> spendingList = new ArrayList<>();
    private static int countId = 1;

    public void save(Expense spending){
        spending.setId(countId);
        spendingList.add(spending);
        countId++;
    }

    public List<Expense> listAll(){
        return new ArrayList<>(spendingList);
    }

    public void remove (Expense selectedSpend){
        Expense spending = findSpendById(selectedSpend);
        if(spending!=null){
            spendingList.remove(spending);
        }
    }

    private Expense findSpendById(Expense selectedSpend) {
        for (Expense spend : spendingList) {
            if(spend.getId()== selectedSpend.getId()){
                return spend;
            }
        }
        return null;
    }

    public List<Expense> listByDate(int monthValue, int year) {
        return spendingList.stream()
                .filter(expense -> expense.getDate()
                        .getMonthValue()==monthValue
                &&expense.getDate().getYear()==year)
                .collect(Collectors.toList());
    }

    public List<Expense> listByDate(int monthValue) {
        return spendingList.stream()
                .filter(expense -> expense.getDate()
                        .getMonthValue()==monthValue)
                .collect(Collectors.toList());
    }
    public void saveWithId(Expense expense) {
        spendingList.add(expense);
    }

    public void edit(Expense editedData) {
        Expense expense = findExpenseById(editedData);
        if (expense != null) {
            int expenseAtPosition = spendingList.indexOf(expense);
            spendingList.set(expenseAtPosition, editedData);
        }
    }

    private Expense findExpenseById(Expense findExpense) {
        for (Expense expense : spendingList) {
            if (findExpense.getId() == expense.getId()) {
                return expense;
            }
        }
        return null;
    }
}
