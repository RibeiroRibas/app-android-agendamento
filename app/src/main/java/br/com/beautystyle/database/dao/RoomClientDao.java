package br.com.beautystyle.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.beautystyle.model.entity.Costumer;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;


@Dao
public interface RoomClientDao {

    @Query("SELECT * FROM Costumer")
    Single<List<Costumer>> getAll();

    @Query("SELECT * FROM Costumer")
    Observable<List<Costumer>> getAllObservable();

    @Update
    Completable update(Costumer costumer);

    @Delete
    Completable delete(Costumer costumer);

    @Insert
    Single<List<Long>> insertAll(List<Costumer> costumerList);

    @Update
    Completable updateAll(List<Costumer> updatedCostumerList);

    @Insert
    Completable insert(Costumer costumer);
}
