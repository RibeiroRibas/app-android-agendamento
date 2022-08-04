package br.com.beautystyle;

import android.app.Application;

public class BeautyStyleApplication extends Application {

    public ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.factory().create(this);
    }
}
