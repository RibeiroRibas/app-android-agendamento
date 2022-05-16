package br.com.beautystyle.ui.fragment.report;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_MONTHLY_REPORT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_REPORT;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.beautystyle.ViewModel.ReportViewModel;
import br.com.beautystyle.model.entities.Event;
import br.com.beautystyle.model.entities.Expense;
import br.com.beautystyle.model.enuns.MonthsOfTheYear;
import br.com.beautystyle.util.CalendarUtil;

public class MonthlyReportFragment extends Fragment {

    private int monthValue, yearValue = -1;
    private AutoCompleteTextView monthsOfTheYear, years;
    private ReportViewModel reportViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportViewModel = new ViewModelProvider(requireActivity()).get(ReportViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_report_monthly, container, false);

        initWidgets(inflatedView);
        setAdapters();
        adapterMonthOfTheYearListener();
        adapterYearsListener();

        return inflatedView;
    }

    private void setAdapters() {
        reportViewModel.getEventList().observe(getViewLifecycleOwner(),eventList->
            reportViewModel.getExpenseList().observe(getViewLifecycleOwner(),expenseList ->{
                setAdapterYears(eventList,expenseList);
                setAdapterMonthsOfTheYear();
            })
        );
    }

    private void initWidgets(View inflatedView) {
        years = inflatedView.findViewById(R.id.fragment_report_monthly_year);
        monthsOfTheYear = inflatedView.findViewById(R.id.fragment_report_monthly_month);
    }

    private void setAdapterMonthsOfTheYear() {
        setAdapter(monthsOfTheYear, MonthsOfTheYear.getMonthList());
    }

    private void adapterMonthOfTheYearListener() {
        monthValue = LocalDate.now().getMonthValue();//value default;
        monthsOfTheYear.setText(CalendarUtil.formatMonth(LocalDate.now()), false);
        monthsOfTheYear.setOnItemClickListener((parent, view, position, id) -> {
            monthValue = position + 1;
            setFragmentResult();
        });
    }

    private void setAdapterYears(List<Event> eventList, List<Expense> expenseList) {
        List<String> yearsList = createListYearsEvent(eventList,expenseList);
        setAdapter(years, yearsList);
        years.setText(CalendarUtil.formatYear(LocalDate.now()), false);
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

    private List<String> createListYearsEvent(List<Event> eventList, List<Expense> expenseList) {
        List<String> expenseYears = mapedExpenseList(expenseList);
        List<String> eventYears = mapedEventList(eventList);
        return eventYears.size() >= expenseList.size() ? eventYears : expenseYears;
    }

    private List<String> mapedExpenseList(List<Expense> expenseList) {
        return  expenseList.stream()
                .map(Expense::getDate)
                .map(LocalDate::getYear)
                .distinct()
                .sorted(Comparator.comparing(Integer::intValue))
                .map(Objects::toString)
                .collect(Collectors.toList());
    }

    private List<String> mapedEventList(List<Event> eventList) {
        return  eventList.stream()
                .map(Event::getEventDate)
                .map(LocalDate::getYear)
                .distinct()
                .sorted(Comparator.comparing(Integer::intValue))
                .map(Objects::toString)
                .collect(Collectors.toList());
    }
}