package br.com.beautystyle.ui.fragment.report;

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
    private Bundle result;
    private CalendarViewModel calendarViewModel;
    int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        position = 0;
        result = new Bundle();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_report_period, container, false);

        initWidgets(inflatedView);
        startDateListener();
        endDateListener();

        calendarViewModel.getDate().observe(requireActivity(), this::setDateAndResult);

        return inflatedView;
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
        String dateFormated = CalendarUtil.formatDate(date);
        if (position == 1) {
            startDate.setText(dateFormated);
        } else if (position == 2) {
            endDate.setText(dateFormated);
        }
    }

    private boolean checkEmptyFields() {
        return !startDate.getText().toString().isEmpty() && !endDate.getText().toString().isEmpty();
    }

    private void setFragmentResult() {
        LocalDate mStartDate = CalendarUtil.fromStringToLocalDate(startDate.getText().toString());
        LocalDate mEndDAte = CalendarUtil.fromStringToLocalDate(endDate.getText().toString());
        result.putSerializable(KEY_START_DATE, mStartDate);
        result.putSerializable(KEY_END_DATE, mEndDAte);
        getParentFragmentManager().setFragmentResult(KEY_REPORT, result);
    }

    private void endDateListener() {
        endDate.setOnClickListener(v -> {
            position = 2;
            calendarViewModel.inflateCalendar(requireActivity());
        });
    }

    private void startDateListener() {
        startDate.setOnClickListener(v -> {
            position = 1;
            calendarViewModel.inflateCalendar(requireActivity());
        });
    }
}