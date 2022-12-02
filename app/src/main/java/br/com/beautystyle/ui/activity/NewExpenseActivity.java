package br.com.beautystyle.ui.activity;

import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_INSERT_EXPENSE;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_UPDATE_EXPENSE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_NAME_CATEGORY;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_RESULT_CATEGORY;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_RESULT_EXPENSE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_UPDATE_EXPENSE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.TAG_CALENDAR_VIEW;
import static br.com.beautystyle.util.ConstantsUtil.DD_MM_YYYY;
import static br.com.beautystyle.util.ConstantsUtil.REMOVE_SYMBOL;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.beautystyle.R;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.ui.fragment.CalendarViewFragment;
import br.com.beautystyle.ui.fragment.expense.CategoryListFragment;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.MoneyTextWatcher;

public class NewExpenseActivity extends AppCompatActivity {

    private Expense expense = new Expense();
    private EditText purchaseDate, expenseValue, categoryEditTxt, expenseDescription;
    private CheckBox nRepeat, repeat;
    private final CalendarViewFragment calendarViewFragment = new CalendarViewFragment();
    private final static String TAG_EXPENSE_LIST = "expenseList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        initWidget();
        loadExpense();

        // LISTENER
        expenseDateListener();
        categoryListener();
        checkBoxListener();
        btnSaveExpenseListener();
        setFragmentResultListener();

        onCalendarClickListener();
    }


    private void initWidget() {
        expenseValue = findViewById(R.id.activity_new_expense_value);
        expenseValue.addTextChangedListener(new MoneyTextWatcher(expenseValue));
        purchaseDate = findViewById(R.id.activity_new_expense_date);
        expenseDescription = findViewById(R.id.activity_new_expense_list_view);
        categoryEditTxt = findViewById(R.id.activity_new_expense_category);
        repeat = findViewById(R.id.activity_new_expense_repeat);
        nRepeat = findViewById(R.id.activity_new_expense_do_not_repeat);
    }

    private void loadExpense() {
        Intent data = getIntent();
        if (isUpdateExpense(data)) {
            Expense expense = (Expense) data.getSerializableExtra(KEY_UPDATE_EXPENSE);
            FillAllForm(expense);
        } else { // is new event mode
            setExpenseDate();
            nRepeat.setChecked(true);
            expense.setRepeat(false);
        }
    }

    private boolean isUpdateExpense(Intent data) {
        return data.hasExtra(KEY_UPDATE_EXPENSE);
    }

    private void FillAllForm(Expense expense) {
        this.expense = expense;
        purchaseDate.setText(CalendarUtil.formatLocalDate(expense.getExpenseDate(), DD_MM_YYYY));
        categoryEditTxt.setText(expense.getCategory());
        expenseDescription.setText(expense.getDescription());
        expenseValue.setText(CoinUtil.format(expense.getValue(), REMOVE_SYMBOL));
        setCheckBoxRepeatOrNot();
    }

    private void setCheckBoxRepeatOrNot() {
        if (expense.isRepeat()) {
            repeat.setChecked(true);
        } else {
            nRepeat.setChecked(true);
        }
    }

    private void setExpenseDate() {
        expense.setExpenseDate(CalendarUtil.selectedDate);
        String formattedDate = CalendarUtil.formatLocalDate(CalendarUtil.selectedDate, DD_MM_YYYY);
        purchaseDate.setText(formattedDate);
    }

    private void expenseDateListener() {
        purchaseDate.setOnClickListener(v ->
                calendarViewFragment.show(getSupportFragmentManager(), TAG_CALENDAR_VIEW));
    }

    private void categoryListener() {
        categoryEditTxt.setOnClickListener(v -> {
            CategoryListFragment categoryListFragment = new CategoryListFragment();
            categoryListFragment.show(getSupportFragmentManager(), TAG_EXPENSE_LIST);
        });
    }

    private void checkBoxListener() {
        checkBoxRepeatListener();
        checkboxNRepeatListener();
    }

    private void checkBoxRepeatListener() {
        repeat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                nRepeat.setChecked(false);
                expense.setRepeat(true);
            }
        });
    }

    private void checkboxNRepeatListener() {
        nRepeat.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                repeat.setChecked(false);
                expense.setRepeat(false);
            }
        }));
    }

    private void btnSaveExpenseListener() {
        Button saveExpense = findViewById(R.id.activity_new_expense_save);
        saveExpense.setOnClickListener(v -> {
            if (checkRequiredField()) {
                categoryEditTxt.setBackgroundResource(R.drawable.custom_invalid_input);
                requiredFieldsAlertDialog();
            } else {
                categoryEditTxt.setBackgroundResource(R.drawable.custom_default_input);
                setExpense();
                checkIntent();
            }
        });
    }

    private void checkIntent() {
        if (isUpdateExpense(getIntent())) {
            setResultAndFinishActivity(REQUEST_CODE_UPDATE_EXPENSE);
        } else {
            setResultAndFinishActivity(REQUEST_CODE_INSERT_EXPENSE);
        }
    }

    public void requiredFieldsAlertDialog() {
        new AlertDialog
                .Builder(this)
                .setTitle("O Campo Categoria é obrigatórios")
                .setPositiveButton("Ok", null)
                .show();
    }

    private boolean checkRequiredField() {
        return categoryEditTxt.getText().toString().isEmpty();
    }

    private void setExpense() {
        expense.setDescription(expenseDescription.getText().toString());
        String formattedPurchaseValue = formatExpenseValue();
        expense.setValue(new BigDecimal(formattedPurchaseValue));
    }

    private String formatExpenseValue() {
        return CoinUtil.formatPriceSave(Objects.requireNonNull(expenseValue.getText()).toString());
    }

    private void setResultAndFinishActivity(int resultCode) {
        Intent intent = newIntent();
        setResult(resultCode, intent);
        finish();
    }

    private Intent newIntent() {
        Intent intent = new Intent();
        intent.putExtra(KEY_RESULT_EXPENSE, expense);
        return intent;
    }

    private void setFragmentResultListener() {
        getSupportFragmentManager().setFragmentResultListener(KEY_RESULT_CATEGORY,
                this, (requestKey, result) -> {
                    if (result.containsKey(KEY_NAME_CATEGORY)) {
                        String categoryName = result.getString(KEY_NAME_CATEGORY);
                        expense.setCategory(categoryName);
                        categoryEditTxt.setText(categoryName);
                    }
                }
        );
    }

    private void onCalendarClickListener() {
        calendarViewFragment.setOnCalendarClickListener((view, year, month, dayOfMonth) -> {
            CalendarUtil.selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
            setExpenseDate();
            calendarViewFragment.dismiss();
        });
    }

}