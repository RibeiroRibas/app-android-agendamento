package br.com.beautystyle.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.model.entity.BlockTime;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomBlockTimeDao {

    @Query("SELECT * FROM BlockTime WHERE tenant=:tenant AND date=:date ")
    Single<List<BlockTime>> getAll(Long tenant, LocalDate date);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertAll(List<BlockTime> blockTimes);

    @Delete
    Completable delete(BlockTime blockTimes);

    @Update
    Completable update(BlockTime category);

}
