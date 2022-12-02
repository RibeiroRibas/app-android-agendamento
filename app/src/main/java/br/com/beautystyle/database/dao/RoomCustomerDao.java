package br.com.beautystyle.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.beautystyle.model.entity.Customer;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;


@Dao
public interface RoomCustomerDao {

    @Query("SELECT * FROM Customer WHERE tenant= :tenant")
    Single<List<Customer>> getAll(Long tenant);

    @Query("SELECT * FROM Customer WHERE tenant= :tenant")
    Observable<List<Customer>> getAllObservable(Long tenant);

    @Update
    Completable update(Customer customer);

    @Delete
    Completable delete(Customer customer);

    @Insert
    Single<List<Long>> insertAll(List<Customer> customerList);

    @Update
    Completable updateAll(List<Customer> updatedCustomerList);

    @Insert
    Completable insert(Customer customer);
}
