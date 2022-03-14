package br.com.beautystyle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.beautystyle.model.Event;
import br.com.beautystyle.model.Expense;
import br.com.beautystyle.model.Report;

public class ReportViewModel extends ViewModel {

    private final MutableLiveData<List<Report>> mReportList = new MutableLiveData<>();
    private MutableLiveData<List<Event>> eventList= new MutableLiveData<>();
    private MutableLiveData<List<Expense>> expenseList = new MutableLiveData<>();

    public LiveData<List<Report>> getReportList() {
        return mReportList;
    }

    public void addReportList(List<Report> reportList) {
        mReportList.setValue(reportList);
    }

    public LiveData<List<Event>> getEventList() {
        if (eventList == null) {
            eventList = new MutableLiveData<>();
        }
        return eventList;
    }

    public void addEventList(List<Event> eventList) {
        this.eventList.setValue(eventList);
    }

    public LiveData<List<Expense>> getExpenseList() {
        if (expenseList == null) {
            expenseList = new MutableLiveData<>();

        }
        return expenseList;
    }

    public void addExpenseList(List<Expense> expenseList) {
        this.expenseList.setValue(expenseList);
    }

}
