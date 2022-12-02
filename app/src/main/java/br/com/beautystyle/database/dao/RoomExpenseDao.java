package br.com.beautystyle.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.model.entity.Expense;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomExpenseDao {

    @Delete
    Completable delete(Expense selectedExpense);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(Expense expense);

    @Update
    Completable update(Expense expense);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertAll(List<Expense> response);

    @Query("SELECT e.expenseDate FROM expense e WHERE tenant= :tenant")
    Single<List<LocalDate>> getYearsList(Long tenant);

    @Query("SELECT * FROM expense e WHERE tenant= :tenant AND e.expenseDate >= :startDate AND e.expenseDate <= :endDate")
    Single<List<Expense>> getByPeriod(LocalDate startDate, LocalDate endDate, Long tenant);

    @Transaction
    @Query("SELECT * FROM expense e WHERE e.expenseDate = :date AND tenant= :tenant")
    Single<List<Expense>> getByDate(LocalDate date, Long tenant);

}
