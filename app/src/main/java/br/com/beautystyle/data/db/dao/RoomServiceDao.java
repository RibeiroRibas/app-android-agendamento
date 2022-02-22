package br.com.beautystyle.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.beautystyle.domain.model.Services;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;

@Dao
public interface RoomServiceDao {

    @Insert
    Completable insert(Services service);

    @Query("SELECT * FROM services")
    Observable<List<Services>> getAll();

    @Update
    Completable update(Services service);

    @Delete
    Completable delete(Services service);

}
