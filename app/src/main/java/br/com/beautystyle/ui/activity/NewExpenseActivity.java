package br.com.beautystyle.ui.activity;

import static br.com.beautystyle.ui.activity.ContantsActivity.INVALID_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_EDIT_EXPENSE;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_POSITION;
import static br.com.beautystyle.ui.fragment.ConstantFragment.KEY_RESULT_EXPENSE;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.beautystyle.R;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import br.com.beautystyle.ViewModel.CalendarViewModel;
import br.com.beautystyle.model.Category;
import br.com.beautystyle.model.Expense;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.CreateListsUtil;
import me.abhinay.input.CurrencyEditText;
import me.abhinay.input.CurrencySymbols;

public class NewExpenseActivity extends AppCompatActivity {

    private CurrencyEditText value;
    private Expense expense = new Expense();
    private int editItemPosition;
    private EditText purchaseDate;
    private AutoCompleteTextView category;
    private CheckBox nRepeat, repeat;
    private EditText description;
    private CalendarViewModel calendarViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        initWidget();
        loadExpense();
        setCategory();
        calendarObserve();
        setPurchaseDateListener();
        formatInputEditTextTotalPrice();
        saveExpenseListener();
        setRepeatOrNotListener();
    }

    private void initWidget() {
        value = findViewById(R.id.activity_new_spending_price);
        purchaseDate = findViewById(R.id.activity_new_spending_date);
        description = findViewById(R.id.activity_new_spending_description);
        category = findViewById(R.id.activity_new_spending_category);
        repeat = findViewById(R.id.activity_new_expend_repeat);
        nRepeat = findViewById(R.id.activity_new_expend_do_not_repeat);
    }
    private void loadExpense() {
        Intent data = getIntent();
        if(data.hasExtra(KEY_EDIT_EXPENSE)){
            Expense expense = (Expense) data.getSerializableExtra(KEY_EDIT_EXPENSE);
            editItemPosition = data.getIntExtra(KEY_POSITION,INVALID_POSITION);
            FillAllForm(expense);
        }else{
            setPurchaseDateDefault();
            expense.setCategory(Category.OUTROS);
            nRepeat.setChecked(true);
            expense.setRepeatOrNot(Expense.RepeatOrNot.NREPEAT);
        }
    }
    
    private void FillAllForm(Expense expense) {
        this.expense = expense;
        purchaseDate.setText(CalendarUtil.formatDate(expense.getDate()));
        category.setText(expense.getCategory().getDescription());
        description.setText(expense.getDescription());
        value.setText(CoinUtil.formatBrWithoutSymbol(expense.getPrice()));
        if(expense.getRepeatOrNot().equals(Expense.RepeatOrNot.REPEAT)){
            repeat.setChecked(true);
        }else{
            nRepeat.setChecked(true);
        }
    }
    
    private void setPurchaseDateDefault(){
        String todayDate = CalendarUtil.formatDate(LocalDate.now());
        purchaseDate.setText(todayDate);
        expense.setDate(LocalDate.now());
    }
    private void setCategory() {
        configureAdapter();
        setOnItemClickListener();
    }

    private void configureAdapter() {
        List<String> categoriesList = CreateListsUtil.createCategoriesList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoriesList);
        category.setAdapter(adapter);
    }
    
    private void setOnItemClickListener() {
        category.setOnItemClickListener(((parent, view, position, id) -> {
            Category findedCategory = Category.getCategoryByDescription(parent.getItemAtPosition(position).toString());
            expense.setCategory(findedCategory);
        }));
    }

    private void calendarObserve() {
        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);
        calendarViewModel.getDate().observe(this,this::setDate);
    }

    private void setDate(LocalDate date) {
        expense.setDate(date);
        String formatedDate = CalendarUtil.formatDate(date);
        purchaseDate.setText(formatedDate);
    }

    private void setPurchaseDateListener() {
        purchaseDate.setOnClickListener(v -> calendarViewModel.inflateCalendar(this));
    }

    private void formatInputEditTextTotalPrice() {
        value.setCurrency(CurrencySymbols.NONE);
        value.setDelimiter(false);
        value.setSpacing(true);
        value.setDecimals(true);
        value.setSeparator(".");
    }

    private void setRepeatOrNotListener() {
        repeat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                nRepeat.setChecked(false);
                expense.setRepeatOrNot(Expense.RepeatOrNot.REPEAT);
            }
        });
        nRepeat.setOnCheckedChangeListener(((buttonView, isChecked) -> {
            if (buttonView.isChecked()) {
                repeat.setChecked(false);
                expense.setRepeatOrNot(Expense.RepeatOrNot.NREPEAT);
            }
        }));
    }

    private void saveExpenseListener() {
        Button saveExpense = findViewById(R.id.activity_new_spending_save);
        saveExpense.setOnClickListener(v -> {
            setDescriptionAndPrice();
            if(getIntent().hasExtra(KEY_EDIT_EXPENSE)){
                setResultAndFinishActivity(4, editItemPosition);
            }else{
                setResultAndFinishActivity(3, INVALID_POSITION);
            }
        });
    }

    private void setDescriptionAndPrice(){
        expense.setDescription(description.getText().toString());
        String formatedPrice = CoinUtil.formatBrBigDecimal(Objects.requireNonNull(value.getText()).toString());
        expense.setPrice(new BigDecimal(formatedPrice));
    }

    private void setResultAndFinishActivity(int resultCode,int position) {
        Intent intent = new Intent();
        intent.putExtra(KEY_RESULT_EXPENSE, expense);
        intent.putExtra(KEY_POSITION,position);
        setResult(resultCode, intent);
        finish();
    }
}