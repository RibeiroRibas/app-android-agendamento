package br.com.beautystyle.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.database.room.references.EventWithClientAndJobs;
import br.com.beautystyle.model.entity.Event;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomEventDao {

    @Transaction
    @Query("SELECT * FROM Event")
    Single<List<EventWithClientAndJobs>> getAll();

    @Insert
    Single<Long> insert(Event event);

    @Delete
    Completable delete(Event event);

    @Update
    Completable update(Event event);

    @Transaction
    @Query("SELECT * FROM Event ev WHERE ev.eventDate = :date")
    Single<List<EventWithClientAndJobs>> getEventListByDate(LocalDate date);

    @Insert
    Single<List<Long>> insertAll(List<Event> convert);

    @Query("SELECT * FROM Event ev WHERE ev.apiId = :apiId")
    Single<Event> getByApiId(Long apiId);

    @Query("SELECT * FROM Event ev WHERE ev.clientCreatorId =:clientId")
    Single<List<Event>> findByClientId(Long clientId);

    @Update
    Completable updatelist(List<Event> eventList);
}
