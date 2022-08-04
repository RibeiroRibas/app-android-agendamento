package br.com.beautystyle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.beautystyle.model.Report;
import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.repository.ExpenseRepository;
import br.com.beautystyle.repository.Resource;
import br.com.beautystyle.util.CreateListsUtil;

public class ExpenseViewModel extends ViewModel {

    private final ExpenseRepository repository;

    public ExpenseViewModel(ExpenseRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<Expense>>> getByPeriodFromRoom() {
        return repository.getByPeriodFromRoom();
    }

    public LiveData<Resource<List<Expense>>> getByPeriodFromApi() {
        return repository.getByPeriodFromApi();
    }

    public LiveData<Resource<Expense>> insert(Expense expense) {
        return repository.insert(expense);
    }

    public LiveData<Resource<Expense>> update(Expense expense) {
        return repository.update(expense);
    }

    public LiveData<Resource<Void>> delete(Expense expense) {
        return repository.delete(expense);
    }

    public LiveData<Resource<List<String>>> getYearsListFromApi() {
        return repository.getYearsListFromApi();
    }

    public LiveData<List<String>> getMonthsOfTheYearList() {
        return CreateListsUtil.createMonthsList();
    }

    public LiveData<Resource<List<Report>>> getReportByPeriod() {
        return repository.getReportByPeriod();
    }
}