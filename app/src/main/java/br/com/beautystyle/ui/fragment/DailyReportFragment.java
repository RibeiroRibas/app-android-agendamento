package br.com.beautystyle.ui.fragment;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.EditText;

import com.example.beautystyle.R;

import java.time.LocalDate;

import br.com.beautystyle.util.CalendarUtil;


public class DailyReportFragment extends Fragment {
    EditText dayOfReport;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_report_daily, container, false);
        dayOfReport = inflatedView.findViewById(R.id.et_fragment_report_daily_date);
        String today = CalendarUtil.formatDateLong(LocalDate.now());
        dayOfReport.setText(today);
        dayOfReport.setOnClickListener(v -> {
            View inflateViewCalendar = getLayoutInflater().inflate(R.layout.dialog_calendar, null);
            AlertDialog dialogBuilderCalendar = createDiologBuilderCalendar(inflateViewCalendar);
            setOnDateChangeListener(dialogBuilderCalendar, inflateViewCalendar);
        });
        return inflatedView;
    }

    private AlertDialog createDiologBuilderCalendar(View inflateViewCalendar) {
        AlertDialog.Builder dialogCalendar = new AlertDialog.Builder(requireActivity());
        dialogCalendar.setView(inflateViewCalendar);
        AlertDialog dialog = dialogCalendar.create();
        dialog.show();
        return dialog;
    }

    private void setOnDateChangeListener(AlertDialog dialogBuilderCalendar, View inflateViewCalendar) {
        CalendarView calendar = inflateViewCalendar.findViewById(R.id.dialog_calendar_view);
        calendar.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            LocalDate dateOfReport = LocalDate.of(year, month + 1, dayOfMonth);
            String dateFormated = CalendarUtil.formatDateLong(dateOfReport);
            dayOfReport.setText(dateFormated);
            dialogBuilderCalendar.dismiss();
        });
    }
}