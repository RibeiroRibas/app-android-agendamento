package br.com.beautystyle.ui.activity;

import static br.com.beautystyle.ui.activity.ContantsActivity.INVALID_POSITION;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_INSERT_EXPENSE;
import static br.com.beautystyle.ui.activity.ContantsActivity.REQUEST_CODE_UPDATE_EXPENSE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_NAME_CATEGORY;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;
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

import androidx.appcompat.app.AppCompatActivity;

import com.example.beautystyle.R;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import br.com.beautystyle.model.entity.Expense;
import br.com.beautystyle.model.enuns.RepeatOrNot;
import br.com.beautystyle.ui.fragment.CalendarViewFragment;
import br.com.beautystyle.ui.fragment.expense.CategoryListFragment;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.MoneyTextWatcher;

public class NewExpenseActivity extends AppCompatActivity {

    private Expense expense = new Expense();
    private int itemPosition;
    private EditText purchaseDate, purchaseValue, categoryEditTxt, expenseDescription;
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
        purchaseDateListener();
        categoryListener();
        checkBoxListener();
        btnSaveExpenseListener();
        setFragmentResultListener();

        onCalendarClickListener();
    }


    private void initWidget() {
        purchaseValue = findViewById(R.id.activity_new_expense_value);
        purchaseValue.addTextChangedListener(new MoneyTextWatcher(purchaseValue));
        purchaseDate = findViewById(R.id.activity_new_expense_date);
        expenseDescription = findViewById(R.id.activity_new_expense_list_view);
        categoryEditTxt = findViewById(R.id.activity_new_expense_category);
        repeat = findViewById(R.id.activity_new_expense_repeat);
        nRepeat = findViewById(R.id.activity_new_expense_do_not_repeat);
    }

    private void loadExpense() {
        Intent data = getIntent();
        if (data.hasExtra(KEY_UPDATE_EXPENSE)) {
            Expense expense = (Expense) data.getSerializableExtra(KEY_UPDATE_EXPENSE);
            itemPosition = data.getIntExtra(KEY_POSITION, INVALID_POSITION);
            FillAllForm(expense);
        } else {
            setPurchaseDate(LocalDate.now());
            nRepeat.setChecked(true);
            expense.setRepeatOrNot(RepeatOrNot.NREPEAT);
        }
    }

    private void FillAllForm(Expense expense) {
        this.expense = expense;
        purchaseDate.setText(CalendarUtil.formatLocalDate(expense.getExpenseDate(), DD_MM_YYYY));
        categoryEditTxt.setText(expense.getCategory());
        expenseDescription.setText(expense.getDescription());
        purchaseValue.setText(CoinUtil.format(expense.getPrice(), REMOVE_SYMBOL));
        setCheckBoxRepeatOrNot();
    }

    private void setCheckBoxRepeatOrNot() {
        if (expense.getRepeatOrNot().equals(RepeatOrNot.REPEAT)) {
            repeat.setChecked(true);
        } else {
            nRepeat.setChecked(true);
        }
    }

    private void setPurchaseDate(LocalDate date) {
            expense.setExpenseDate(date);
            String formatedDate = CalendarUtil.formatLocalDate(date, DD_MM_YYYY);
            purchaseDate.setText(formatedDate);
    }

    private void purchaseDateListener() {
        purchaseDate.setOnClickListener(v -> {
            calendarViewFragment.show(getSupportFragmentManager(), TAG_CALENDAR_VIEW);
        });
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
                expense.setRepeatOrNot(RepeatOrNot.REPEAT);
            }
        });
    }

    private void checkboxNRepeatListener() {
        nRepeat.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                repeat.setChecked(false);
                expense.setRepeatOrNot(RepeatOrNot.NREPEAT);
            }
        }));
    }

    private void btnSaveExpenseListener() {
        Button saveExpense = findViewById(R.id.activity_new_expense_save);
        saveExpense.setOnClickListener(v -> {
            setExpense();
            if (getIntent().hasExtra(KEY_UPDATE_EXPENSE)) {
                setResultAndFinishActivity(REQUEST_CODE_UPDATE_EXPENSE);
            } else {
                setResultAndFinishActivity(REQUEST_CODE_INSERT_EXPENSE);
            }
        });
    }

    private void setExpense() {
        expense.setDescription(expenseDescription.getText().toString());
        String formatedPurchaseValue = formatPurchaseValue();
        expense.setPrice(new BigDecimal(formatedPurchaseValue));
    }

    private String formatPurchaseValue() {
        return CoinUtil.formatPriceSave(Objects.requireNonNull(purchaseValue.getText()).toString());
    }

    private void setResultAndFinishActivity(int resultCode) {
        Intent intent = createIntent();
        setResult(resultCode, intent);
        finish();
    }

    private Intent createIntent() {
        Intent intent = new Intent();
        intent.putExtra(KEY_RESULT_EXPENSE, expense);
        intent.putExtra(KEY_POSITION, itemPosition);
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
            LocalDate selectedDate = LocalDate.of(year, month + 1, dayOfMonth);
            CalendarUtil.selectedDate = selectedDate;
            setPurchaseDate(selectedDate);
            calendarViewFragment.dismiss();
        });
    }

}