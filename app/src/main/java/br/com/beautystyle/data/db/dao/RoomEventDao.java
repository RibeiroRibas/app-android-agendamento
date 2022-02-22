package br.com.beautystyle.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.domain.model.Event;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomEventDao {

    @Query("SELECT * FROM Event")
    Single<List<Event>> getAll();

    @Query("SELECT * FROM Event e WHERE e.eventDate = :date ")
    Observable<List<Event>> getByDate(LocalDate date);

    @Insert
    Single<Long> insert(Event event);

    @Delete
    Completable delete(Event event);

    @Update
    Completable update(Event event);

}
