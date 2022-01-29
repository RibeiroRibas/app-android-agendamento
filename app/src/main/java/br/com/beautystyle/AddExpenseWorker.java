package br.com.beautystyle;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import br.com.beautystyle.dao.ExpenseDao;
import br.com.beautystyle.model.Expense;

public class AddExpenseWorker extends Worker {

    public AddExpenseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {

        if (LocalDate.now().getDayOfMonth() == 1) {
            ExpenseDao dao = new ExpenseDao();

            List<Expense> expenselist = dao.listAll().stream()
                    .filter(ex -> ex.getRepeatOrNot().equals(Expense.RepeatOrNot.REPEAT))
                    .collect(Collectors.toList());

                for (Expense expense : expenselist) {
                        Expense newExpense = new Expense(expense.getDescription(), expense.getPrice(), expense.getDate(), expense.getCategory(), expense.getRepeatOrNot());
                        newExpense.setDate(LocalDate.now());
                        dao.save(newExpense);
                        expense.setRepeatOrNot(Expense.RepeatOrNot.NREPEAT);
                        dao.edit(expense);
                    }
        }

        return Result.success();
    }
}
