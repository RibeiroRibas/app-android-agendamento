package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_MONTHLY_REPORT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_REPORT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.beautystyle.domain.model.Event;
import br.com.beautystyle.domain.model.Expense;
import br.com.beautystyle.domain.model.MonthsOfTheYear;
import br.com.beautystyle.util.CalendarUtil;

public class MonthlyReportFragment extends Fragment {

    private int monthValue, yearValue = -1;
    private AutoCompleteTextView monthsOfTheYear, years;
    private final List<Event> eventList;
    private final List<Expense> expenseList;

    public MonthlyReportFragment(List<Event> eventList, List<Expense> expenseList) {
        this.eventList = eventList;
        this.expenseList = expenseList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_report_monthly, container, false);

        setAdapterMonthsOfTheYear(inflatedView);
        adapterMonthOfTheYearListener();
        setAdapterYears(inflatedView);
        adapterYearsListener();

        return inflatedView;
    }

    private void setAdapterMonthsOfTheYear(View inflatedView) {
        monthsOfTheYear = inflatedView.findViewById(R.id.fragment_report_monthly_month);
        setAdapter(monthsOfTheYear, MonthsOfTheYear.getMonthList());
        monthsOfTheYear.setText(CalendarUtil.formatMonth(LocalDate.now()), false);
    }

    private void adapterMonthOfTheYearListener() {
        monthValue = LocalDate.now().getMonthValue();//value default;
        monthsOfTheYear.setOnItemClickListener((parent, view, position, id) -> {
            monthValue = position + 1;
            setFragmentResult();
        });
    }

    private void setAdapterYears(View inflatedView) {
        years = inflatedView.findViewById(R.id.fragment_report_monthly_year);
        setAdapter(years, createListYearsEvent());
        years.setText(CalendarUtil.formatYear(LocalDate.now()), false);
    }

    private List<String> createListYearsEvent() {
        List<String> expenseYears = mapedExpenseList();
        List<String> eventYears = mapedEventList();
        assert eventYears != null;
        return eventYears.size() >= expenseList.size() ? eventYears : expenseYears;
    }

    private List<String> mapedExpenseList() {
        return  expenseList.stream()
                .map(Expense::getDate)
                .map(LocalDate::getYear)
                .distinct()
                .sorted(Comparator.comparing(Integer::intValue))
                .map(Objects::toString)
                .collect(Collectors.toList());
    }

    private List<String> mapedEventList() {
        return  eventList.stream()
                .map(Event::getEventDate)
                .map(LocalDate::getYear)
                .distinct()
                .sorted(Comparator.comparing(Integer::intValue))
                .map(Objects::toString)
                .collect(Collectors.toList());
    }

    private void adapterYearsListener() {
        yearValue = LocalDate.now().getYear();//value default
        years.setOnItemClickListener(((parent, view, position, id) -> {
            yearValue = Integer.parseInt(parent.getItemAtPosition(position).toString());
            setFragmentResult();
        }));
    }

    private void setAdapter(AutoCompleteTextView autoCompleteTextView, List<String> itemList) {
        ArrayAdapter<String> adapterItens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, itemList);
        autoCompleteTextView.setAdapter(adapterItens);
    }

    private void setFragmentResult() {
        Bundle result = new Bundle();
        LocalDate date = LocalDate.of(yearValue, monthValue, 1);
        result.putSerializable(KEY_MONTHLY_REPORT, date);
        getParentFragmentManager().setFragmentResult(KEY_REPORT, result);
    }


}