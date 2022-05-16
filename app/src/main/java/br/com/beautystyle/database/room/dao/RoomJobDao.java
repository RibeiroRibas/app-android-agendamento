package br.com.beautystyle.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.beautystyle.model.entities.Job;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface RoomJobDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Job service);

    @Query("SELECT * FROM Job")
    Observable<List<Job>> getAll();

    @Update
    Completable update(Job service);

    @Delete
    Completable delete(Job service);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<Job> response);
}
