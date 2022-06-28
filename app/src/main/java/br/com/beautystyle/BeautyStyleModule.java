package br.com.beautystyle;

import static br.com.beautystyle.repository.ConstantsRepository.USER_SHARED_PREFERENCES;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class BeautyStyleModule {

    @Singleton
    @Provides
    public SharedPreferences providesSharedPreferences( Context context){
        return context.getSharedPreferences(USER_SHARED_PREFERENCES,Context.MODE_PRIVATE);
    }

}
