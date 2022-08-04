package br.com.beautystyle.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.beautystyle.R;

public class CalendarViewFragment extends DialogFragment {

    private CalendarView.OnDateChangeListener listener;

    public void setOnCalendarClickListener (CalendarView.OnDateChangeListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams")
        View inflatedCalendar = LayoutInflater.from(requireActivity())
                .inflate(R.layout.dialog_calendar, null);
        AlertDialog dialogBuilderCalendar = createDialogBuilderCalendar(inflatedCalendar);
        setOnDateChangeListener(inflatedCalendar);
        return dialogBuilderCalendar;
    }


    private AlertDialog createDialogBuilderCalendar(View inflatedCalendar) {
        AlertDialog.Builder dialogCalendar = new AlertDialog.Builder(requireActivity());
        dialogCalendar.setView(inflatedCalendar);
        AlertDialog dialog = dialogCalendar.create();
        dialog.show();
        return dialog;
    }

    private void setOnDateChangeListener(View inflatedCalendar) {
        CalendarView calendar = inflatedCalendar.findViewById(R.id.dialog_calendar_view);
        calendar.setOnDateChangeListener(listener);
    }

}
