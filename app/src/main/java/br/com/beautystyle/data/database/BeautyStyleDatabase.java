package br.com.beautystyle.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.com.beautystyle.data.database.converters.BigDecimalConverter;
import br.com.beautystyle.data.database.converters.ListObjectConverter;
import br.com.beautystyle.data.database.converters.LocalDateConverter;
import br.com.beautystyle.data.database.converters.LocalTimeConverter;
import br.com.beautystyle.data.database.dao.RoomClientDao;
import br.com.beautystyle.data.database.dao.RoomEventDao;
import br.com.beautystyle.data.database.dao.RoomEventWithServicesDao;
import br.com.beautystyle.data.database.dao.RoomExpenseDao;
import br.com.beautystyle.data.database.dao.RoomServiceDao;
import br.com.beautystyle.data.database.references.EventServiceCroosRef;
import br.com.beautystyle.model.Client;
import br.com.beautystyle.model.Event;
import br.com.beautystyle.model.Expense;
import br.com.beautystyle.model.Services;


@Database(entities = {Client.class, Event.class, Expense.class, Services.class, EventServiceCroosRef.class}, version = 1, exportSchema = false)
@TypeConverters({LocalDateConverter.class, LocalTimeConverter.class, BigDecimalConverter.class, ListObjectConverter.class})
public abstract class BeautyStyleDatabase extends RoomDatabase {

    private static final String NAME_DATABASE = "beautyStyle.db";

    public abstract RoomClientDao getRoomClientDao();

    public abstract RoomEventDao getRoomEventDao();

    public abstract RoomExpenseDao getRoomExpenseDao();

    public abstract RoomServiceDao getRoomServiceDao();

    public abstract RoomEventWithServicesDao getRoomEventWithServicesDao();

    public static BeautyStyleDatabase getInstance(Context context) {
        return Room
                .databaseBuilder(context, BeautyStyleDatabase.class, NAME_DATABASE)
                .build();
    }
}
