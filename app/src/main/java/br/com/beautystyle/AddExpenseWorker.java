package br.com.beautystyle;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import br.com.beautystyle.ViewModel.ExpenseViewModel;
import br.com.beautystyle.domain.model.Expense;

public class AddExpenseWorker extends Worker {

    private final Application context;

    public AddExpenseWorker(@NonNull Application context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        if (LocalDate.now().getDayOfMonth() == 1) {
            ExpenseViewModel expenseViewModel = new ExpenseViewModel(context);
            expenseViewModel.getAll().doOnSuccess(expenses -> {
                List<Expense> expenselist =  expenses.stream()
                        .filter(ex -> ex.getRepeatOrNot().equals(Expense.RepeatOrNot.REPEAT))
                        .collect(Collectors.toList());
                for (Expense expense : expenselist) {
                    Expense newExpense = new Expense(expense.getDescription(), expense.getPrice(), expense.getDate(), expense.getCategory(), expense.getRepeatOrNot());
                    newExpense.setDate(LocalDate.now());
                    expenseViewModel.insert(newExpense).subscribe();
                    expense.setRepeatOrNot(Expense.RepeatOrNot.NREPEAT);
                    expenseViewModel.update(expense).subscribe();
                }
            }).subscribe();
        }
        return Result.success();
    }
}
