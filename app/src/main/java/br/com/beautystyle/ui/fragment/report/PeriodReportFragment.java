package br.com.beautystyle.ui.fragment.report;

import static br.com.beautystyle.util.ConstantsUtil.DD_MM_YYYY;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.util.Objects;

import br.com.beautystyle.ViewModel.ReportViewModel;
import br.com.beautystyle.ui.fragment.CalendarViewFragment;
import br.com.beautystyle.util.CalendarUtil;

public class PeriodReportFragment extends Fragment {

    private static final String TAG_CALENDAR_VIEW_START_DATE = "calendarStartDate";
    private static final String TAG_CALENDAR_VIEW_END_DATE = "calendarEndDate";
    private EditText startDateEdTxt, endDateEdTxt;
    private CalendarViewFragment calendarViewFragment;
    private ReportViewModel viewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendarViewFragment = new CalendarViewFragment();
        viewModel = new ViewModelProvider(requireParentFragment()).get(ReportViewModel.class);
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
            onBindViews(selectedDate);
            addReportLiveData();
            calendarViewFragment.dismiss();
        });
    }

    private void initWidgets(View inflatedView) {
        endDateEdTxt = inflatedView.findViewById(R.id.et_fragment_report_period_end_date);
        startDateEdTxt = inflatedView.findViewById(R.id.fragment_report_period_start_date);
    }

    private void addReportLiveData() {
        if (checkEmptyFields()) {
            LocalDate startDate = formatStartDate(startDateEdTxt);
            LocalDate endDate = formatEndDate(endDateEdTxt);
            viewModel.addReportByPeriod(startDate, endDate);
        }
    }

    private LocalDate formatEndDate(EditText endDateEdTxt) {
        return CalendarUtil.fromStringToLocalDate(endDateEdTxt.getText().toString());
    }

    private LocalDate formatStartDate(EditText startDateEdTxt) {
        return formatEndDate(startDateEdTxt);
    }

    private void onBindViews(LocalDate date) {
        String formattedDate = CalendarUtil.formatLocalDate(date, DD_MM_YYYY);
        if (Objects.equals(calendarViewFragment.getTag(), TAG_CALENDAR_VIEW_START_DATE)) {
            startDateEdTxt.setText(formattedDate);
        } else if (Objects.equals(calendarViewFragment.getTag(), TAG_CALENDAR_VIEW_END_DATE)) {
            endDateEdTxt.setText(formattedDate);
        }
    }

    private boolean checkEmptyFields() {
        return !startDateEdTxt.getText().toString().isEmpty() && !endDateEdTxt.getText().toString().isEmpty();
    }

    private void endDateListener() {
        endDateEdTxt.setOnClickListener(v ->
                calendarViewFragment.show(getChildFragmentManager(), TAG_CALENDAR_VIEW_END_DATE)
        );
    }

    private void startDateListener() {
        startDateEdTxt.setOnClickListener(v ->
            calendarViewFragment.show(getChildFragmentManager(), TAG_CALENDAR_VIEW_START_DATE)
        );
    }
}