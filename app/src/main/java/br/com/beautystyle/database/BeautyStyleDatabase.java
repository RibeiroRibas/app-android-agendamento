package br.com.beautystyle.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import br.com.beautystyle.database.converters.BigDecimalConverter;
import br.com.beautystyle.database.converters.LocalDateConverter;
import br.com.beautystyle.database.converters.LocalTimeConverter;
import br.com.beautystyle.database.dao.RoomBlockTimeDao;
import br.com.beautystyle.database.dao.RoomOpeningHoursDao;
import br.com.beautystyle.database.dao.RoomCategoryDao;
import br.com.beautystyle.database.dao.RoomCustomerDao;
import br.com.beautystyle.database.dao.RoomEventDao;
import br.com.beautystyle.database.dao.RoomEventWithJobsDao;
import br.com.beautystyle.database.dao.RoomExpenseDao;
import br.com.beautystyle.database.dao.RoomJobDao;
import br.com.beautystyle.database.dao.RoomUserDao;
import br.com.beautystyle.model.entity.BlockTime;
import br.com.beautystyle.model.entity.Category;
import br.com.beautystyle.model.entity.Customer;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.EventJobCrossRef;
import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.model.entity.Job;
import br.com.beautystyle.model.entity.OpeningHours;
import br.com.beautystyle.model.entity.User;


@Database(entities = {Customer.class, Event.class, Expense.class, Job.class, EventJobCrossRef.class,
        User.class, Category.class, OpeningHours.class, BlockTime.class},
        version = 8, exportSchema = false)
@TypeConverters({LocalDateConverter.class, LocalTimeConverter.class, BigDecimalConverter.class})
public abstract class BeautyStyleDatabase extends RoomDatabase {

    public abstract RoomCustomerDao getRoomClientDao();

    public abstract RoomEventDao getRoomEventDao();

    public abstract RoomExpenseDao getRoomExpenseDao();

    public abstract RoomJobDao getRoomJobDao();

    public abstract RoomEventWithJobsDao getRoomEventWithJobDao();

    public abstract RoomUserDao getRoomUserDao();

    public abstract RoomCategoryDao getRoomCategoryDao();

    public abstract RoomOpeningHoursDao getRoomOpeningHoursDao();

    public abstract RoomBlockTimeDao getRoomBlockTimeDao();

}
