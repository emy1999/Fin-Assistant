package com.example.finassistant.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finassistant.R;
import com.example.finassistant.domain.ExchangeCategory;
import com.example.finassistant.domain.Expense;
import com.example.finassistant.domain.ExpenseCategory;
import com.example.finassistant.ui.account.ExpensePresenter;
import com.example.finassistant.ui.account.ExpenseView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/**
 * The type Expense activity.
 */
public class ExpenseActivity extends AppCompatActivity implements ExpenseView {

    /**
     * The Text view 2.
     */
    TextView textView2;
    /**
     * The Text view 3.
     */
    TextView textView3;
    /**
     * The Text view 4.
     */
    TextView textView4;
    /**
     * The Expenses.
     */
    ListView expenses;
    /**
     * The Selected category.
     */
    ExpenseCategory selected_category;
    /**
     * The Selected exchange category.
     */
    ExchangeCategory selected_exchange_category;
    /**
     * The Categories.
     */
    Spinner categories;
    /**
     * The Exchange category.
     */
    Spinner exchange_category;
    /**
     * The Amount.
     */
    EditText amount;
    /**
     * The Date.
     */
    EditText date;

    /**
     * The Expenselist.
     */
    ArrayList<Expense> expenselist = new ArrayList<>();
    /**
     * The Amount value.
     */
    double amountValue;
    /**
     * The Date value.
     */
    Date dateValue;

