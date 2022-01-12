package br.com.beautystyle.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
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
import br.com.beautystyle.model.Expenses;
import br.com.beautystyle.ui.activity.NewExpenseActivity;
import br.com.beautystyle.ui.adapter.recyclerview.ExpenseListAdapter;
import br.com.beautystyle.util.CreateListsUtil;

public class ExpenseListFragment extends Fragment {

    private AutoCompleteTextView monthsOfTheYear;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ExpenseListAdapter adapter;
    private final ExpenseDao dao = new ExpenseDao();
    private int monthValue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View inflatedView = inflater.inflate(R.layout.fragment_list_expense, container, false);

        registerActivityResult();
        setAdapterMonthsOfTheYear(inflatedView);
        setAdapterExpenses(inflatedView);
        setMonthsOfTheYearListener();
        launchNewExpenseActivityListener(inflatedView);

        return inflatedView;
    }

    private void launchNewExpenseActivityListener(View inflatedView) {
        ImageButton newSpending = inflatedView.findViewById(R.id.fragment_list_expense_save);
        newSpending.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NewExpenseActivity.class);
            activityResultLauncher.launch(intent);
        });
    }

    private void setAdapterExpenses(View inflatedView) {
        RecyclerView expenseList = inflatedView.findViewById(R.id.fragment_list_expense_rv);
        List<Expenses> filteredList = dao.listByMonth(LocalDate.now().getMonthValue());
        adapter = new ExpenseListAdapter(filteredList, requireActivity());
        expenseList.setAdapter(adapter);
        adapter.publishResultsFilteredList(filteredList);
    }

    private void setMonthsOfTheYearListener() {
       setPositionDefault();
        monthsOfTheYear.setOnItemClickListener(((parent, view, monthValue, id) -> {
            this.monthValue = monthValue+1;
            List<Expenses> expensesList = new ArrayList<>(dao.listByMonth(monthValue + 1));
            adapter.publishResultsFilteredList(expensesList);
        }));
    }

    private void setPositionDefault() {
        monthValue = LocalDate.now().getMonthValue()-1;
        monthsOfTheYear.setText(monthsOfTheYear.getAdapter().getItem(monthValue).toString(),false);
    }

    private void setAdapterMonthsOfTheYear(View inflatedView) {
        monthsOfTheYear = inflatedView.findViewById(R.id.fragment_list_expense_month);
        List<String> monthsOfTheYearList = CreateListsUtil.createMonthList();
        ArrayAdapter<String> adapterItens = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, monthsOfTheYearList);
        monthsOfTheYear.setAdapter(adapterItens);
    }

    private void registerActivityResult() {
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == 1) {
                Intent intent = result.getData();
                if (intent != null) {
                    Expenses expense = saveExpense(intent);
                    adapter.publishResultsNew(expense,monthValue);
                }
            }
        });
    }

    private Expenses saveExpense(Intent intent) {
        Expenses expense = (Expenses) intent.getSerializableExtra("result");;
        dao.save(expense);
        return expense;
    }
}