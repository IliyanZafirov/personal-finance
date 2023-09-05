package com.main.personalfinances.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.main.personalfinances.R;
import com.main.personalfinances.daos.BudgetDao;
import com.main.personalfinances.daos.ExpenseDao;
import com.main.personalfinances.data.Budget;
import com.main.personalfinances.data.BudgetRepository;
import com.main.personalfinances.data.Expense;
import com.main.personalfinances.data.ExpensesRepository;
import com.main.personalfinances.db.PersonalFinancesDatabase;
import com.main.personalfinances.enums.TransactionCategory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateExpenseActivity extends AppCompatActivity {

    private PersonalFinancesDatabase appDatabase;
    private ExpenseDao expenseDao;
    private ExpensesRepository expensesRepository;
    private BudgetDao budgetDao;
    private BudgetRepository budgetRepository;

    private ExecutorService databaseWriteExecutor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        Toolbar myToolbar = findViewById(R.id.form_toolbar);
        setSupportActionBar(myToolbar);


        try {
            appDatabase = PersonalFinancesDatabase.getDatabase(this);
            expenseDao = appDatabase.expenseDao();
            expensesRepository = new ExpensesRepository(expenseDao);
            budgetDao = appDatabase.budgetDao();
            budgetRepository = new BudgetRepository(budgetDao);
        } catch (Exception e) {
            e.printStackTrace();
        }
        databaseWriteExecutor = Executors.newSingleThreadExecutor();

        Spinner categorySpinner = findViewById(R.id.spinnerCategory);
        TransactionCategory[] categories = TransactionCategory.values();
        ArrayAdapter<TransactionCategory> categoryAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        categorySpinner.setAdapter(categoryAdapter);

        Button saveExpenseButton = findViewById(R.id.btnSaveExpense);
        saveExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createExpense();
            }
        });
    }

    private void createExpense() {
        Spinner categorySpinner = findViewById(R.id.spinnerCategory);
        TransactionCategory category = (TransactionCategory) categorySpinner.getSelectedItem();
        EditText editPrice = findViewById(R.id.editPrice);
        EditText editDescription = findViewById(R.id.editDescription);
        EditText editDueDate = findViewById(R.id.editDueDate);

        String description = editDescription.getText().toString();
        String dueDateString = editDueDate.getText().toString();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        final LocalDate dueDate;

        if (categoryRequiresDueDate(category)) {
            try {
                dueDate = LocalDate.parse(dueDateString, formatter);

                if (dueDate.isBefore(LocalDate.now())) {
                    runOnUiThread(() -> {
                        showToast("Due date can't be in the past");
                    });
                    return;
                }
            } catch(Exception e) {
                runOnUiThread(() -> {
                    showToast("Invalid due date format");
                });
                return;
            }
        } else {
            dueDate = null;
        }

        double price;
        try {
            price = Double.parseDouble(editPrice.getText().toString());
            databaseWriteExecutor.execute(() -> {
                Budget budget = budgetRepository.getBudget();
                if (budget.getCurrentAmount() >= price) {
                    budget.pay(price);
                    budgetRepository.updateBudget(budget);

                    Expense expense = new Expense(1, category, description, LocalDate.now(),
                            dueDate, price);
                    expensesRepository.insertExpense(expense);

                    if (dueDate != null) {
                        runOnUiThread(()-> {
                            expense.scheduleNotification(getApplicationContext());
                        });
                    }

                    runOnUiThread(() -> {
                        Intent intent = new Intent(this, ExpensesActivity.class);
                        startActivity(intent);
                    });
                } else {
                    runOnUiThread(() -> showToast("Price is bigger than current budget amount"));
                }
            });
        } catch (NumberFormatException e) {
            runOnUiThread(() -> showToast("Invalid expense"));
        }
    }

    private void showToast(String s) {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void goToExpenses(View view) {
        Intent intent = new Intent(this, ExpensesActivity.class);
        startActivity(intent);
    }

    public void goToMain(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private boolean categoryRequiresDueDate(TransactionCategory category) {
        return category == TransactionCategory.BILLS ||
                category == TransactionCategory.TAXES ||
                category == TransactionCategory.LOAN_SERVICING;
    }
}
