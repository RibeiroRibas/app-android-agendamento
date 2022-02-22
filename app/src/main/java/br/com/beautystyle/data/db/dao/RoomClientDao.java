package br.com.beautystyle.data.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.beautystyle.domain.model.Client;
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

    @Query("SELECT * FROM Client c WHERE c.id = :clientId")
    Single<Client> getById(int clientId);

    @Insert
    Single<Long[]> insertAll(List<Client> clientList);

}
