package br.com.beautystyle.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import br.com.beautystyle.model.entity.OpeningHours;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomOpeningHoursDao {

    @Query("SELECT * FROM OpeningHours WHERE tenant= :tenant")
    Single<List<OpeningHours>> getAll(Long tenant);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<OpeningHours> openingHours);

}
