package br.com.beautystyle.ui.fragment.report;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.util.List;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.ViewModel.ReportViewModel;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CreateListsUtil;

public class MonthlyReportFragment extends Fragment {

    private AutoCompleteTextView monthsOfTheYear, years;
    private ReportViewModel viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        injectFragment();
    }

    private void injectFragment() {
        ((BeautyStyleApplication) requireActivity().getApplicationContext())
                .applicationComponent.injectMonthlyReportFrag(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireParentFragment()).get(ReportViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_report_monthly, container, false);

        initWidgets(inflatedView);

        setAdapterMonthsOfTheYearLiveData();
        setAdapterYearsLiveData();

        adapterMonthOfTheYearListener();
        adapterYearsListener();

        return inflatedView;
    }

    private void setAdapterMonthsOfTheYearLiveData() {
        viewModel.getMonthsOfTheYearLiveData().observe(requireActivity(), monthsOfTheYearList -> {
            ArrayAdapter<String> adapterItens = getSimpleAdapterList(monthsOfTheYearList);
            monthsOfTheYear.setAdapter(adapterItens);
        });
    }

    private void setAdapterYearsLiveData() {
        viewModel.getYearsListLiveData().observe(requireActivity(), resource -> {
            if (resource.isDataNotNull()) {
                updateAdapterYears(resource.getData());
            } else {
                showErrorMessage(resource.getError());
            }
        });
    }

    private void showErrorMessage(String erro) {
        Toast.makeText(requireActivity(), erro, Toast.LENGTH_LONG).show();
    }

    private void updateAdapterYears(List<String> yearsList) {
        ArrayAdapter<String> adapterItens = getSimpleAdapterList(yearsList);
        years.setAdapter(adapterItens);
    }

    private ArrayAdapter<String> getSimpleAdapterList(List<String> monthsOfTheYearList) {
        return new ArrayAdapter<>(
                requireActivity(), android.R.layout.simple_list_item_1, monthsOfTheYearList
        );
    }

    private void initWidgets(View inflatedView) {
        years = inflatedView.findViewById(R.id.fragment_report_monthly_year);
        monthsOfTheYear = inflatedView.findViewById(R.id.fragment_report_monthly_month);
    }

    private void adapterMonthOfTheYearListener() {
        monthsOfTheYear.setOnItemClickListener((parent, view, position, id) -> {
            String yearValue = years.getText().toString();
            String month = monthsOfTheYear.getText().toString();
            int monthValue = getMonthValue(month);
            if (!yearValue.isEmpty()) {
                CalendarUtil.selectedDate = LocalDate.of(Integer.parseInt(yearValue),monthValue, 1);
                viewModel.addMonthlyReport();
            }
        });
    }

    private int getMonthValue(String month) {
        List<String> months = CreateListsUtil.getMonths();
        for(int i =0; i<months.size();i++){
            if(months.get(i).equals(month)){
                return i+1;
            }
        }
        return 0;
    }

    private void adapterYearsListener() {
        years.setOnItemClickListener(((parent, view, position, id) -> {
            String month = monthsOfTheYear.getText().toString();
            int monthValue = getMonthValue(month);
            int yearValue = Integer.parseInt(years.getText().toString());
            if (!month.isEmpty()) {
                CalendarUtil.selectedDate = LocalDate.of(yearValue, monthValue, 1);
                viewModel.addMonthlyReport();
            }
        }));
    }
}