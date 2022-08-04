package br.com.beautystyle.ui.fragment.report;

import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_CALENDAR_VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.beautystyle.R;

import java.time.LocalDate;

import br.com.beautystyle.ViewModel.ReportViewModel;
import br.com.beautystyle.ui.fragment.CalendarViewFragment;
import br.com.beautystyle.util.CalendarUtil;


public class DailyReportFragment extends Fragment {

    private EditText dayOfReport;
    private final CalendarViewFragment calendarViewFragment = new CalendarViewFragment();
    private ReportViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(ReportViewModel.class);
    }

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
        dayOfReport.setOnClickListener(v ->
                calendarViewFragment.show(getParentFragmentManager(), TAG_CALENDAR_VIEW)
        );
    }

    private void onCalendarClickListener() {
        calendarViewFragment.setOnCalendarClickListener((view, year, month, dayOfMonth) -> {
            CalendarUtil.selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
            String dateFormatted = CalendarUtil.formatDateLong();
            dayOfReport.setText(dateFormatted);
            viewModel.addReportByDate();
            calendarViewFragment.dismiss();
        });
    }
}