package br.com.beautystyle.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import br.com.beautystyle.model.entity.User;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface RoomUserDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(User user);

    @Update
    Completable update(User user);

    @Query("SELECT * FROM user u WHERE u.email =:email")
    Single<User> getByEmail(String email);
}
