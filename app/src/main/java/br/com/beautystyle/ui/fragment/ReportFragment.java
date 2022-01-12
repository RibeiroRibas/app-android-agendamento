package br.com.beautystyle.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;
import com.github.mikephil.charting.charts.PieChart;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.dao.EventDao;
import br.com.beautystyle.dao.ExpenseDao;
import br.com.beautystyle.ui.adapter.recyclerview.ReportListAdapter;
import br.com.beautystyle.util.CreateListsUtil;

public class ReportFragment extends Fragment {

    private AutoCompleteTextView typeOfReport;
    private EventDao eventDao = new EventDao();
    private ExpenseDao expenseDao = new ExpenseDao();

    public ReportFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_report, container, false);

        beginMonthlyReportFragment();
        setAdapterTypeOfReport(inflatedView);
        setTypeOfReportListener();
        setAdapterReport(inflatedView);

        return inflatedView;
    }

    private void setAdapterReport(View inflatedView) {
        RecyclerView reportList = inflatedView.findViewById(R.id.fragment_report_rv);
        List<Object> createdReportList = CreateListsUtil.createMonthlyReportList(LocalDate.now(), expenseDao.listAll(), eventDao.listAll());
        ReportListAdapter adapter = new ReportListAdapter(createdReportList, requireActivity());
        reportList.setAdapter(adapter);
        adapter.publishResultsFilteredList(createdReportList);
    }

    private void setTypeOfReportListener() {
        typeOfReport.setText(typeOfReport.getAdapter().getItem(0).toString(), false);
        typeOfReport.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0://monthly
                    replaceMonthlyReportFragment();
                    break;
                case 1://daily
                    replaceDailyReportFragment();
                    break;
                case 2://weekly
                    replaceWeeklyReportFragment();
                    break;
                case 3://annual
                    replaceAnnualReportFragment();
                    break;
            }
        });
    }

    private void beginMonthlyReportFragment() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_report_container, new MonthlyReportFragment())
                .commit();
    }

    private void setAdapterTypeOfReport(View inflateView) {
        typeOfReport = inflateView.findViewById(R.id.fragment_report_type_of_);
        List<String> typeOfReportList = CreateListsUtil.createTypeOfReportList();
        ArrayAdapter<String> adapteritens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, typeOfReportList);
        typeOfReport.setAdapter(adapteritens);
    }

    private void replaceAnnualReportFragment() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_report_container, new AnnualReportFragment())
                .commit();
    }

    private void replaceWeeklyReportFragment() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_report_container, new WeekReportFragment())
                .commit();
    }

    private void replaceDailyReportFragment() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_report_container, new DailyReportFragment())
                .commit();
    }

    private void replaceMonthlyReportFragment() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_report_container, new MonthlyReportFragment())
                .commit();
    }

}