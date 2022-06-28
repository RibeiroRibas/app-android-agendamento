package br.com.beautystyle.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.beautystyle.model.entity.Client;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;


@Dao
public interface RoomClientDao {

    @Query("SELECT * FROM client")
    Single<List<Client>> getAll();

    @Insert
    Single<Long> insert(Client client);

    @Update
    Completable update(Client client);

    @Delete
    Completable delete(Client client);

    @Query("SELECT * FROM Client c WHERE c.clientId = :clientId")
    Single<Client> getById(Long clientId);

    @Insert
    Single<List<Long>> insertAll(List<Client> clientList);

    @Query("SELECT * FROM Client c WHERE c.apiId = :apiId")
    Single<Client> getByApiId(Long apiId);

    @Query("SELECT * FROM Client c WHERE c.apiId = :apiId")
    Single<List<Client>> getAllByApiId(Long apiId);

    @Update
    Completable updateAll(List<Client> updatedClientList);

    @Query("SELECT * FROM Client WHERE clientId IN (:ids)")
    Single<List<Client>> getAllByIds(Long[] ids);
}
