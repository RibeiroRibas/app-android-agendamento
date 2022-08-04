package br.com.beautystyle.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.model.entity.Expense;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomExpenseDao {

    @Query("SELECT * FROM expense")
    Observable<List<Expense>> getAll();

    @Delete
    Completable delete(Expense selectedExpense);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insert(Expense expense);

    @Update
    Completable update(Expense expense);

    @Query("SELECT * FROM expense e WHERE e.expenseDate = :date")
    Single<List<Expense>> getByDate(LocalDate date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertAll(List<Expense> response);

    @Query("SELECT e.expenseDate FROM expense e")
    Single<List<LocalDate>> getYearsList();

    @Query("SELECT * FROM expense e WHERE e.expenseDate >= :startDate AND e.expenseDate <= :endDate")
    Single<List<Expense>> getByPeriod(LocalDate startDate, LocalDate endDate);

}
