package br.com.beautystyle.database.room.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import br.com.beautystyle.model.entity.EventJobCroosRef;
import io.reactivex.rxjava3.core.Completable;

@Dao
public interface RoomEventWithJobsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(List<EventJobCroosRef> eventJobCroosRef);

    @Query("DELETE FROM EventJobCroosRef WHERE eventId IN (:eventIds)")
    Completable deleteAllByIds(Long[] eventIds);

}
