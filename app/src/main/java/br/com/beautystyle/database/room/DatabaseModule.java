package br.com.beautystyle.database.room;

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
                .fallbackToDestructiveMigration()
                .build();
    }
}
