package br.com.beautystyle.ui.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TimePickerFragment extends DialogFragment {


    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    private static int hour;
    private static int minute;

    public static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener timePickerDialog, boolean checkIs24hrs){
        final TimePickerFragment listener = new TimePickerFragment();
        listener.setOnTimeSetListener(timePickerDialog);
        if(checkIs24hrs){
            hour = 1;
            minute = 0;
        }else{
            hour = 7;
            minute = 30;
        }
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
