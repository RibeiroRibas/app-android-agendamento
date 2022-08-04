package br.com.beautystyle.database;

import androidx.annotation.NonNull;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class DatabaseMigrations {
    static final Migration MIGRATION_1_2 = new Migration(1,2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Client RENAME TO Costumer");
            database.execSQL("ALTER TABLE EventJobCroosRef RENAME TO EventJobCrossRef");
        }
    };
}
