package br.com.beautystyle.ui.fragment;

import static br.com.beautystyle.ui.activity.ContantsActivity.INVALID_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_EDIT_EXPENSE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_RESULT_EXPENSE;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import com.example.beautystyle.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.beautystyle.dao.ExpenseDao;
import br.com.beautystyle.model.Expense;
import br.com.beautystyle.ui.ListExpenseView;
import br.com.beautystyle.ui.activity.NewExpenseActivity;
import br.com.beautystyle.ui.adapter.recyclerview.ExpenseListAdapter;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CreateListsUtil;

public class ExpenseListFragment extends Fragment {

    private AutoCompleteTextView monthsOfTheYear;
    private AutoCompleteTextView years;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private final ExpenseDao dao = new ExpenseDao();
    private int monthValue, year;
    private View inflatedView;
    private ListExpenseView listExpenseView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listExpenseView = new ListExpenseView(requireActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        inflatedView = inflater.inflate(R.layout.fragment_list_expense, container, false);


        setAdapterMonthsOfTheYear();
        setMonthsOfTheYearListener();
        setAdapterYears();
        setYearsListener();
        setPositionDefault();
        launchNewExpenseActivityListener();
        setAdapterExpenses();
        listExpenseView.setOnItemClickListener(activityResultLauncher);
        registerActivityResult();

        return inflatedView;
    }

    @Override
    public void onResume() {
        super.onResume();
        setAdapterYears();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        listExpenseView.checkRemove(item);
        return super.onContextItemSelected(item);
    }

    private void setAdapterMonthsOfTheYear() {
        monthsOfTheYear = inflatedView.findViewById(R.id.fragment_list_expense_month);
        List<String> monthsOfTheYearList = CreateListsUtil.createMonthList();
        ArrayAdapter<String> adapterItens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, monthsOfTheYearList);
        monthsOfTheYear.setAdapter(adapterItens);
    }

    private void setMonthsOfTheYearListener() {
        monthsOfTheYear.setOnItemClickListener(((parent, view, monthValue, id) -> {
            this.monthValue = monthValue + 1;
            listExpenseView.publishResultsChangedList(monthValue + 1, year);
        }));
    }


    private void setAdapterYears() {
        List<String> itemList = CreateListsUtil.CreateListYearsExpense(dao.listAll());
        years = inflatedView.findViewById(R.id.fragment_list_expense_year);
        ArrayAdapter<String> adapterItens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, itemList);
        years.setAdapter(adapterItens);
    }
    private void setYearsListener() {
        years.setOnItemClickListener(((parent, view, position, id) -> {
            this.year = Integer.parseInt(parent.getItemAtPosition(position).toString());
            listExpenseView.publishResultsChangedList(monthValue, year);
        }));
    }
    private void setPositionDefault() {
        monthValue = LocalDate.now().getMonthValue();
        year = LocalDate.now().getYear();
        monthsOfTheYear.setText(monthsOfTheYear.getAdapter().getItem(monthValue - 1).toString(), false);
        years.setText(CalendarUtil.formatYear(LocalDate.now()), false);
    }

    private void launchNewExpenseActivityListener() {
        ImageButton newSpending = inflatedView.findViewById(R.id.fragment_list_expense_save);
        newSpending.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NewExpenseActivity.class);
            activityResultLauncher.launch(intent);
        });
    }

    private void setAdapterExpenses() {
        RecyclerView expenseList = inflatedView.findViewById(R.id.fragment_list_expense_rv);
        listExpenseView.setAdapter(expenseList);
        registerForContextMenu(expenseList);
    }

    private void registerActivityResult() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            Intent intent = result.getData();
            if (intent != null) {
                Expense expense = (Expense) intent.getSerializableExtra(KEY_RESULT_EXPENSE);
                if (result.getResultCode() == 3) {
                    listExpenseView.save(expense, monthValue, year);
                }
                if (result.getResultCode() == 4) {
                    int position = intent.getIntExtra(KEY_POSITION, INVALID_POSITION);
                    listExpenseView.edit(expense, position);
                }
            }
        });
    }
}
