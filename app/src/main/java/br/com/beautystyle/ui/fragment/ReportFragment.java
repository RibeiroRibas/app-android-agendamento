package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_DAILY_REPORT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_END_DATE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_MONTHLY_REPORT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_REPORT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_START_DATE;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import br.com.beautystyle.ViewModel.ReportViewModel;
import br.com.beautystyle.domain.model.Event;
import br.com.beautystyle.domain.model.Expense;
import br.com.beautystyle.domain.model.Report;
import br.com.beautystyle.domain.model.TypeOfReport;
import br.com.beautystyle.ui.adapter.recyclerview.ReportListAdapter;
import br.com.beautystyle.util.CoinUtil;

public class ReportFragment extends Fragment {

    private AutoCompleteTextView typeOfReport;
    private ReportViewModel reportViewModel;
    private ReportListAdapter adapter;
    private View inflatedView;
    private final List<Event> eventList;
    private final List<Expense> expenseList;

    public ReportFragment(List<Event> eventList, List<Expense> expenseList) {
        this.eventList = new ArrayList<>(eventList);
        this.expenseList = new ArrayList<>(expenseList);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_report, container, false);

        setAdapterTypeOfReport();
        setTypeOfReportListener();
        beginMonthlyReportFragment();
        setAdapterReport();
        setFragmentResultListener();
        reportLiveDataViewModel();

        return inflatedView;
    }

    private void setAdapterTypeOfReport() {
        typeOfReport = inflatedView.findViewById(R.id.fragment_report_type_of_);
        List<String> typeOfReportList = TypeOfReport.getTypeOfReportList();
        ArrayAdapter<String> adapteritens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, typeOfReportList);
        typeOfReport.setAdapter(adapteritens);
    }

    private void setTypeOfReportListener() {
        typeOfReport.setText(typeOfReport.getAdapter().getItem(0).toString(), false);
        typeOfReport.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0://monthly
                    replaceFragment(new MonthlyReportFragment(eventList, expenseList));
                    reportViewModel.add(createMonthlyReportList(LocalDate.now()));
                    break;
                case 1://daily
                    replaceFragment(new DailyReportFragment());
                    reportViewModel.add(createDailyReportList(LocalDate.now()));
                    break;
                case 2://by period
                    replaceFragment(new PeriodReportFragment());
                    adapter.removeItemRange();
                    onBindDataReport("R$ 0,00", "R$ 0,00", "R$ 0,00");
                    break;
            }
        });
    }

    private void replaceFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_report_container, fragment)
                .commit();
    }

    private void beginMonthlyReportFragment() {
        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_report_container, new MonthlyReportFragment(eventList, expenseList))
                .commit();
    }

    private void setAdapterReport() {
        RecyclerView reportList = inflatedView.findViewById(R.id.fragment_report_rv);
        adapter = new ReportListAdapter(requireActivity().getApplication());
        reportList.setAdapter(adapter);
    }

    private void setFragmentResultListener() {
        getChildFragmentManager().setFragmentResultListener(KEY_REPORT, this, (requestKey, result) -> {
            if (result.containsKey(KEY_MONTHLY_REPORT)) {
                LocalDate date = (LocalDate) result.getSerializable(KEY_MONTHLY_REPORT);
                reportViewModel.add(createMonthlyReportList(date));
            } else if (result.containsKey(KEY_DAILY_REPORT)) {
                LocalDate selectedDate = (LocalDate) result.getSerializable(KEY_DAILY_REPORT);
                reportViewModel.add(createDailyReportList(selectedDate));
            } else if (result.containsKey(KEY_START_DATE)) {
                LocalDate startDate = (LocalDate) result.getSerializable(KEY_START_DATE);
                LocalDate endDate = (LocalDate) result.getSerializable(KEY_END_DATE);
                reportViewModel.add(createPeriodReportList(startDate, endDate));
            }
        });
    }

    private List<Report> createDailyReportList(LocalDate selectedDate) {

        List<Event> filteredEventList = eventList.stream()
                .filter(event -> event.getEventDate().equals(selectedDate))
                .collect(Collectors.toList());

        List<Expense> filteredExpenseList = expenseList.stream()
                .filter(expense -> expense.getDate().equals(selectedDate))
                .collect(Collectors.toList());

        return createReportList(filteredEventList,filteredExpenseList);
    }

    private List<Report> createPeriodReportList(LocalDate startDate, LocalDate endDate) {

        List<Event> filteredEventList = eventList.stream()
                .filter(ev -> !ev.getEventDate().isBefore(startDate)
                        && !ev.getEventDate().isAfter(endDate))
                .collect(Collectors.toList());

        List<Expense> filteredExpenseList = expenseList.stream().filter(ex -> !ex.getDate().isBefore(startDate)
                && !ex.getDate().isAfter(endDate))
                .collect(Collectors.toList());

        return createReportList(filteredEventList,filteredExpenseList);
    }

    private List<Report> createReportList(List<Event> filteredEventList, List<Expense> filteredExpenseList) {
        List<Report> reportList = new ArrayList<>();
        for (Event ev : filteredEventList) {
            reportList.add(new Report(ev.getEventDate(),ev));
        }

        for (Expense ex : filteredExpenseList) {
            reportList.add(new Report(ex.getDate(),ex));
        }
        reportList.sort(Comparator.comparing(Report::getDate));
        return reportList;
    }

    private void reportLiveDataViewModel() {
        reportViewModel = new ViewModelProvider(requireActivity()).get(ReportViewModel.class);
        reportViewModel.getReportList().observe(requireActivity(), this::updateViews);
        reportViewModel.add(createMonthlyReportList(LocalDate.now()));
    }

    private List<Report> createMonthlyReportList(LocalDate date) {

        List<Event> filteredEventList = eventList.stream()
                .filter(event -> event.getEventDate().getMonthValue() == date.getMonthValue()
                        && event.getEventDate().getYear() == date.getYear())
                .sorted(Comparator.comparing(Event::getEventDate)).collect(Collectors.toList());

        List<Expense> filteredExpenseList = expenseList.stream()
                .filter(expense -> expense.getDate().getMonthValue() == date.getMonthValue()
                        && expense.getDate().getYear() == date.getYear())
                .sorted(Comparator.comparing(Expense::getDate))
                .collect(Collectors.toList());

        return createReportList(filteredEventList,filteredExpenseList);
    }

    private void updateViews(List<Report> filteredList) {
        BigDecimal valueExpense = new BigDecimal(0);
        BigDecimal valueGain = new BigDecimal(0);
        for (Report report : filteredList) {
            if (report.getExpense()!=null) {
                valueExpense = valueExpense.add(report.getExpense().getPrice());
            } else {
                valueGain = valueGain.add(report.getEvent().getValueEvent());
            }
        }
        onBindDataReport(CoinUtil.formatBr(valueGain), CoinUtil.formatBr(valueExpense), CoinUtil.formatBr(valueGain.subtract(valueExpense)));
        adapter.publishResultsFilteredList(filteredList);
    }

    private void onBindDataReport(String valueGain, String valueExpense, String valueLeft) {
        TextView amount = inflatedView.findViewById(R.id.fragment_report_amount);
        TextView leftValue = inflatedView.findViewById(R.id.fragment_report_value_left);
        TextView expense = inflatedView.findViewById(R.id.fragment_report_expense);
        amount.setText(valueGain);
        expense.setText(valueExpense);
        leftValue.setText(valueLeft);
    }

}