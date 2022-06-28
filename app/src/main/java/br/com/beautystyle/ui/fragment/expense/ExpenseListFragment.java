package br.com.beautystyle.ui.fragment.expense;

import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_INSERT_EXPENSE;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_UPDATE_EXPENSE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.INVALID_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import javax.inject.Inject;

import br.com.beautystyle.BeautyStyleApplication;
import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.repository.ExpenseRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.ui.activity.NewExpenseActivity;
import br.com.beautystyle.ui.adapter.recyclerview.ExpenseListAdapter;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CreateListsUtil;

public class ExpenseListFragment extends Fragment {

    private AutoCompleteTextView monthsOfTheYear, years;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private View inflatedView;
    private ExpenseListAdapter adapterExpenses;
    @Inject
    ExpenseRepository expenseRepositoty;


    @Override
    public void onAttach(@NonNull Context context) {
        injectFrament();
        super.onAttach(context);
    }

    private void injectFrament() {
        ((BeautyStyleApplication) requireActivity().getApplicationContext())
                .applicationComponent.injectExpenseListFrag(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapterExpenses = new ExpenseListAdapter(requireActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_list_expense, container, false);

        CalendarUtil.selectedDate = LocalDate.now();
        initWidgets();

        setAdapterExpenses(inflatedView);//recycler view
        updateAdapterExpenses();

        //LISTENERS
        adapterMonthsOfTheYearListener();
        adapterYearsListener();
        adapterExpenseListListener();
        newExpenseActivityListener();

        registerActivityResult();
        return inflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapterYears();
        setAdapterMonthsOfTheYear();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        checkRemove(item.getGroupId());
        return super.onContextItemSelected(item);
    }

    public void checkRemove(int adapterPosition) {
        new AlertDialog
                .Builder(requireActivity())
                .setTitle("Removendo o gasto selecionado")
                .setMessage("Tem certeza que deseja remover o item selecionado?")
                .setPositiveButton("Sim", (dialog, which) -> removeExpense(adapterPosition))
                .setNegativeButton("Não", null)
                .show();
    }

    private void removeExpense(int adapterPosition) {
        Expense selectedExpense = adapterExpenses.getItem(adapterPosition);
        expenseRepositoty.deleteOnApi(selectedExpense, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                removeOnRoom(selectedExpense, adapterPosition);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void removeOnRoom(Expense selectedExpense, int adapterPosition) {
        expenseRepositoty.deleteOnRoom(selectedExpense)
                .doOnComplete(() ->
                        adapterExpenses.publishResultsRemoved(selectedExpense, adapterPosition)
                ).subscribe();
    }

    private void initWidgets() {
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
            updateAdapterExpenses();
        }));
    }

