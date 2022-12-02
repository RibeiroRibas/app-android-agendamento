package br.com.beautystyle.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.beautystyle.model.entity.Job;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomJobDao {

    @Query("SELECT * FROM Job WHERE tenant= :tenant")
    Single<List<Job>> getAll(Long tenant);

    @Update
    Completable update(Job service);

    @Delete
    Completable delete(Job service);

    @Insert
    Single<List<Long>> insertAll(List<Job> response);

    @Update
    Completable updateAll(List<Job> updateJobs);

    @Query("SELECT * FROM Job WHERE tenant= :tenant")
    Observable<List<Job>> getAllLiveData(Long tenant);

    @Insert
    Completable insert(Job job);
}
