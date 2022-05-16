package br.com.beautystyle.ui.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class TimePickerFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    public static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener timePickerDialog){
        final TimePickerFragment listener = new TimePickerFragment();
        listener.setOnTimeSetListener(timePickerDialog);
        return listener;
    }

    public void setOnTimeSetListener(TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        this.onTimeSetListener = onTimeSetListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new TimePickerDialog(requireActivity(), onTimeSetListener, 7, 0, DateFormat.is24HourFormat(getActivity()));
    }

}
