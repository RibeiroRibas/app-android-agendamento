package br.com.beautystyle.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.database.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.Event;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomEventDao {

    @Transaction
    @Query("SELECT * FROM Event ev WHERE ev.eventDate = :date")
    Single<List<EventWithClientAndJobs>> getByDate(LocalDate date);

    @Insert
    Single<Long> insert(Event event);

    @Delete
    Completable delete(Event event);

    @Update
    Completable update(Event event);

    @Insert
    Single<List<Long>> insertAll(List<Event> convert);

    @Update
    Completable updateAll(List<Event> eventList);
}
