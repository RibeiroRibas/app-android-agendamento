package br.com.beautystyle.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import br.com.beautystyle.model.entity.EventJobCrossRef;
import io.reactivex.rxjava3.core.Completable;

@Dao
public interface RoomEventWithJobsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(List<EventJobCrossRef> eventJobCrossRef);

    @Query("DELETE FROM EventJobCrossRef WHERE eventId IN (:eventIds)")
    Completable deleteAllByIds(Long[] eventIds);

}
