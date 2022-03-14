package br.com.beautystyle.data.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;
import androidx.room.Transaction;

import java.util.List;

import br.com.beautystyle.data.database.references.EventServiceCroosRef;
import br.com.beautystyle.data.database.references.EventWithServices;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomEventWithServicesDao {

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
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
