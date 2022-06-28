package br.com.beautystyle.ui.fragment.report;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_DAILY_REPORT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_REPORT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_CALENDAR_VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.beautystyle.R;

import java.time.LocalDate;

import br.com.beautystyle.ui.fragment.CalendarViewFragment;
import br.com.beautystyle.util.CalendarUtil;


public class DailyReportFragment extends Fragment {

    private EditText dayOfReport;
    private final CalendarViewFragment calendarViewFragment = new CalendarViewFragment();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_report_daily, container, false);

        setDailyReportListener(inflatedView);
        onCalendarClickListener();

        return inflatedView;
    }

    private void setDailyReportListener(View inflatedView) {
        dayOfReport = inflatedView.findViewById(R.id.et_fragment_report_daily_date);
        dayOfReport.setOnClickListener(v -> {
            calendarViewFragment.show(getParentFragmentManager(), TAG_CALENDAR_VIEW);
        });
    }

    private void onCalendarClickListener() {
        calendarViewFragment.setOnCalendarClickListener((view, year, month, dayOfMonth) -> {
            LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
            CalendarUtil.selectedDate = selectedDate;
            setDateAndResult(selectedDate);
            calendarViewFragment.dismiss();
        });
    }

    private void setDateAndResult(LocalDate date) {
            String dateFormated = CalendarUtil.formatDateLong(date);
            dayOfReport.setText(dateFormated);
            setFragmenResult(date);
    }

    private void setFragmenResult(LocalDate date) {
        Bundle result = new Bundle();
        result.putSerializable(KEY_DAILY_REPORT, date);
        getParentFragmentManager().setFragmentResult(KEY_REPORT, result);
    }
}