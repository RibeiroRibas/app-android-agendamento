package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.activity.ContantsActivity.INVALID_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_EXPENSE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_RESULT_EXPENSE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import br.com.beautystyle.ViewModel.ExpenseViewModel;
import br.com.beautystyle.domain.model.Expense;
import br.com.beautystyle.domain.model.MonthsOfTheYear;
import br.com.beautystyle.ui.activity.NewExpenseActivity;
import br.com.beautystyle.ui.adapter.recyclerview.ExpenseListAdapter;
import br.com.beautystyle.util.CalendarUtil;

public class ExpenseListFragment extends Fragment {

    private AutoCompleteTextView monthsOfTheYear;
    private AutoCompleteTextView years;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private int monthValue, year;
    private View inflatedView;
    private final ExpenseViewModel expenseViewModel;
    private ExpenseListAdapter adapter;

    public ExpenseListFragment(ExpenseViewModel expenseViewModel) {
        this.expenseViewModel = expenseViewModel;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ExpenseListAdapter(requireActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_list_expense, container, false);

        setAdapterMonthsOfTheYear();
        setAdapterYears();
        setAdapterExpenses();
        //LISTENERS
        adapterMonthsOfTheYearListener();
        adapterYearsListener();
        adapterExpenseListListener();
        launchNewExpenseActivityListener();

        setPositionDefault();

        return inflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapterYears();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        checkRemove(item.getGroupId());
        return super.onContextItemSelected(item);
    }

    public void checkRemove(int identifier) {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("Removendo o gasto selecionado")
                .setMessage("Tem certeza que deseja remover o item selecionado?")
                .setPositiveButton("Sim", (dialog, which) -> removeExpense(adapter.getItem(identifier),identifier))
                .setNegativeButton("Não", null)
                .show();
    }

    private void removeExpense(Expense selectedExpense,int identifier) {
        expenseViewModel.delete(selectedExpense).doOnComplete(() ->
            adapter.publishResultsRemoved(selectedExpense, identifier)).subscribe();
    }

    private void setAdapterMonthsOfTheYear() {
        monthsOfTheYear = inflatedView.findViewById(R.id.fragment_list_expense_month);
        List<String> monthsOfTheYearList = MonthsOfTheYear.getMonthList();
        ArrayAdapter<String> adapterItens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, monthsOfTheYearList);
        monthsOfTheYear.setAdapter(adapterItens);
    }

    private void setAdapterYears() {
        years = inflatedView.findViewById(R.id.fragment_list_expense_year);
        expenseViewModel.getAll().doOnSuccess(expenseLis -> {
            List<String> yearList = createListYearsExpense(expenseLis);
            ArrayAdapter<String> adapterItens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, yearList);
            years.setAdapter(adapterItens);
        }).subscribe();
    }

    public List<String> createListYearsExpense(List<Expense> expenseLis) {
        return expenseLis.stream()
                .map(Expense::getDate)
                .map(LocalDate::getYear)
                .distinct()
                .sorted(Comparator.comparing(Integer::intValue))
                .map(Objects::toString)
                .collect(Collectors.toList());
    }

    private void setAdapterExpenses() {
        RecyclerView expenseList = inflatedView.findViewById(R.id.fragment_list_expense_rv);
        expenseList.setAdapter(adapter);
        this.monthValue = LocalDate.now().getMonthValue();
        this.year = LocalDate.now().getYear();
        updateExpenseList();
        registerForContextMenu(expenseList);
    }

    private void adapterMonthsOfTheYearListener() {
        monthsOfTheYear.setOnItemClickListener(((parent, view, monthValue, id) -> {
            this.monthValue = monthValue + 1;
            updateExpenseList();
        }));
    }

    private void updateExpenseList() {
        expenseViewModel.getAll().doOnSuccess(expenses -> {
            List<Expense> expensesList = new ArrayList<>(listByDate(monthValue, year, expenses));
            adapter.publishResultsChangedList(expensesList);
        }).subscribe();
    }

    private List<Expense> listByDate(int monthValue, int year, List<Expense> expenseList) {
        return expenseList.stream()
                .filter(expense -> expense.getDate()
                        .getMonthValue() == monthValue
                        && expense.getDate().getYear() == year)
                .collect(Collectors.toList());
    }

    private void adapterYearsListener() {
        years.setOnItemClickListener(((parent, view, position, id) -> {
            this.year = Integer.parseInt(parent.getItemAtPosition(position).toString());
            updateExpenseList();
        }));
    }

    private void adapterExpenseListListener() {
        if (activityResultLauncher == null)
            registerActivityResult();
        adapter.setOnItemClickListener((expense, position) -> {
            Intent intent = new Intent(requireActivity(), NewExpenseActivity.class);
            intent.putExtra(KEY_UPDATE_EXPENSE, expense);
            intent.putExtra(KEY_POSITION, position);
            activityResultLauncher.launch(intent);
        });
    }

    public void registerActivityResult() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            if (intent != null) {
                Expense expense = (Expense) intent.getSerializableExtra(KEY_RESULT_EXPENSE);
                if (result.getResultCode() == 3) { // new
                    insert(expense);
                }
                if (result.getResultCode() == 4) {// edited
                    int position = intent.getIntExtra(KEY_POSITION, INVALID_POSITION);
                    update(expense,position);
                }
            }
        });
    }

    private void insert(Expense expense) {
        expenseViewModel.insert(expense).doOnSuccess(id -> {
            expense.setId(id.intValue());
            adapter.publishResultsNew(expense, monthValue, year);
        }).subscribe();
    }

    private void update(Expense expense, int position) {
        expenseViewModel.update(expense).doOnComplete(() -> adapter.publishResultsChanged(expense, position)).subscribe();
    }

    private void launchNewExpenseActivityListener() {
        ImageButton newSpending = inflatedView.findViewById(R.id.fragment_list_expense_save);
        newSpending.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NewExpenseActivity.class);
            if (activityResultLauncher == null)
                registerActivityResult();
            activityResultLauncher.launch(intent);
        });
    }

    private void setPositionDefault() {
        monthValue = LocalDate.now().getMonthValue();
        year = LocalDate.now().getYear();
        monthsOfTheYear.setText(monthsOfTheYear.getAdapter().getItem(monthValue - 1).toString(), false);
        years.setText(CalendarUtil.formatYear(LocalDate.now()), false);
    }
}
