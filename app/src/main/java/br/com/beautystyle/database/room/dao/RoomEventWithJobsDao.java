package br.com.beautystyle.database.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.beautystyle.model.entities.EventJobCroosRef;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomEventWithJobsDao {

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    Completable insert(List<EventJobCroosRef> eventJobCroosRef);

    @Query("SELECT * FROM EventJobCroosRef e WHERE e.eventId = :eventId")
    Single<List<EventJobCroosRef>> getById(long eventId);

    @Update
    Completable update(List<EventJobCroosRef> eventJobCroosRef);

}
