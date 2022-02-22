package br.com.beautystyle.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import br.com.beautystyle.data.db.references.EventServiceCroosRef;
import br.com.beautystyle.data.db.references.EventWithServices;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomEventWithServicesDao {

    @Transaction
    @Query("SELECT * FROM EventServiceCroosRef ")
    Single<List<EventWithServices>> getEventWithServices();

    @Insert (onConflict = OnConflictStrategy.REPLACE)
    Completable insert(List<EventServiceCroosRef> eventServiceCroosRef);

    @Query("SELECT * FROM EventServiceCroosRef e WHERE e.eventId = :eventId")
    Single<List<EventServiceCroosRef>> getById(long eventId);

    @Delete
    Completable delete(List<EventServiceCroosRef> eventServiceCroosRef);
}
