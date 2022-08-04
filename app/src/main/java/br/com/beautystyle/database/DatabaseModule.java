package br.com.beautystyle.database;

import static br.com.beautystyle.database.DatabaseMigrations.MIGRATION_1_2;

import android.content.Context;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    @Singleton
    @Provides
    public BeautyStyleDatabase providesLocalDataBase(Context context){
        return Room
                .databaseBuilder(context, BeautyStyleDatabase.class, "beautyStyle.db")
                .addMigrations(MIGRATION_1_2)
                .build();
    }
}
