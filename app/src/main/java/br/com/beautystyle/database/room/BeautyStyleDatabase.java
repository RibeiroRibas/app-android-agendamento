package br.com.beautystyle.database.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.com.beautystyle.database.room.converters.BigDecimalConverter;
import br.com.beautystyle.database.room.converters.ListObjectConverter;
import br.com.beautystyle.database.room.converters.LocalDateConverter;
import br.com.beautystyle.database.room.converters.LocalTimeConverter;
import br.com.beautystyle.database.room.dao.RoomCategoryDao;
import br.com.beautystyle.database.room.dao.RoomClientDao;
import br.com.beautystyle.database.room.dao.RoomEventDao;
import br.com.beautystyle.database.room.dao.RoomEventWithJobsDao;
import br.com.beautystyle.database.room.dao.RoomExpenseDao;
import br.com.beautystyle.database.room.dao.RoomJobDao;
import br.com.beautystyle.database.room.dao.RoomUserDao;
import br.com.beautystyle.model.entity.User;
import br.com.beautystyle.model.entity.Category;
import br.com.beautystyle.model.entity.Client;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.EventJobCroosRef;
import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.model.entity.Job;


@Database(entities = {Client.class, Event.class, Expense.class, Job.class, EventJobCroosRef.class, User.class, Category.class}, version = 1, exportSchema = false)
@TypeConverters({LocalDateConverter.class, LocalTimeConverter.class, BigDecimalConverter.class, ListObjectConverter.class})
public abstract class BeautyStyleDatabase extends RoomDatabase {

    public abstract RoomClientDao getRoomClientDao();

    public abstract RoomEventDao getRoomEventDao();

    public abstract RoomExpenseDao getRoomExpenseDao();

    public abstract RoomJobDao getRoomJobDao();

    public abstract RoomEventWithJobsDao getRoomEventWithJobDao();

    public abstract RoomUserDao getRoomUserDao();

    public abstract RoomCategoryDao getRoomCategoryDao();

}
