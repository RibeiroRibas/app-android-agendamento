package br.com.beautystyle.data.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.beautystyle.model.Expense;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomExpenseDao {

    @Query("SELECT * FROM expense")
    Observable<List<Expense>> getAll();

    @Delete
    Completable delete(Expense selectedExpense);

    @Insert
    Single<Long> insert(Expense expense);

    @Update
    Completable update(Expense expense);

}
