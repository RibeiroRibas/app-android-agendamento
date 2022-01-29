package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_END_DATE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_REPORT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_START_DATE;

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

public class PeriodReportFragment extends Fragment {

    private EditText startDate, endDate;
    private final Bundle result = new Bundle();
    private CalendarViewModel calendarViewModel;
    int position = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_report_period, container, false);

        setStartDateOnClickListener(inflatedView);
        setEndDateOnCLickListener(inflatedView);
        calendarObserve();

        return inflatedView;
    }

    private void calendarObserve() {
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.getDate().observe(requireActivity(), this::setDate);
    }

    private void setDate(LocalDate date) {
        String dateFormated = CalendarUtil.formatDate(date);
        if (position == 1) {
            startDate.setText(dateFormated);
            result.putSerializable(KEY_START_DATE, date);
        } else if (position == 2) {
            endDate.setText(dateFormated);
            result.putSerializable(KEY_END_DATE, date);
        }
        if (!startDate.getText().toString().isEmpty() && !endDate.getText().toString().isEmpty())
            getParentFragmentManager().setFragmentResult(KEY_REPORT, result);
    }

    private void setEndDateOnCLickListener(View inflatedView) {
        endDate = inflatedView.findViewById(R.id.et_fragment_report_period_end_date);
        endDate.setOnClickListener(v -> {
            position = 2;
            calendarViewModel.inflateCalendar(requireActivity());
        });
    }

    private void setStartDateOnClickListener(View inflatedView) {
        startDate = inflatedView.findViewById(R.id.fragment_report_period_start_date);
        startDate.setOnClickListener(v -> {
            position = 1;
            calendarViewModel.inflateCalendar(requireActivity());
        });
    }
}