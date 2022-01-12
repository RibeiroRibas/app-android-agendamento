package br.com.beautystyle.ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;

import com.example.beautystyle.R;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import br.com.beautystyle.model.Category;
import br.com.beautystyle.model.Expenses;
import br.com.beautystyle.util.CalendarUtil;
import br.com.beautystyle.util.CoinUtil;
import br.com.beautystyle.util.CreateListsUtil;
import me.abhinay.input.CurrencyEditText;
import me.abhinay.input.CurrencySymbols;

public class NewExpenseActivity extends AppCompatActivity {

    private CurrencyEditText value;
    private final Expenses expense = new Expenses();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_spending);

        initWidget();
        setPurchaseDateAction();
        setCategory();
        formatInputEditTextTotalPrice();
        saveExpenseListener();
    }

    private void initWidget() {
        value = findViewById(R.id.activity_new_spending_price);
    }

    private void setPurchaseDateAction() {
        EditText purchaseDate = findViewById(R.id.activity_new_spending_date);
        setPurchaseDateDefault(purchaseDate);
        setPurchaseDateListener(purchaseDate);
    }

    private void setPurchaseDateDefault(EditText purchaseDate){
        String todayDate = CalendarUtil.formatDate(LocalDate.now());
        purchaseDate.setText(todayDate);
        expense.setDate(LocalDate.now());
    }

    private void setPurchaseDateListener(EditText purchaseDate) {
        purchaseDate.setOnClickListener(v -> {
            View inflatedCalendar = getLayoutInflater().inflate(R.layout.dialog_calendar, null);
            AlertDialog dialogBuilderCalendar = createDialogBuilderCalendar(inflatedCalendar);
            setOnDateChangeListener(inflatedCalendar, dialogBuilderCalendar, purchaseDate);
        });
    }

    private AlertDialog createDialogBuilderCalendar(View inflatedCalendar) {
        AlertDialog.Builder dialogCalendar = new AlertDialog.Builder(this);
        dialogCalendar.setView(inflatedCalendar);
        AlertDialog dialog = dialogCalendar.create();
        dialog.show();
        return dialog;
    }

    private void setOnDateChangeListener(View inflatedCalendar, AlertDialog dialogBuilderCalendar, EditText purchaseDate) {
        CalendarView calendar = inflatedCalendar.findViewById(R.id.dialog_calendar_view);
        calendar.setOnDateChangeListener(((view, year, month, dayOfMonth) -> {
            LocalDate date = LocalDate.of(year, month + 1, dayOfMonth);
            setPurchaseDate(date, purchaseDate);
            dialogBuilderCalendar.dismiss();
        }));
    }

    private void setPurchaseDate(LocalDate date, EditText purchaseDate) {
        expense.setDate(date);
        String formatedDate = CalendarUtil.formatDate(date);
        purchaseDate.setText(formatedDate);
    }

    private void setCategory() {
        AutoCompleteTextView category = findViewById(R.id.activity_new_spending_category);
        configureAdapter(category);
        setCategoryDefault();
        setOnItemClickListener(category);
    }

    private void configureAdapter(AutoCompleteTextView category) {
        List<String> categoriesList = CreateListsUtil.createCategoriesList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, categoriesList);
        category.setAdapter(adapter);
    }


    private void setCategoryDefault(){
        expense.setCategory(Category.OUTROS);
    }

    private void setOnItemClickListener(AutoCompleteTextView category) {
        category.setOnItemClickListener(((parent, view, position, id) -> {
            Category findedCategory = Category.getCategoryByDescription(parent.getItemAtPosition(position).toString());
            expense.setCategory(findedCategory);
        }));
    }

    private void formatInputEditTextTotalPrice() {
        value.setCurrency(CurrencySymbols.NONE);
        value.setDelimiter(false);
        value.setSpacing(true);
        value.setDecimals(true);
        value.setSeparator(".");
    }

    private void saveExpenseListener() {
        Button saveExpense = findViewById(R.id.activity_new_spending_save);
        saveExpense.setOnClickListener(v -> {
            setDescriptionAndPrice();
            setResultAndFinishActivity();
        });
    }

    private void setDescriptionAndPrice(){
        EditText description = findViewById(R.id.activity_new_spending_description);
        expense.setDescription(description.getText().toString());
        String formatedPrice = CoinUtil.formatBrBigDecimal(value.getText().toString());
        expense.setPrice(new BigDecimal(formatedPrice));
    }

    private void setResultAndFinishActivity() {
        Intent intent = new Intent();
        intent.putExtra("result", expense);
        setResult(1, intent);
        finish();
    }
}