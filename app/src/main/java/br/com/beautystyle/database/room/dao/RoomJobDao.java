package br.com.beautystyle.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.beautystyle.model.entity.Job;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomJobDao {

    @Insert
    Single<Long> insert(Job service);

    @Query("SELECT * FROM Job")
    Single<List<Job>> getAll();

    @Update
    Completable update(Job service);

    @Delete
    Completable delete(Job service);

    @Insert
    Single<List<Long>> insertAll(List<Job> response);

    @Query("SELECT * FROM Job j WHERE j.jobId =:id")
    Single<Job> getById(Long id);

    @Query("SELECT * FROM Job j WHERE j.apiId =:apiId")
    Single<Job> getByApiId(Long apiId);

    @Update
    Completable updateAll(List<Job> updateJobs);
}
