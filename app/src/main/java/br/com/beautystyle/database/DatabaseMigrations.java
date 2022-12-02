package br.com.beautystyle.database;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseMigrations {
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Client RENAME TO Costumer");
            database.execSQL("ALTER TABLE EventJobCroosRef RENAME TO EventJobCrossRef");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("CREATE TABLE IF NOT EXISTS `Events` (" +
                    "`eventId` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " `costumerCreatorId` INTEGER," +
                    " `eventDate` INTEGER," +
                    " `starTime` TEXT," +
                    " `endTime` TEXT," +
                    " `valueEvent` INTEGER," +
                    " `paymentStatus` TEXT," +
                    " `companyId` INTEGER," +
                    " `apiId` INTEGER)");

            database.execSQL("INSERT INTO Events (eventId, costumerCreatorId, eventDate, starTime," +
                    " endTime, valueEvent, paymentStatus, companyId, apiId) " +
                    "SELECT eventId, clientCreatorId, eventDate, starTime, endTime, valueEvent," +
                    " statusPagamento, companyId, apiId FROM Event");

            database.execSQL("DROP TABLE Event");

            database.execSQL("ALTER TABLE Events RENAME TO Event");

            database.execSQL("CREATE TABLE IF NOT EXISTS `Costumers` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "`name` TEXT, `phone` TEXT," +
                    " `companyId` INTEGER," +
                    " `apiId` INTEGER," +
                    " `userId` INTEGER)");

            database.execSQL("INSERT INTO Costumers (id, name, companyId, apiId, userId)" +
                    "SELECT clientId, name, companyId, apiId, userId FROM Costumer");

            database.execSQL("DROP TABLE Costumer");

            database.execSQL("ALTER TABLE Costumers RENAME TO Costumer");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {

            database.execSQL("CREATE TABLE IF NOT EXISTS `Events` (" +
                    "`eventId` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " `costumerCreatorId` INTEGER," +
                    " `eventDate` INTEGER," +
                    " `startTime` TEXT," +
                    " `endTime` TEXT," +
                    " `value` INTEGER," +
                    " `paymentStatus` TEXT," +
                    " `tenant` INTEGER," +
                    " `apiId` INTEGER)");

            database.execSQL("INSERT INTO Events (eventId, costumerCreatorId, eventDate, startTime," +
                    " endTime, value, paymentStatus, tenant, apiId) " +
                    "SELECT eventId, costumerCreatorId, eventDate, starTime, endTime, valueEvent," +
                    " paymentStatus, companyId, apiId FROM Event");

            database.execSQL("DROP TABLE Event");

            database.execSQL("ALTER TABLE Events RENAME TO Event");

            database.execSQL("CREATE TABLE IF NOT EXISTS `Costumers` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "`name` TEXT, `phone` TEXT," +
                    " `tenant` INTEGER," +
                    " `apiId` INTEGER," +
                    " `userId` INTEGER)");

            database.execSQL("INSERT INTO Costumers (id, name, phone, tenant, apiId, userId)" +
                    "SELECT id, name, phone, companyId, apiId, userId FROM Costumer");

            database.execSQL("DROP TABLE Costumer");

            database.execSQL("ALTER TABLE Costumers RENAME TO Costumer");

            database.execSQL("CREATE TABLE IF NOT EXISTS `Categories` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " `name` TEXT," +
                    " `apiId` INTEGER," +
                    " `tenant` INTEGER)");

            database.execSQL("INSERT INTO Categories (id, name, apiId, tenant)" +
                    "SELECT id, name, apiId, companyId FROM Category");

            database.execSQL("DROP TABLE Category");

            database.execSQL("ALTER TABLE Categories RENAME TO Category");

            database.execSQL("CREATE TABLE IF NOT EXISTS `Expenses` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " `description` TEXT," +
                    " `value` INTEGER," +
                    " `expenseDate` INTEGER," +
                    " `category` TEXT," +
                    " `repeatOrNot` TEXT," +
                    " `tenant` INTEGER," +
                    " `apiId` INTEGER)");

            database.execSQL("INSERT INTO Expenses (id, description, value, expenseDate,category," +
                    "repeatOrNot,tenant,apiId)" +
                    "SELECT id, description, price, expenseDate,category, repeatOrNot,companyId,apiId " +
                    "FROM Expense");

            database.execSQL("DROP TABLE Expense");

            database.execSQL("ALTER TABLE Expenses RENAME TO Expense");

            database.execSQL("CREATE TABLE IF NOT EXISTS `Jobs` (" +
                    "`jobId` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " `name` TEXT," +
                    " `price` INTEGER," +
                    " `durationTime` TEXT," +
                    " `tenant` INTEGER," +
                    " `apiId` INTEGER)");

            database.execSQL("INSERT INTO Jobs (jobId, name, price, durationTime,tenant,apiId)" +
                    "SELECT jobId, name, valueOfJob, durationTime,companyId,apiId " +
                    "FROM Job");

            database.execSQL("DROP TABLE Job");

            database.execSQL("ALTER TABLE Jobs RENAME TO Job");

        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `Customers` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "`name` TEXT, `phone` TEXT," +
                    " `tenant` INTEGER," +
                    " `apiId` INTEGER," +
                    " `isUser` INTEGER NOT NULL DEFAULT 0)");

            database.execSQL("INSERT INTO Customers (id, name,phone, tenant, apiId)" +
                    "SELECT id, name,phone, tenant, apiId FROM Costumer");

            database.execSQL("DROP TABLE Costumer");

            database.execSQL("ALTER TABLE Customers RENAME TO Customer");

        }
    };

    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `Events` (" +
                    "`eventId` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " `customerCreatorId` INTEGER," +
                    " `eventDate` INTEGER," +
                    " `startTime` TEXT," +
                    " `endTime` TEXT," +
                    " `value` INTEGER," +
                    " `hasPaymentReceived` INTEGER NOT NULL DEFAULT 0," +
                    " `tenant` INTEGER," +
                    " `apiId` INTEGER)");

            database.execSQL("INSERT INTO Events (eventId, customerCreatorId, eventDate, startTime," +
                    " endTime, value, tenant, apiId) " +
                    "SELECT eventId, costumerCreatorId, eventDate, startTime, endTime, value," +
                    " tenant, apiId FROM Event");

            database.execSQL("DROP TABLE Event");

            database.execSQL("ALTER TABLE Events RENAME TO Event");

        }
    };

    static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `Expenses` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " `description` TEXT," +
                    " `value` INTEGER," +
                    " `expenseDate` INTEGER," +
                    " `category` TEXT," +
                    " `repeat` INTEGER NOT NULL DEFAULT 0," +
                    " `tenant` INTEGER," +
                    " `apiId` INTEGER)");

            database.execSQL("INSERT INTO Expenses (id, description, value, expenseDate,category," +
                    "tenant,apiId)" +
                    "SELECT id, description, value, expenseDate,category, tenant,apiId " +
                    "FROM Expense");

            database.execSQL("DROP TABLE Expense");

            database.execSQL("ALTER TABLE Expenses RENAME TO Expense");

        }
    };

    static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `OpeningHours` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " `dayOfWeek` INTEGER NOT NULL," +
                    " `startTime` TEXT," +
                    " `endTime` TEXT," +
                    " `tenant` INTEGER," +
                    " `apiId` INTEGER)");

            database.execSQL("CREATE TABLE IF NOT EXISTS `BlockTime` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " `startTime` TEXT," +
                    " `endTime` TEXT," +
                    " `reason` TEXT," +
                    " `date` INTEGER," +
                    " `tenant` INTEGER," +
                    " `apiId` INTEGER)");

        }
    };

    static final Migration[] ALL_MIGRATIONS = {MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4,
            MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8};

}