    /**
     * The Presenter.
     */
    static ExpensePresenter presenter;
    /**
     * The Expense.
     */
    Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense);
        presenter =  new ExpensePresenter(this);
    }

    public void onStart() {
        super.onStart();

        categories = findViewById(R.id.category);
        exchange_category = findViewById(R.id.exchange_category);
        amount = findViewById(R.id.txt_input);
        date = findViewById(R.id.date);
        expenses = findViewById(R.id.button_expense);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        final FloatingActionButton add = findViewById(R.id.add);
        final Button submit = findViewById(R.id.submit);
        date.setVisibility(View.GONE);
        amount.setVisibility(View.GONE);
        submit.setVisibility(View.GONE);
        categories.setVisibility(View.GONE);
        exchange_category.setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        textView3.setVisibility(View.VISIBLE);
        textView4.setVisibility(View.GONE);
        expenses.setVisibility(View.VISIBLE);
        add.setVisibility(View.VISIBLE);

        textView3.setText("Total expenses: " + presenter.getAccount().CalculateTotalExpense() + " €");

        Iterator<Expense> iterator = presenter.getAccount().getExpenses().iterator();
        while(iterator.hasNext()) {
            expenselist.add(iterator.next());
        }

        final ArrayAdapter arrayAdapter = new ArrayAdapter(ExpenseActivity.this, android.R.layout.simple_list_item_1, expenselist);
        expenses.setAdapter(arrayAdapter);

        expenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                android.app.AlertDialog.Builder info = new android.app.AlertDialog.Builder(ExpenseActivity.this);
                info.setTitle("Details");
                DecimalFormat numberFormat = new DecimalFormat("#.00");
                info.setMessage("Category: " + expenselist.get(position).getCategory() +"\n\n" + "Amount: " +
                        numberFormat.format(expenselist.get(position).getSum()) + " €\n\n" + "End Date: " + expenselist.get(position).getDateEnd() + "\n\nExchange Category: " +
                        expenselist.get(position).getExchange());

                info.setPositiveButton("OK", null);

                info.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        android.app.AlertDialog.Builder delete = new android.app.AlertDialog.Builder(ExpenseActivity.this);
                        delete.setTitle("Are you sure you want to delete the expense?");
                        delete.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });
                        delete.setPositiveButton("Delete", new android.app.AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Expense expense = expenselist.get(position);
                                presenter.getAccount().removeExpense(expense);
                                arrayAdapter.notifyDataSetChanged();
                            }});
                        delete.show();
                    }
                });
                info.show();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ExpenseCategory[] items = new ExpenseCategory[]{ExpenseCategory.HEALTH, ExpenseCategory.ENTERTAINMENT, ExpenseCategory.SHOPPING, ExpenseCategory.TRANSPORT, ExpenseCategory.OBLIGATION};

                ArrayAdapter<String> adapter = new ArrayAdapter(ExpenseActivity.this, android.R.layout.simple_spinner_dropdown_item, items);

                categories.setAdapter(adapter);

                ExchangeCategory[] exchangeCategories = new ExchangeCategory[]{ExchangeCategory.CASH, ExchangeCategory.ONLINE};
                ArrayAdapter<String> eadapter = new ArrayAdapter(ExpenseActivity.this, android.R.layout.simple_spinner_dropdown_item, exchangeCategories);
                exchange_category.setAdapter(eadapter);

                date.setVisibility(View.VISIBLE);
                amount.setVisibility(View.VISIBLE);
                categories.setVisibility(View.VISIBLE);
                exchange_category.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                textView3.setVisibility(View.GONE);
                textView4.setVisibility(View.VISIBLE);
                expenses.setVisibility(View.GONE);
                add.setVisibility(View.GONE);

                expense = new Expense();

                categories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String clickedItem = String.valueOf(position);
                        if (clickedItem.equals("0")){
                            addCategory(ExpenseCategory.HEALTH);
                            selected_category=ExpenseCategory.HEALTH;
                        }else if (clickedItem.equals("1")){
                            addCategory(ExpenseCategory.ENTERTAINMENT);
                            selected_category=ExpenseCategory.ENTERTAINMENT;
                        }else if(clickedItem.equals("2")){
                            addCategory(ExpenseCategory.SHOPPING);
                            selected_category=ExpenseCategory.SHOPPING;
                        }else if(clickedItem.equals("3")){
                            addCategory(ExpenseCategory.TRANSPORT);
                            selected_category=ExpenseCategory.TRANSPORT;
                        }else if(clickedItem.equals("4")){
                            addCategory(ExpenseCategory.OBLIGATION);
                            selected_category=ExpenseCategory.OBLIGATION;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        addCategory(ExpenseCategory.HEALTH);
                    }
                });

                exchange_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String clickedItem = String.valueOf(position);
                        if (clickedItem.equals("0")){
                            selected_exchange_category = ExchangeCategory.CASH;
                        }else if (clickedItem.equals("1")){
                            selected_exchange_category = ExchangeCategory.ONLINE;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        amount = findViewById(R.id.txt_input);

                        if(amount.getText().toString().equals("")){
                            amount.setText("0");
                        }

                        amountValue = Double.parseDouble(amount.getText().toString());
                        amount.setText("");
                        boolean isValid = presenter.validateAmount(amountValue);

                        if (isValid) {
                            addAmount(amountValue);

                            date = findViewById(R.id.date);
                            String parsedDate = (date.getText().toString());
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            if (parsedDate.equals("")) {
                                addDate(new Date());
                            }
                            else {
                                try {
                                    dateValue = formatter.parse(parsedDate);
                                    addDate(dateValue);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }

                            expenses.setVisibility(View.VISIBLE);
                            add.setVisibility(View.VISIBLE);
                            amount.setVisibility(View.GONE);
                            date.setVisibility(View.GONE);
                            submit.setVisibility(View.GONE);
                            categories.setVisibility(View.GONE);
                            exchange_category.setVisibility(View.GONE);
                            textView2.setVisibility(View.GONE);
                            textView3.setVisibility(View.VISIBLE);
                            textView4.setVisibility(View.GONE);

                            presenter.getAccount().addExpense(expense);

                            expense.setExchange(selected_exchange_category);

                            expenselist.add(expense);

                            presenter.getAccount().addExpense(expense);
                            textView3.setText("Total expenses: " + presenter.getAccount().CalculateTotalExpense() + " €");
                        }
                    }
                });
            }
        });
    }

    @Override
    public void addCategory(ExpenseCategory category){
        expense.setCategory(category);
    }

    @Override
    public void addAmount(Double amount){
        expense.setSum(amount);
    }

    @Override
    public void addDate(Date date){
        expense.setDateEnd(date);
    }

    @Override
    public void showErrorMessage(String title,String message){
        new AlertDialog.Builder(ExpenseActivity.this)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null).create().show();
    }
}
