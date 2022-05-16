package br.com.beautystyle.ui.fragment.expense;

import static br.com.beautystyle.ui.activity.ContantsActivity.INVALID_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_RESULT_EXPENSE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_EXPENSE;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

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

import br.com.beautystyle.model.entities.Expense;
import br.com.beautystyle.model.enuns.MonthsOfTheYear;
import br.com.beautystyle.repository.ExpenseRepository;
import br.com.beautystyle.repository.ResultsCallBack;
import br.com.beautystyle.ui.activity.NewExpenseActivity;
import br.com.beautystyle.ui.adapter.recyclerview.ExpenseListAdapter;
import br.com.beautystyle.util.CalendarUtil;
import io.reactivex.rxjava3.disposables.Disposable;

public class ExpenseListFragment extends Fragment {

    private AutoCompleteTextView monthsOfTheYear;
    private AutoCompleteTextView years;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private View inflatedView;
    private ExpenseListAdapter adapter;
    private List<Expense> mExpenseList;
    private ExpenseRepository repository;
    private Disposable disposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ExpenseListAdapter(requireActivity());
        repository = new ExpenseRepository(requireActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_list_expense, container, false);

        initWidgets();
        setTextMonthAndYear(LocalDate.now());//default

        setAdapterMonthsOfTheYear();
        setAdapterExpenseList();//recycler view

        observableExpenseList();

        //LISTENERS
        adapterMonthsOfTheYearListener();
        adapterYearsListener();
        adapterExpenseListListener();
        launchNewExpenseActivityListener();

        return inflatedView;
    }

    private void getAllFromApi() {
        repository.getAllFromApi(new ResultsCallBack<List<Expense>>() {
            @Override
            public void onSuccess(List<Expense> expenseList) {
                updateAdapterList(expenseList);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void updateAdapterList(List<Expense> expenseList) {
        mExpenseList = expenseList;
        setAdapterYears(expenseList);
        updateExpenseList();
    }

    private void observableExpenseList() {
        disposable = repository.getAllFromRoom().doOnNext(expenses -> {
            updateAdapterList(expenses);
            getAllFromApi();
        }).subscribe();
    }

    private void initWidgets() {
        monthsOfTheYear = inflatedView.findViewById(R.id.fragment_list_expense_month);
        years = inflatedView.findViewById(R.id.fragment_list_expense_year);
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
                .setPositiveButton("Sim", (dialog, which) -> removeExpense(adapter.getItem(identifier), identifier))
                .setNegativeButton("Não", null)
                .show();
    }

    private void removeExpense(Expense selectedExpense, int identifier) {
        repository.delete(selectedExpense, new ResultsCallBack<Void>() {
            @Override
            public void onSuccess(Void result) {
                adapter.publishResultsRemoved(selectedExpense, identifier);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
    }

    private void setAdapterMonthsOfTheYear() {
        List<String> monthsOfTheYearList = MonthsOfTheYear.getMonthList();
        ArrayAdapter<String> adapterItens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, monthsOfTheYearList);
        monthsOfTheYear.setAdapter(adapterItens);
    }

    private void setAdapterYears(List<Expense> expenseList) {
        List<String> yearList = createListYears(expenseList);
        ArrayAdapter<String> adapterItens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, yearList);
        years.setAdapter(adapterItens);
    }

    public List<String> createListYears(List<Expense> expenseLis) {
        return expenseLis.stream()
                .map(Expense::getDate)
                .map(LocalDate::getYear)
                .distinct()
                .sorted(Comparator.comparing(Integer::intValue))
                .map(Objects::toString)
                .collect(Collectors.toList());
    }

    private void setAdapterExpenseList() {
        RecyclerView expenseList = inflatedView.findViewById(R.id.fragment_list_expense_rv);
        expenseList.setAdapter(adapter);
        registerForContextMenu(expenseList);
    }

    private void adapterMonthsOfTheYearListener() {
        monthsOfTheYear.setOnItemClickListener(((parent, view, monthValue, id) -> {
            CalendarUtil.monthValue = monthValue + 1;
            updateExpenseList();
        }));
    }

    private void updateExpenseList() {
        List<Expense> expensesList = new ArrayList<>(listByDate(CalendarUtil.monthValue, CalendarUtil.year));
        adapter.publishResultsChangedList(expensesList);
    }

    private List<Expense> listByDate(int monthValue, int year) {
        return mExpenseList.stream()
                .filter(expense -> expense.getDate()
                        .getMonthValue() == monthValue
                        && expense.getDate().getYear() == year)
                .collect(Collectors.toList());
    }

    private void adapterYearsListener() {
        years.setOnItemClickListener(((parent, view, position, id) -> {
            CalendarUtil.year = Integer.parseInt(parent.getItemAtPosition(position).toString());
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
                if (result.getResultCode() == 4) {// update
                    int position = intent.getIntExtra(KEY_POSITION, INVALID_POSITION);
                    update(expense, position);
                }
            }
        });
    }

    private void insert(Expense expense) {
        repository.insert(expense, new ResultsCallBack<Expense>() {
            @Override
            public void onSuccess(Expense resultado) {
                setTextMonthAndYear(resultado.getDate());
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

    private void update(Expense expense, int position) {
        repository.update(expense, new ResultsCallBack<Expense>() {
            @Override
            public void onSuccess(Expense result) {
                setTextMonthAndYear(result.getDate());
                adapter.publishResultsChanged(result, position);
            }

            @Override
            public void onError(String erro) {
                showErrorMessage(erro);
            }
        });
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

    private void setTextMonthAndYear(LocalDate date) {
        monthsOfTheYear.setText(CalendarUtil.formatMonth(date), false);
        years.setText(CalendarUtil.formatYear(date), false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
