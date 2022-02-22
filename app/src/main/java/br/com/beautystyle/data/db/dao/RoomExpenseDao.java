package br.com.beautystyle.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.beautystyle.domain.model.Expense;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomExpenseDao {

    @Query("SELECT * FROM expense")
    Single<List<Expense>> getAll();

    @Delete
    Completable delete(Expense selectedExpense);

    @Insert
    Single<Long> insert(Expense expense);

    @Update
    Completable update(Expense expense);



}
