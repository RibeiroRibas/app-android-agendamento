package br.com.beautystyle.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import br.com.beautystyle.data.repository.ExpenseRepository;
import br.com.beautystyle.model.Expense;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public class ExpenseViewModel extends AndroidViewModel {

    private final ExpenseRepository repository;
    private final MutableLiveData<List<Expense>> mExpenseList = new MutableLiveData<>();

    public void add(List<Expense> expenseList) {
        mExpenseList.setValue(expenseList);
    }

    public LiveData<List<Expense>> getExpenseList() {
        return mExpenseList;
    }

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        repository = new ExpenseRepository(application);
    }

    public Completable update(Expense expense) {
        return repository.update(expense);
    }

    public Single<Long> insert(Expense expense) {
        return repository.insert(expense);
    }

    public Completable delete(Expense expense) {
        return repository.delete(expense);
    }

    public Observable<List<Expense>> getAll() {
        return repository.getAll();
    }
}
