package br.com.beautystyle.ui.fragment.report;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_END_DATE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_REPORT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_START_DATE;
import static br.com.beautystyle.util.ConstantsUtil.DD_MM_YYYY;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.util.Objects;

import br.com.beautystyle.ui.fragment.CalendarViewFragment;
import br.com.beautystyle.util.CalendarUtil;

public class PeriodReportFragment extends Fragment {

    private static final String TAG_CALENDAR_VIEW_START_DATE = "calendarStardDate";
    private static final String TAG_CALENDAR_VIEW_END_DATE = "calendarEndDate";
    private EditText startDate, endDate;
    private CalendarViewFragment calendarViewFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendarViewFragment = new CalendarViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_report_period, container, false);

        initWidgets(inflatedView);
        startDateListener();
        endDateListener();
        calendarClickListener();

        return inflatedView;
    }

    private void calendarClickListener() {
        calendarViewFragment.setOnCalendarClickListener((view, year, month, dayOfMonth) -> {
            LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
            CalendarUtil.selectedDate = selectedDate;
            setDateAndResult(selectedDate);
            calendarViewFragment.dismiss();
        });
    }

    private void initWidgets(View inflatedView) {
        endDate = inflatedView.findViewById(R.id.et_fragment_report_period_end_date);
        startDate = inflatedView.findViewById(R.id.fragment_report_period_start_date);
    }

    private void setDateAndResult(LocalDate date) {
        setDate(date);
        if (checkEmptyFields())
            setFragmentResult();
    }

    private void setDate(LocalDate date) {
        String dateFormated = CalendarUtil.formatLocalDate(date, DD_MM_YYYY);
        if (Objects.equals(calendarViewFragment.getTag(), TAG_CALENDAR_VIEW_START_DATE)) {
            startDate.setText(dateFormated);
        } else if (Objects.equals(calendarViewFragment.getTag(), TAG_CALENDAR_VIEW_END_DATE)) {
            endDate.setText(dateFormated);
        }
    }

    private boolean checkEmptyFields() {
        return !startDate.getText().toString().isEmpty() && !endDate.getText().toString().isEmpty();
    }

    private void setFragmentResult() {
        LocalDate startDate = CalendarUtil.fromStringToLocalDate(this.startDate.getText().toString());
        LocalDate endDAte = CalendarUtil.fromStringToLocalDate(endDate.getText().toString());
        Bundle result = new Bundle();
        result.putSerializable(KEY_START_DATE, startDate);
        result.putSerializable(KEY_END_DATE, endDAte);
        getParentFragmentManager().setFragmentResult(KEY_REPORT, result);
    }

    private void endDateListener() {
        endDate.setOnClickListener(v -> {
            calendarViewFragment.show(getChildFragmentManager(), TAG_CALENDAR_VIEW_END_DATE);
        });
    }

    private void startDateListener() {
        startDate.setOnClickListener(v -> {
            calendarViewFragment.show(getChildFragmentManager(), TAG_CALENDAR_VIEW_START_DATE);
        });
    }
}