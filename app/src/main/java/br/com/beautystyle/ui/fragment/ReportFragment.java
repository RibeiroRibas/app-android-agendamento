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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.ViewModel.ReportViewModel;
import br.com.beautystyle.dao.EventDao;
import br.com.beautystyle.dao.ExpenseDao;
import br.com.beautystyle.model.Event;
import br.com.beautystyle.model.Expense;
import br.com.beautystyle.ui.adapter.recyclerview.ReportListAdapter;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.CreateListsUtil;

public class ReportFragment extends Fragment {

    private AutoCompleteTextView typeOfReport;
    private final EventDao eventDao = new EventDao();
    private final ExpenseDao expenseDao = new ExpenseDao();
    private ReportViewModel reportViewModel;
    private ReportListAdapter adapter;
    private View inflatedView;

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
        List<String> typeOfReportList = CreateListsUtil.createTypeOfReportList();
        ArrayAdapter<String> adapteritens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, typeOfReportList);
        typeOfReport.setAdapter(adapteritens);
    }

    private void setTypeOfReportListener() {
        typeOfReport.setText(typeOfReport.getAdapter().getItem(0).toString(), false);
        typeOfReport.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0://monthly
                    replaceFragment(new MonthlyReportFragment());
                    reportViewModel.add(createMonthlyReportList(LocalDate.now()));
                    break;
                case 1://daily
                    replaceFragment(new DailyReportFragment());
                    reportViewModel.add(createDailyReportList(LocalDate.now()));
                    break;
                case 2://by period
                    replaceFragment(new PeriodReportFragment());
                    adapter.removeItemRange();
                    setTextView("R$ 0,00", "R$ 0,00", "R$ 0,00");
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
                .add(R.id.fragment_report_container, new MonthlyReportFragment())
                .commit();
    }

    private void setAdapterReport() {
        RecyclerView reportList = inflatedView.findViewById(R.id.fragment_report_rv);
        adapter = new ReportListAdapter(requireActivity());
        reportList.setAdapter(adapter);
    }

    private void setFragmentResultListener() {
        getChildFragmentManager().setFragmentResultListener(KEY_REPORT, this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
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
            }

            private List<Object> createPeriodReportList(LocalDate startDate, LocalDate endDate) {
                return CreateListsUtil.createPeriodReportList(startDate, endDate, eventDao.listAll(), expenseDao.listAll());
            }
        });
    }

    private List<Object> createDailyReportList(LocalDate selectedDate) {
        return CreateListsUtil.createDailyReportList(selectedDate, expenseDao.listAll(), eventDao.listAll());
    }

    private void reportLiveDataViewModel() {
        reportViewModel = new ViewModelProvider(requireActivity()).get(ReportViewModel.class);
        reportViewModel.getReportList().observe(requireActivity(), this::updateViews);
        reportViewModel.add(createMonthlyReportList(LocalDate.now()));
    }

    private void updateViews(List<Object> filteredList) {
        BigDecimal valueExpense = new BigDecimal(0);
        BigDecimal valueGain = new BigDecimal(0);
        for (Object obj : filteredList) {
            if (obj instanceof Expense) {
                valueExpense = valueExpense.add(((Expense) obj).getPrice());
            } else if (obj instanceof Event) {
                valueGain = valueGain.add(((Event) obj).getValueEvent());
            }
        }
        setTextView(CoinUtil.formatBr(valueGain), CoinUtil.formatBr(valueExpense), CoinUtil.formatBr(valueGain.subtract(valueExpense)));
        adapter.publishResultsFilteredList(filteredList);
    }

    private void setTextView(String valueGain, String valueExpense, String valueLeft) {
        TextView amount = inflatedView.findViewById(R.id.fragment_report_amount);
        TextView leftValue = inflatedView.findViewById(R.id.fragment_report_value_left);
        TextView expense = inflatedView.findViewById(R.id.fragment_report_expense);
        amount.setText(valueGain);
        expense.setText(valueExpense);
        leftValue.setText(valueLeft);
    }

    private List<Object> createMonthlyReportList(LocalDate date) {
        return CreateListsUtil.createMonthlyReportList(date, expenseDao.listAll(), eventDao.listAll());
    }

}