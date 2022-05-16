package br.com.beautystyle.ui.fragment.report;

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
import br.com.beautystyle.model.Report;
import br.com.beautystyle.model.entities.Event;
import br.com.beautystyle.model.entities.Expense;
import br.com.beautystyle.model.enuns.TypeOfReport;
import br.com.beautystyle.repository.EventRepository;
import br.com.beautystyle.repository.ExpenseRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.ui.adapter.recyclerview.ReportListAdapter;
import br.com.beautystyle.util.CoinUtil;

public class ReportFragment extends Fragment {

    private AutoCompleteTextView typeOfReport;
    private ReportViewModel reportViewModel;
    private ReportListAdapter adapter;
    private View inflatedView;
    private List<Event> eventList;
    private List<Expense> expenseList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportViewModel = new ViewModelProvider(requireActivity()).get(ReportViewModel.class);
        getListsFromDatabase();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_report, container, false);
        setAdapterReport();

        //LISTENERS
        TypeOfReportListener();
        fragmentResultListener();

        getEventAndExpenseListObserve();
        reportViewModel.getReportList().observe(getViewLifecycleOwner(), this::updateViews);

        return inflatedView;
    }

    private void getEventAndExpenseListObserve() {
        reportViewModel.getEventList().observe(getViewLifecycleOwner(), eventList ->
            reportViewModel.getExpenseList().observe(getViewLifecycleOwner(), expenseList -> {
                initEventAndExpenseList(eventList, expenseList);
                setAdapterTypeOfReport();
            })
        );
    }

    private void initEventAndExpenseList(List<Event> eventList, List<Expense> expenseList) {
        this.expenseList = expenseList;
        this.eventList = eventList;
    }

    private void getListsFromDatabase() {
        getEventList();
        getExpenseList();
    }

    private void getExpenseList() {
        ExpenseRepository expenseRepository = new ExpenseRepository(requireActivity());
        getExpenseListFromRoom(expenseRepository);
    }

    private void getExpenseListFromRoom(ExpenseRepository expenseRepository) {
        expenseRepository.getAllFromRoom()
                .doOnNext(expenseList->{
                    reportViewModel.addExpenseList(expenseList);
                    getExpenseListFromApi(expenseRepository);
                }).subscribe();
    }

    private void getExpenseListFromApi(ExpenseRepository expenseRepository) {
        expenseRepository.getAllFromApi(new ResultsCallBack<List<Expense>>() {
            @Override
            public void onSuccess(List<Expense> resultado) {
                reportViewModel.addExpenseList(expenseList);
            }

            @Override
            public void onError(String erro) {

            }
        });
    }

    private void getEventList() {
        EventRepository eventRepository = new EventRepository(requireActivity());
        getEventListFromRooom(eventRepository);
    }

    private void getEventListFromRooom(EventRepository eventRepository) {
        eventRepository.getAllFromRoom()
                .doOnSuccess(eventList -> {
                    reportViewModel.addEventList(eventList);
                    getEventListFromApi(eventRepository);
                })
                .subscribe();
    }

    private void getEventListFromApi(EventRepository eventRepository) {
        eventRepository.getAllFromApi(new ResultsCallBack<List<Event>>() {
            @Override
            public void onSuccess(List<Event> resultado) {
                reportViewModel.addEventList(eventList);
            }

            @Override
            public void onError(String erro) {

            }
        });
    }

    private void setAdapterTypeOfReport() {
        List<String> typeOfReportList = TypeOfReport.getTypeOfReportList();
        ArrayAdapter<String> adapteritens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, typeOfReportList);
        typeOfReport.setAdapter(adapteritens);
    }

    private void TypeOfReportListener() {
        typeOfReport = inflatedView.findViewById(R.id.fragment_report_type_of_);
        typeOfReport.setOnItemClickListener((parent, view, position, id) -> checkItemClick(position));
    }

    private void checkItemClick(int position) {
        switch (position) {
            case 0://monthly
                replaceFragment(new MonthlyReportFragment(),createMonthlyReportList(LocalDate.now()));
                break;
            case 1://daily
                replaceFragment(new DailyReportFragment(),createDailyReportList(LocalDate.now()));
                break;
            case 2://by period
                replaceFragment(new PeriodReportFragment());
                adapter.removeItemRange();
                onBindOverView("R$ 0,00", "R$ 0,00", "R$ 0,00");
                break;
        }
    }

    private void replaceFragment(Fragment fragment, List<Report> reportList) {
        replaceFragment(fragment);
        reportViewModel.addReportList(reportList);
    }

    private void replaceFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_report_container, fragment)
                .commit();
    }

    private void setAdapterReport() {
        RecyclerView reportList = inflatedView.findViewById(R.id.fragment_report_rv);
        adapter = new ReportListAdapter(requireActivity().getApplication());
        reportList.setAdapter(adapter);
    }

    private void fragmentResultListener() {
        getChildFragmentManager().setFragmentResultListener(KEY_REPORT, this, (requestKey, result) -> {
            if (result.containsKey(KEY_MONTHLY_REPORT)) {
                resultMonthlyReport(result);
            } else if (result.containsKey(KEY_DAILY_REPORT)) {
                resultDailyReport(result);
            } else if (result.containsKey(KEY_START_DATE)) {
                resultByPeriodReport(result);
            }
        });
    }

    private void resultByPeriodReport(Bundle result) {
        LocalDate startDate = (LocalDate) result.getSerializable(KEY_START_DATE);
        LocalDate endDate = (LocalDate) result.getSerializable(KEY_END_DATE);
        reportViewModel.addReportList(createPeriodReportList(startDate, endDate));
    }

    private void resultDailyReport(Bundle result) {
        LocalDate selectedDate = (LocalDate) result.getSerializable(KEY_DAILY_REPORT);
        reportViewModel.addReportList(createDailyReportList(selectedDate));
    }

    private void resultMonthlyReport(Bundle result) {
        LocalDate date = (LocalDate) result.getSerializable(KEY_MONTHLY_REPORT);
        reportViewModel.addReportList(createMonthlyReportList(date));
    }


    private List<Report> createDailyReportList(LocalDate selectedDate) {
        List<Event> filteredEventList = createDailyEventList(selectedDate);
        List<Expense> filteredExpenseList = createDailyExpenseList(selectedDate);
        return createReportList(filteredEventList, filteredExpenseList);
    }

    private List<Expense> createDailyExpenseList(LocalDate selectedDate) {
        return expenseList.stream()
                .filter(expense -> expense.getDate().equals(selectedDate))
                .collect(Collectors.toList());
    }

    private List<Event> createDailyEventList(LocalDate selectedDate) {
        return eventList.stream()
                .filter(event -> event.getEventDate().equals(selectedDate))
                .collect(Collectors.toList());
    }

    private List<Report> createPeriodReportList(LocalDate startDate, LocalDate endDate) {
        List<Event> filteredEventList = createPeriodEventList(startDate,endDate);
        List<Expense> filteredExpenseList = createPeriodExpenseList(startDate,endDate);
        return createReportList(filteredEventList, filteredExpenseList);
    }

    private List<Expense> createPeriodExpenseList(LocalDate startDate, LocalDate endDate) {
        return expenseList.stream().filter(ex -> !ex.getDate().isBefore(startDate)
                && !ex.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    private List<Event> createPeriodEventList(LocalDate startDate, LocalDate endDate) {
        return eventList.stream()
                .filter(ev -> !ev.getEventDate().isBefore(startDate)
                        && !ev.getEventDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

    private List<Report> createReportList(List<Event> filteredEventList, List<Expense> filteredExpenseList) {
        List<Report> reportList = new ArrayList<>();
        filteredEventList.forEach(ev -> reportList.add(new Report(ev.getEventDate(), ev)));
        filteredExpenseList.forEach(ex -> reportList.add(new Report(ex.getDate(), ex)));
        reportList.sort(Comparator.comparing(Report::getDate));
        return reportList;
    }


    private List<Report> createMonthlyReportList(LocalDate date) {
        List<Event> filteredEventList = createMonthlyEventList(date);
        List<Expense> filteredExpenseList = createMonthlyExpenseList(date);
        return createReportList(filteredEventList, filteredExpenseList);
    }

    private List<Expense> createMonthlyExpenseList(LocalDate date) {
        return expenseList.stream()
                .filter(expense -> expense.getDate().getMonthValue() == date.getMonthValue()
                        && expense.getDate().getYear() == date.getYear())
                .sorted(Comparator.comparing(Expense::getDate))
                .collect(Collectors.toList());
    }

    private List<Event> createMonthlyEventList(LocalDate date) {
        return eventList.stream()
                .filter(event -> event.getEventDate().getMonthValue() == date.getMonthValue()
                        && event.getEventDate().getYear() == date.getYear())
                .sorted(Comparator.comparing(Event::getEventDate)).collect(Collectors.toList());
    }

    private void updateViews(List<Report> filteredList) {
            BigDecimal valueExpense = new BigDecimal(0);
            BigDecimal valueGain = new BigDecimal(0);
            addValues(valueExpense, valueGain, filteredList);
            onBindOverView(CoinUtil.formatBr(valueGain), CoinUtil.formatBr(valueExpense), CoinUtil.formatBr(valueGain.subtract(valueExpense)));
            adapter.publishResultsFilteredList(filteredList);
    }

    private void addValues(BigDecimal valueExpense, BigDecimal valueGain, List<Report> filteredList) {
        for (Report report : filteredList) {
            if (report.getExpense() != null) {
                valueExpense = valueExpense.add(report.getExpense().getPrice());
            } else {
                valueGain = valueGain.add(report.getEvent().getValueEvent());
            }
        }
    }

    private void onBindOverView(String valueGain, String valueExpense, String valueLeft) {
        TextView amount = inflatedView.findViewById(R.id.fragment_report_amount);
        TextView leftValue = inflatedView.findViewById(R.id.fragment_report_value_left);
        TextView expense = inflatedView.findViewById(R.id.fragment_report_expense);
        amount.setText(valueGain);
        expense.setText(valueExpense);
        leftValue.setText(valueLeft);
    }

}