package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_DAILY_REPORT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_REPORT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.beautystyle.R;

import java.time.LocalDate;

import br.com.beautystyle.ViewModel.CalendarViewModel;
import br.com.beautystyle.util.CalendarUtil;


public class DailyReportFragment extends Fragment {

    private EditText dayOfReport;
    private CalendarViewModel calendarViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_report_daily, container, false);

        setDailyReportDefault(inflatedView);
        setDailyReportListener();
        calendarObserve();

        return inflatedView;
    }

    private void setDailyReportDefault(View inflatedView) {
        dayOfReport = inflatedView.findViewById(R.id.et_fragment_report_daily_date);
        String today = CalendarUtil.formatDateLong(LocalDate.now());
        dayOfReport.setText(today);
    }

    private void setDailyReportListener() {
        dayOfReport.setOnClickListener(v -> calendarViewModel.inflateCalendar(requireActivity()));
    }

    private void calendarObserve() {
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.getDate().observe(requireActivity(), this::setDate);
    }

    private void setDate(LocalDate date) {
        String dateFormated = CalendarUtil.formatDateLong(date);
        dayOfReport.setText(dateFormated);
        Bundle result = new Bundle();
        result.putSerializable(KEY_DAILY_REPORT, date);
        getParentFragmentManager().setFragmentResult(KEY_REPORT, result);
    }
}