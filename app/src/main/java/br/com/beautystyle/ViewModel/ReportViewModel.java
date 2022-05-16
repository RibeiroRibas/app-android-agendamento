package br.com.beautystyle.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import br.com.beautystyle.model.entities.Event;
import br.com.beautystyle.model.entities.Expense;
import br.com.beautystyle.model.Report;

public class ReportViewModel extends ViewModel {

    private MutableLiveData<List<Report>> mReportList;
    private MutableLiveData<List<Event>> eventList;
    private MutableLiveData<List<Expense>> expenseList;

    public void addEventList(List<Event> eventList) {
        this.eventList.setValue(eventList);
    }

    public void addExpenseList(List<Expense> expenseList) { this.expenseList.setValue(expenseList); }

    public void addReportList(List<Report> reportList) {
        mReportList.setValue(reportList);
    }

    public LiveData<List<Report>> getReportList() {
        if(mReportList == null){
            mReportList = new MutableLiveData<>();
        }
        return mReportList;
    }

    public LiveData<List<Event>> getEventList() {
        if (eventList == null) {
            eventList = new MutableLiveData<>();
        }
        return eventList;
    }

    public LiveData<List<Expense>> getExpenseList() {
        if (expenseList == null) {
            expenseList = new MutableLiveData<>();

        }
        return expenseList;
    }

}