    private void adapterYearsListener() {
        years.setOnItemClickListener(((parent, view, position, id) -> {
            CalendarUtil.selectedDate = changeYearValue(parent, position);
            updateAdapterExpenses();
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

    private void updateAdapterExpenses() {
        LocalDate startDate = CalendarUtil.selectedDate.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        getByPeriod(startDate, endDate);
    }

    private void getByPeriod(LocalDate startDate, LocalDate endDate) {
        expenseRepositoty.getByPeriodFromRoom(startDate, endDate)
                .doOnSuccess(expensesFromRoom -> {
                    adapterExpenses.publishResultsChangedList(expensesFromRoom, startDate);
                    setTextMonthAndYear(CalendarUtil.selectedDate);
                    getByPeriodFromApi(startDate, endDate, expensesFromRoom);
                }).subscribe();
    }

    private void getByPeriodFromApi(LocalDate startDate, LocalDate endDate,
                                    List<Expense> expensesFromRoom) {
        expenseRepositoty.getByPeriodFromApi(startDate, endDate,
                new ResultsCallBack<List<Expense>>() {
                    @Override
                    public void onSuccess(List<Expense> expensesFromApi) {
                        updateLocalDatabase(expensesFromApi, expensesFromRoom, startDate);
                    }

                    @Override
                    public void onError(String erro) {

                    }
                });
    }

    private void updateLocalDatabase(List<Expense> expensesFromApi,
                                     List<Expense> expensesFromRoom,
                                     LocalDate startDate) {
        expenseRepositoty.updateLocalDatabase(expensesFromApi, expensesFromRoom,
                new ResultsCallBack<List<Expense>>() {
                    @Override
                    public void onSuccess(List<Expense> expensesFromApi) {
                        adapterExpenses.publishResultsChangedList(expensesFromApi, startDate);
                        setTextMonthAndYear(startDate);
                    }

                    @Override
                    public void onError(String erro) {
                        showErrorMessage(erro);
                    }
                });
    }

    private void setTextMonthAndYear(LocalDate date) {
        monthsOfTheYear.setText(CalendarUtil.formatMonth(date), false);
        years.setText(CalendarUtil.formatLocalDate(date, YYYY), false);
    }

    private void adapterExpenseListListener() {
        adapterExpenses.setOnItemClickListener((expense, position) -> {
            Intent intent = createIntent(expense, position);
            activityResultLauncher.launch(intent);
        });
    }

    private Intent createIntent(Expense expense, int position) {
        return new Intent(requireActivity(), NewExpenseActivity.class)
                .putExtra(KEY_UPDATE_EXPENSE, expense)
                .putExtra(KEY_POSITION, position);
    }

    private void newExpenseActivityListener() {
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
                        isUpdateExpense(result, expense, intent);
                    }
                }
        );
    }

    private void isNewExpense(ActivityResult result, Expense expense) {
        if (result.getResultCode() == REQUEST_CODE_INSERT_EXPENSE) {
            insert(expense);
        }
    }

    private void insert(Expense expense) {
        expenseRepositoty.insertOnApi(expense, new ResultsCallBack<Expense>() {
            @Override
            public void onSuccess(Expense expense) {
                insertOnRoom(expense);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void insertOnRoom(Expense expense) {
        expenseRepositoty.insertOnRoom(expense)
                .doOnSuccess(id -> {
                    expense.setId(id);
                    adapterExpenses.publishResultsInserted(expense);
                }).subscribe();
    }

    private void isUpdateExpense(ActivityResult result, Expense expense, Intent intent) {
        if (result.getResultCode() == REQUEST_CODE_UPDATE_EXPENSE) {
            int position = intent.getIntExtra(KEY_POSITION, INVALID_POSITION);
            update(expense, position);
        }
    }

    private void update(Expense expense, int position) {
        expenseRepositoty.updateOnApi(expense, new ResultsCallBack<Expense>() {
            @Override
            public void onSuccess(Expense expenseFromApi) {
                updateOnRoom(expense, position);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void updateOnRoom(Expense expense, int position) {
        expenseRepositoty.updateOnRoom(expense)
                .doOnComplete(() -> adapterExpenses.publishResultsChanged(expense, position))
                .subscribe();
    }

    private void showErrorMessage(String erro) {
        Toast.makeText(requireActivity(), erro, Toast.LENGTH_LONG).show();
    }


    private void setAdapterMonthsOfTheYear() {
        List<String> monthsOfTheYearList = CreateListsUtil.createMonthsList();
        ArrayAdapter<String> adapterItens = getSimpleAdapterList(monthsOfTheYearList);
        monthsOfTheYear.setAdapter(adapterItens);
    }

    private ArrayAdapter<String> getSimpleAdapterList(List<String> monthsOfTheYearList) {
        return new ArrayAdapter<>(
                requireActivity(), android.R.layout.simple_list_item_1, monthsOfTheYearList
        );
    }

    private void setAdapterYears() {
        expenseRepositoty.getYearsListFromApi(new ResultsCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> result) {
                updateAdapterYears(result);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void updateAdapterYears(List<String> expensesDate) {
        ArrayAdapter<String> adapterItens = getSimpleAdapterList(expensesDate);
        years.setAdapter(adapterItens);
    }
}
