package br.com.beautystyle.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.database.room.references.EventWithJobs;
import br.com.beautystyle.model.entities.Event;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomEventDao {

    @Query("SELECT * FROM Event")
    Single<List<Event>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Event event);

    @Delete
    Completable delete(Event event);

    @Update
    Completable update(Event event);

    @Transaction
    @Query("SELECT * FROM Event ev WHERE ev.eventDate = :date")
    Single<List<EventWithJobs>> getEventListByDate(LocalDate date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<Event> convert);
}
