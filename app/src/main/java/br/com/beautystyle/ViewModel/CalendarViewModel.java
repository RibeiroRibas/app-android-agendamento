package br.com.beautystyle.ViewModel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.CalendarView;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.beautystyle.R;

import java.time.LocalDate;

public class CalendarViewModel extends ViewModel {

    private MutableLiveData<LocalDate> date;

    public void inflateCalendar(Activity activity) {
        @SuppressLint("InflateParams") View inflatedCalendar = activity.getLayoutInflater().inflate(R.layout.dialog_calendar, null);
        AlertDialog dialogBuilderCalendar = createDialogBuilderCalendar(inflatedCalendar,activity);
        setOnDateChangeListener(inflatedCalendar, dialogBuilderCalendar);
    }

    private AlertDialog createDialogBuilderCalendar(View inflatedCalendar,Activity activity) {
        AlertDialog.Builder dialogCalendar = new AlertDialog.Builder(activity);
        dialogCalendar.setView(inflatedCalendar);
        AlertDialog dialog = dialogCalendar.create();
        dialog.show();
        return dialog;
    }

    private void setOnDateChangeListener(View inflatedCalendar, AlertDialog dialogBuilderCalendar) {
        CalendarView calendar = inflatedCalendar.findViewById(R.id.dialog_calendar_view);
        calendar.setOnDateChangeListener(((view, year, month, dayOfMonth) -> {
            this.add(LocalDate.of(year, month + 1, dayOfMonth));
            dialogBuilderCalendar.dismiss();
        }));
    }

    public MutableLiveData<LocalDate> getDate(){
        if(date ==null){
            date = new MutableLiveData<>();
        }
        return date;
    }

    public void add(LocalDate calendarDate){
        date.setValue(calendarDate);
    }

}
