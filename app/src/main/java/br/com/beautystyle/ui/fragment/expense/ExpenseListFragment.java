package br.com.beautystyle.ui.fragment.expense;

import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_INSERT_EXPENSE;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_UPDATE_EXPENSE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_RESULT_EXPENSE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_EXPENSE;
import static br.com.beautystyle.util.ConstantsUtil.YYYY;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.ViewModel.ExpenseViewModel;
import br.com.beautystyle.ViewModel.factory.ExpenseFactory;
import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.repository.ExpenseRepository;
import br.com.beautystyle.repository.Resource;
import br.com.beautystyle.ui.activity.NewExpenseActivity;
import br.com.beautystyle.ui.adapter.recyclerview.ExpenseListAdapter;
import br.com.beautystyle.util.CalendarUtil;

public class ExpenseListFragment extends Fragment {

    private AutoCompleteTextView monthsOfTheYear, years;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ExpenseListAdapter adapterExpenses;
    @Inject
    ExpenseRepository expenseRepository;
    private ExpenseViewModel expenseViewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        injectFragment();
        super.onAttach(context);
    }

    private void injectFragment() {
        ((BeautyStyleApplication) requireActivity().getApplicationContext())
                .applicationComponent.injectExpenseListFrag(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapterExpenses = new ExpenseListAdapter(requireActivity());
        ExpenseFactory factory = new ExpenseFactory(expenseRepository);
        expenseViewModel = new ViewModelProvider(requireActivity(), factory)
                .get(ExpenseViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_list_expense, container, false);

        initWidgets(inflatedView);

        // ADAPTER
        setAdapterExpenses(inflatedView); // recycler view

        // ADAPTERS // LIVEDATA
        updateAdapterExpensesLiveData();
        setAdapterMonthsOfTheYearLiveData();

        // LISTENERS
        adapterMonthsOfTheYearListener();
        adapterYearsListener();
        adapterExpenseListListener();
        newExpenseActivityListener(inflatedView);

        registerActivityResult();

        return inflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // ADAPTER // LIVEDATA
        setAdapterYearsLiveData();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        checkDelete(item.getGroupId());
        return super.onContextItemSelected(item);
    }

    public void checkDelete(int adapterPosition) {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("Removendo o item selecionado")
                .setMessage("Tem certeza que deseja remover o item selecionado?")
                .setPositiveButton("Sim", (dialog, which) -> delete(adapterPosition))
                .setNegativeButton("Não", null)
                .show();
    }

    private void delete(int adapterPosition) {
        Expense expense = adapterExpenses.getItem(adapterPosition);
        expenseViewModel.delete(expense).observe(requireActivity(), resource -> {
            if (resource.isErrorNotNull()) {
                showErrorMessage(resource.getError());
            } else {
                adapterExpenses.publishResultsRemoved(adapterPosition);
            }
        });
    }


    private void initWidgets(View inflatedView) {
        monthsOfTheYear = inflatedView.findViewById(R.id.fragment_list_expense_month);
        years = inflatedView.findViewById(R.id.fragment_list_expense_year);
    }

    private void setAdapterExpenses(View inflatedView) {
        RecyclerView expenseList = inflatedView.findViewById(R.id.fragment_list_expense_rv);
        expenseList.setAdapter(adapterExpenses);
        registerForContextMenu(expenseList);
    }

    private void adapterMonthsOfTheYearListener() {
        monthsOfTheYear.setOnItemClickListener(((parent, view, monthValue, id) -> {
            CalendarUtil.selectedDate = CalendarUtil.selectedDate.withMonth(monthValue + 1);
            updateAdapterExpensesLiveData();
        }));
    }

    private void adapterYearsListener() {
        years.setOnItemClickListener(((parent, view, position, id) -> {
            CalendarUtil.selectedDate = changeYearValue(parent, position);
            updateAdapterExpensesLiveData();
        }));
    }

    private LocalDate changeYearValue(AdapterView<?> parent, int position) {
        return CalendarUtil.selectedDate.withYear(
                Integer.parseInt(getYearValue(parent, position))
        );
    }

    private String getYearValue(AdapterView<?> parent, int position) {
        return parent.getItemAtPosition(position).toString();
    }

    private void updateAdapterExpensesLiveData() {
        expenseViewModel.getByPeriodLiveData()
                .observe(requireActivity(), this::checkResourceValue);
    }

    private void checkResourceValue(Resource<List<Expense>> resource) {
        if (resource.isErrorNotNull()) {
            showErrorMessage(resource.getError());
        } else {
            adapterExpenses.publishResultsChangedList(resource.getData());
            setTextMonthAndYear(CalendarUtil.selectedDate);
        }
    }

    private void setTextMonthAndYear(LocalDate date) {
        monthsOfTheYear.setText(CalendarUtil.formatMonth(date), false);
        years.setText(CalendarUtil.formatLocalDate(date, YYYY), false);
    }

    private void adapterExpenseListListener() {
        adapterExpenses.setOnItemClickListener((expense, position) -> {
            Intent intent = getIntent(expense);
            activityResultLauncher.launch(intent);
        });
    }

    private Intent getIntent(Expense expense) {
        return new Intent(requireActivity(), NewExpenseActivity.class)
                .putExtra(KEY_UPDATE_EXPENSE, expense);
    }

    private void newExpenseActivityListener(View inflatedView) {
        ImageButton newSpending = inflatedView.findViewById(R.id.fragment_list_expense_save);
        newSpending.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NewExpenseActivity.class);
            activityResultLauncher.launch(intent);
        });
    }

    public void registerActivityResult() {
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    Intent intent = result.getData();
                    if (intent != null) {
                        Expense expense = (Expense) intent.getSerializableExtra(KEY_RESULT_EXPENSE);
                        isNewExpense(result, expense);
                        isUpdateExpense(result, expense);
                    }
                }
        );
    }

    private void isNewExpense(ActivityResult result, Expense expense) {
        if (result.getResultCode() == REQUEST_CODE_INSERT_EXPENSE) {
            expenseViewModel.insert(expense)
                    .observe(requireActivity(), this::checkResourceValues);
        }
    }

    private void isUpdateExpense(ActivityResult result, Expense expense) {
        if (result.getResultCode() == REQUEST_CODE_UPDATE_EXPENSE) {
            expenseViewModel.update(expense)
                    .observe(requireActivity(), this::checkResourceValues);
        }
    }

    private void checkResourceValues(Resource<Expense> resource) {
        if (resource.isErrorNotNull()) {
            showErrorMessage(resource.getError());
        } else {
            updateAdapterExpensesLiveData();
        }
    }

    private void showErrorMessage(String error) {
        Toast.makeText(requireActivity(), error, Toast.LENGTH_LONG).show();
    }


    private void setAdapterMonthsOfTheYearLiveData() {
        expenseViewModel.getMonthsOfTheYearList().observe(requireActivity(),monthsOfTheYearList->{
            ArrayAdapter<String> adapterItems = getSimpleAdapterList(monthsOfTheYearList);
            monthsOfTheYear.setAdapter(adapterItems);
        });
    }

    private ArrayAdapter<String> getSimpleAdapterList(List<String> monthsOfTheYearList) {
        return new ArrayAdapter<>(
                requireActivity(), android.R.layout.simple_list_item_1, monthsOfTheYearList
        );
    }

    private void setAdapterYearsLiveData() {
        expenseViewModel.getYearsListLiveData().observe(requireActivity(), resource -> {
            if (resource.isErrorNotNull()) {
                showErrorMessage(resource.getError());
            } else {
                updateAdapterYears(resource.getData());
            }
        });
    }

    private void updateAdapterYears(List<String> expensesDate) {
        ArrayAdapter<String> adapterItems = getSimpleAdapterList(expensesDate);
        years.setAdapter(adapterItems);
    }
}
