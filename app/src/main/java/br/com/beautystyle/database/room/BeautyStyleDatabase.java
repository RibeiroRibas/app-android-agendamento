package br.com.beautystyle.database.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.com.beautystyle.database.room.converters.BigDecimalConverter;
import br.com.beautystyle.database.room.converters.ListObjectConverter;
import br.com.beautystyle.database.room.converters.LocalDateConverter;
import br.com.beautystyle.database.room.converters.LocalTimeConverter;
import br.com.beautystyle.database.room.dao.RoomClientDao;
import br.com.beautystyle.database.room.dao.RoomEventDao;
import br.com.beautystyle.database.room.dao.RoomEventWithJobsDao;
import br.com.beautystyle.database.room.dao.RoomExpenseDao;
import br.com.beautystyle.database.room.dao.RoomJobDao;
import br.com.beautystyle.model.entities.Client;
import br.com.beautystyle.model.entities.Event;
import br.com.beautystyle.model.entities.EventJobCroosRef;
import br.com.beautystyle.model.entities.Expense;
import br.com.beautystyle.model.entities.Job;


@Database(entities = {Client.class, Event.class, Expense.class, Job.class, EventJobCroosRef.class}, version = 1, exportSchema = false)
@TypeConverters({LocalDateConverter.class, LocalTimeConverter.class, BigDecimalConverter.class, ListObjectConverter.class})
public abstract class BeautyStyleDatabase extends RoomDatabase {

    private static final String NAME_DATABASE = "beautyStyle.db";

    public abstract RoomClientDao getRoomClientDao();

    public abstract RoomEventDao getRoomEventDao();

    public abstract RoomExpenseDao getRoomExpenseDao();

    public abstract RoomJobDao getRoomJobDao();

    public abstract RoomEventWithJobsDao getRoomEventWithJobDao();

    public static BeautyStyleDatabase getInstance(Context context) {
        return Room
                .databaseBuilder(context, BeautyStyleDatabase.class, NAME_DATABASE)
                .fallbackToDestructiveMigration()
                .build();
    }
}
