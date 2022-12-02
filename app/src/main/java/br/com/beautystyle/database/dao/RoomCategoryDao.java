package br.com.beautystyle.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import br.com.beautystyle.model.entity.Category;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomCategoryDao {

    @Query("SELECT * FROM category WHERE tenant= :tenant")
    Observable<List<Category>> getAllObservable(Long tenant);

    @Query("SELECT * FROM category WHERE tenant= :tenant")
    Single<List<Category>> getAllSingle(Long tenant);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Category category);

    @Update
    Completable update(Category category);

    @Delete
    Completable delete(Category category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<List<Long>> insertAll(List<Category> categoriesFromApi);

}
