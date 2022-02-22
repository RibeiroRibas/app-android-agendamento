package br.com.beautystyle.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.com.beautystyle.data.db.converters.BigDecimalConverter;
import br.com.beautystyle.data.db.converters.ListObjectConverter;
import br.com.beautystyle.data.db.converters.LocalDateConverter;
import br.com.beautystyle.data.db.converters.LocalTimeConverter;
import br.com.beautystyle.data.db.dao.RoomClientDao;
import br.com.beautystyle.data.db.dao.RoomEventDao;
import br.com.beautystyle.data.db.dao.RoomEventWithServicesDao;
import br.com.beautystyle.data.db.dao.RoomExpenseDao;
import br.com.beautystyle.data.db.dao.RoomServiceDao;
import br.com.beautystyle.data.db.references.EventServiceCroosRef;
import br.com.beautystyle.domain.model.Client;
import br.com.beautystyle.domain.model.Event;
import br.com.beautystyle.domain.model.Expense;
import br.com.beautystyle.domain.model.Services;

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
