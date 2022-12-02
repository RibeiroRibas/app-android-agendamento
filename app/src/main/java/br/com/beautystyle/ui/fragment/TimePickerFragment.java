package br.com.beautystyle.ui.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.time.LocalTime;

public class TimePickerFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    private static int hour;
    private static int minute;

    public static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener timePickerDialog) {
        final TimePickerFragment listener = new TimePickerFragment();
        listener.setOnTimeSetListener(timePickerDialog);
        hour = 1;
        minute = 0;
        return listener;
    }

    public static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener timePickerDialog,
                                                 LocalTime startTime) {
        final TimePickerFragment listener = new TimePickerFragment();
        listener.setOnTimeSetListener(timePickerDialog);
        hour = startTime.getHour();
        minute = startTime.getMinute();
        return listener;
    }

    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        this.onTimeSetListener = onTimeSetListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new TimePickerDialog(requireActivity(), onTimeSetListener, hour, minute, true);
    }

}
