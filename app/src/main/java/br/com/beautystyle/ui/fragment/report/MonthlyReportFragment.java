package br.com.beautystyle.ui.fragment.report;

import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_END_DATE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_REPORT;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_START_DATE;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.repository.EventRepository;
import br.com.beautystyle.repository.ExpenseRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.util.CreateListsUtil;

public class MonthlyReportFragment extends Fragment {

    private int monthValue, yearValue = -1;
    private AutoCompleteTextView monthsOfTheYear, years;
    @Inject
    ExpenseRepository expenseRepositoty;
    @Inject
    EventRepository eventRepository;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        injectFrament();
    }

    private void injectFrament() {
        ((BeautyStyleApplication) requireActivity().getApplicationContext())
                .applicationComponent.injectMonthlyReportFrag(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_report_monthly, container, false);

        initWidgets(inflatedView);

        adapterMonthOfTheYearListener();
        adapterYearsListener();

        return inflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapterYears();
        setAdapterMonthsOfTheYear();
    }

    private void setAdapterMonthsOfTheYear() {
        List<String> monthsOfTheYearList = CreateListsUtil.createMonthsList();
        ArrayAdapter<String> adapterItens = getSimpleAdapterList(monthsOfTheYearList);
        monthsOfTheYear.setAdapter(adapterItens);
    }

    private void setAdapterYears() {
        expenseRepositoty.getYearsListFromApi(new ResultsCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> yearsListOfEvent) {
                getyearListFromRoom(yearsListOfEvent);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void getyearListFromRoom(List<String> yearsListOfEvent) {
        eventRepository.getYearsListFromApi(new ResultsCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> yearsListOfExpense) {
                updateAdapterYears(yearsListOfEvent, yearsListOfExpense);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void showErrorMessage(String erro) {
        Toast.makeText(requireActivity(), erro, Toast.LENGTH_LONG).show();
    }

    private void updateAdapterYears(List<String> yearsListOfEvent, List<String> yearsListOfExpense) {
        yearsListOfEvent.addAll(yearsListOfExpense);
        List<String> yearsList = yearsListOfEvent.stream().distinct().collect(Collectors.toList());
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
            monthValue = position + 1;
            if (yearValue != -1)
                setFragmentResult();
        });
    }

    private void adapterYearsListener() {
        years.setOnItemClickListener(((parent, view, position, id) -> {
            yearValue = Integer.parseInt(parent.getItemAtPosition(position).toString());
            if (monthValue != -1)
                setFragmentResult();
        }));
    }

    private void setFragmentResult() {
        Bundle result = new Bundle();
        LocalDate startDate = LocalDate.of(yearValue, monthValue, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        result.putSerializable(KEY_START_DATE, startDate);
        result.putSerializable(KEY_END_DATE, endDate);
        getParentFragmentManager().setFragmentResult(KEY_REPORT, result);
    }
}