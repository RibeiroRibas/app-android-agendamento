package br.com.beautystyle.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.com.beautystyle.database.converters.BigDecimalConverter;
import br.com.beautystyle.database.converters.LocalDateConverter;
import br.com.beautystyle.database.converters.LocalTimeConverter;
import br.com.beautystyle.database.dao.RoomCategoryDao;
import br.com.beautystyle.database.dao.RoomClientDao;
import br.com.beautystyle.database.dao.RoomEventDao;
import br.com.beautystyle.database.dao.RoomEventWithJobsDao;
import br.com.beautystyle.database.dao.RoomExpenseDao;
import br.com.beautystyle.database.dao.RoomJobDao;
import br.com.beautystyle.database.dao.RoomUserDao;
import br.com.beautystyle.model.entity.Category;
import br.com.beautystyle.model.entity.Costumer;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.EventJobCrossRef;
import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.model.entity.User;


@Database(entities = {Costumer.class, Event.class, Expense.class, Job.class, EventJobCrossRef.class, User.class, Category.class}, version = 2, exportSchema = false)
@TypeConverters({LocalDateConverter.class, LocalTimeConverter.class, BigDecimalConverter.class})
public abstract class BeautyStyleDatabase extends RoomDatabase {

    public abstract RoomClientDao getRoomClientDao();

    public abstract RoomEventDao getRoomEventDao();

    public abstract RoomExpenseDao getRoomExpenseDao();

    public abstract RoomJobDao getRoomJobDao();

    public abstract RoomEventWithJobsDao getRoomEventWithJobDao();

    public abstract RoomUserDao getRoomUserDao();

    public abstract RoomCategoryDao getRoomCategoryDao();

}
