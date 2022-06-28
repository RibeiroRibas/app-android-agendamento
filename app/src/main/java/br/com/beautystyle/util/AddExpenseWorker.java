package br.com.beautystyle.util;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import io.reactivex.rxjava3.disposables.Disposable;

public class AddExpenseWorker extends Worker {

    Context context;
    WorkerParameters workerParams;
    private Disposable subscribe;

    public AddExpenseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.workerParams = workerParams;
    }

    @NonNull
    @Override
    public Result doWork() {
        return null;
    }

//    @NonNull
//    @Override
//    public Result doWork() {
//            ExpenseRepository repository = new ExpenseRepository(context);
//            subscribe = repository.getAll().doOnNext(expenses -> {
//                List<Expense> expenselist = expenses.stream()
//                        .filter(ex -> ex.getRepeatOrNot().equals(Expense.RepeatOrNot.REPEAT))
//                        .collect(Collectors.toList());
//                for (Expense expense : expenselist) {
//                    if(expense.getDate().getMonthValue()!=LocalDate.now().getMonthValue()){
//                        Expense newExpense = new Expense(expense.getDescription(), expense.getPrice(), expense.getDate(), expense.getCategory(), expense.getRepeatOrNot());
//                        newExpense.setDate(LocalDate.now());
//                    //    repository.insert(newExpense).subscribe();
//                        expense.setRepeatOrNot(Expense.RepeatOrNot.NREPEAT);
//                        repository.update(expense).subscribe();
//                    }
//                }
//                subscribe.dispose();
//            }).subscribe();
//        return Result.success();
//    }
//}
}
