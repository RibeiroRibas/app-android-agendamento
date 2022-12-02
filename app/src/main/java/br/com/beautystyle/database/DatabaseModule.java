package br.com.beautystyle.database;

import static br.com.beautystyle.database.DatabaseMigrations.ALL_MIGRATIONS;

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
                .addMigrations(ALL_MIGRATIONS)
                .build();
    }
}
