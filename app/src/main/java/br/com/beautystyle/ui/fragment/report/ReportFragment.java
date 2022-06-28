package br.com.beautystyle.ui.fragment.report;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_DAILY_REPORT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_END_DATE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_REPORT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_START_DATE;
import static br.com.beautystyle.util.ConstantsUtil.DESIRED_FORMAT;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.model.Report;
import br.com.beautystyle.model.entity.Event;
import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.model.enuns.TypeOfReport;
import br.com.beautystyle.repository.EventRepository;
import br.com.beautystyle.repository.ExpenseRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.ui.adapter.recyclerview.ReportListAdapter;
import br.com.beautystyle.util.CoinUtil;

public class ReportFragment extends Fragment {

    private AutoCompleteTextView typeOfReport;
    private ReportListAdapter adapter;
    private View inflatedView;
    private List<Event> eventList;
    private List<Expense> expenseList;
    @Inject
    ExpenseRepository expenseRepository;
    @Inject
    EventRepository eventRepository;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        injectFrament();
    }

    private void injectFrament() {
        ((BeautyStyleApplication) requireActivity().getApplicationContext())
                .applicationComponent.injectReportFrag(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_report, container, false);

        setAdapterReport();

        //LISTENERS
        TypeOfReportListener();

        return inflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapterTypeOfReport();
        fragmentResultListener();
    }

    private void setAdapterTypeOfReport() {
        List<String> typeOfReportList = TypeOfReport.getTypeOfReportList();
        ArrayAdapter<String> adapteritens = new ArrayAdapter<>(
                requireActivity(), android.R.layout.simple_list_item_1, typeOfReportList
        );
        typeOfReport.setAdapter(adapteritens);
    }

    private void TypeOfReportListener() {
        typeOfReport = inflatedView.findViewById(R.id.fragment_report_type_of_);
        typeOfReport.setOnItemClickListener((parent, view, position, id) -> checkItemClick(position));
    }

    private void checkItemClick(int position) {
        onBindOverView("R$ 0,00", "R$ 0,00", "R$ 0,00");
        adapter.removeItemRange();
        switch (position) {
            case 0://monthly
                replaceFragment(new MonthlyReportFragment());
                break;
            case 1://daily
                replaceFragment(new DailyReportFragment());
                break;
            case 2://by period
                replaceFragment(new PeriodReportFragment());
                adapter.removeItemRange();
                break;
        }
    }

    private void replaceFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_report_container, fragment)
                .commit();
    }

    private void setAdapterReport() {
        RecyclerView reportList = inflatedView.findViewById(R.id.fragment_report_rv);
        adapter = new ReportListAdapter(requireActivity());
        reportList.setAdapter(adapter);
    }

    private void fragmentResultListener() {
        getChildFragmentManager().setFragmentResultListener(KEY_REPORT, this, (requestKey, result) -> {
            if (result.containsKey(KEY_DAILY_REPORT)) {
                resultDailyReport(result);
            } else {
                resultMonthlyAndByPeriodReport(result);
            }
        });
    }

    private void resultDailyReport(Bundle result) {
        LocalDate selectedDate = (LocalDate) result.getSerializable(KEY_DAILY_REPORT);
        eventRepository.getEventReportByDateFromApi(selectedDate,
                new ResultsCallBack<List<Report>>() {
                    @Override
                    public void onSuccess(List<Report> reportList) {
                        getExpenseReportByDateFromApi(selectedDate, reportList);
                    }

                    @Override
                    public void onError(String erro) {
                        showErrorMessage(erro);
                    }
                });
    }

    private void getExpenseReportByDateFromApi(LocalDate selectedDate, List<Report> reportList) {
        expenseRepository.getExpenseReportByDateFromApi(selectedDate,
                new ResultsCallBack<List<Report>>() {
                    @Override
                    public void onSuccess(List<Report> expenseReportList) {
                        reportList.addAll(expenseReportList);
                        onBindViews(reportList);
                        adapter.publishResultsChangedList(reportList);
                    }

                    @Override
                    public void onError(String erro) {
                        showErrorMessage(erro);
                    }
                });
    }

    private void resultMonthlyAndByPeriodReport(Bundle result) {
        LocalDate startDate = (LocalDate) result.getSerializable(KEY_START_DATE);
        LocalDate endDate = (LocalDate) result.getSerializable(KEY_END_DATE);
        getEventReportByPeridoFromApi(startDate, endDate);
    }

    private void getEventReportByPeridoFromApi(LocalDate startDate, LocalDate endDate) {
        eventRepository.getReportByPeriodFromApi(startDate, endDate,
                new ResultsCallBack<List<Report>>() {
                    @Override
                    public void onSuccess(List<Report> reportList) {
                        getExpenseReportByPeridoFromApi(startDate, endDate, reportList);
                    }

                    @Override
                    public void onError(String erro) {
                        showErrorMessage(erro);
                    }
                });
    }

    private void getExpenseReportByPeridoFromApi(LocalDate startDate, LocalDate endDate,
                                                 List<Report> reportList) {
        expenseRepository.getReportByPeriodFromApi(startDate, endDate,
                new ResultsCallBack<List<Report>>() {
                    @Override
                    public void onSuccess(List<Report> expenseReport) {
                        reportList.addAll(expenseReport);
                        reportList.sort(Comparator.comparing(Report::getDate));
                        onBindViews(reportList);
                        adapter.publishResultsChangedList(reportList);
                    }

                    @Override
                    public void onError(String erro) {
                        showErrorMessage(erro);
                    }
                });
    }

    private void onBindViews(List<Report> reports) {
        BigDecimal valueExpense = sumValuesExpense(reports);
        BigDecimal valueGain = sumValuesEvent(reports);
        BigDecimal subtractionResult = valueGain.subtract(valueExpense);
        onBindOverView(CoinUtil.format(valueGain, DESIRED_FORMAT),
                CoinUtil.format(valueExpense, DESIRED_FORMAT),
                CoinUtil.format(subtractionResult, DESIRED_FORMAT));
    }

    private BigDecimal sumValuesEvent(List<Report> reports) {
        return reports.stream()
                .map(Report::getEventValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumValuesExpense(List<Report> reports) {
        return reports.stream()
                .map(Report::getExpenseValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void onBindOverView(String valueGain, String valueExpense, String valueLeft) {
        TextView amount = inflatedView.findViewById(R.id.fragment_report_amount);
        TextView leftValue = inflatedView.findViewById(R.id.fragment_report_value_left);
        TextView expense = inflatedView.findViewById(R.id.fragment_report_expense);
        amount.setText(valueGain);
        expense.setText(valueExpense);
        leftValue.setText(valueLeft);
    }

    private void showErrorMessage(String erro) {
        Toast.makeText(requireActivity(), erro, Toast.LENGTH_LONG).show();
    }
}