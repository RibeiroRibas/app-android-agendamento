package br.com.beautystyle;

import android.app.Application;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import br.com.beautystyle.util.AddExpenseWorker;

public class BeautyStyleApplication extends Application {

    private final WorkRequest workRequest = new OneTimeWorkRequest.Builder(AddExpenseWorker.class).build();
    public ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.factory().create(this);
        WorkManager.getInstance(this).enqueue(workRequest);
    }
}
