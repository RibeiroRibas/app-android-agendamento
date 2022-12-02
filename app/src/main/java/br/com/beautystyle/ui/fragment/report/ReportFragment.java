package br.com.beautystyle.ui.fragment.report;

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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.ViewModel.ReportViewModel;
import br.com.beautystyle.ViewModel.factory.ReportFactory;
import br.com.beautystyle.model.util.Report;
import br.com.beautystyle.repository.ReportRepository;
import br.com.beautystyle.ui.adapter.recyclerview.ReportListAdapter;
import br.com.beautystyle.util.CoinUtil;

public class ReportFragment extends Fragment {

    private AutoCompleteTextView typeOfReport;
    private ReportListAdapter adapter;
    private View inflatedView;
    @Inject
    ReportRepository repository;
    private ReportViewModel viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        injectFragment();
    }

    private void injectFragment() {
        ((BeautyStyleApplication) requireActivity().getApplicationContext())
                .applicationComponent.injectReportFrag(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ReportFactory factory = new ReportFactory(repository);
        viewModel = new ViewModelProvider(this, factory).get(ReportViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_report, container, false);

        // ADAPTERS // LIVEDATA
        setAdapterReport();
        setAdapterTypeOfReportLiveData();
        reportLiveData();

        //LISTENERS
        typeOfReportListener();

        return inflatedView;
    }

    private void reportLiveData() {
        viewModel.getReport().observe(requireActivity(),
                resource -> {
                    if (resource.isDataNotNull()) {
                        onBindViews(resource.getData());
                        adapter.update(resource.getData());
                    } else {
                        showErrorMessage(resource.getError());
                    }
                });
    }

    private void setAdapterTypeOfReportLiveData() {
        typeOfReport = inflatedView.findViewById(R.id.fragment_report_type_of_);
        viewModel.getTypeOfReport().observe(requireActivity(), typeOfReportList -> {
            ArrayAdapter<String> adapterItems = new ArrayAdapter<>(
                    requireActivity(), android.R.layout.simple_list_item_1, typeOfReportList
            );
            typeOfReport.setAdapter(adapterItems);
        });
    }

    private void typeOfReportListener() {
        typeOfReport.setOnItemClickListener((parent, view, position, id) -> {
            viewModel.clearReport();
            checkItemClick(position);
        });
    }

    private void checkItemClick(int position) {
        switch (position) {
            case 0://monthly
                replaceFragment(new MonthlyReportFragment());
                break;
            case 1://daily
                replaceFragment(new DailyReportFragment());
                break;
            case 2://by period
                replaceFragment(new PeriodReportFragment());
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