package br.com.beautystyle.database.room.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.beautystyle.model.entities.Client;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;


@Dao
public interface RoomClientDao {

    @Query("SELECT * FROM client")
    Single<List<Client>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Client client);

    @Update
    Completable update(Client client);

    @Delete
    Completable delete(Client client);

    @Query("SELECT * FROM Client c WHERE c.clientId = :clientId")
    Single<Client> getById(Long clientId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertAll(List<Client> clientList);

}
