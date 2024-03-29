package com.main.personalfinances.db;

import android.content.Context;


import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.main.personalfinances.converter.LocalDateConverter;
import com.main.personalfinances.converter.LocalDateTimeConverter;
import com.main.personalfinances.dao.FuturePaymentDao;
import com.main.personalfinances.model.Budget;
import com.main.personalfinances.dao.BudgetDao;
import com.main.personalfinances.model.FuturePayment;
import com.main.personalfinances.model.Savings;
import com.main.personalfinances.dao.SavingsDao;
import com.main.personalfinances.model.Expense;
import com.main.personalfinances.dao.ExpenseDao;

@Database(entities = {Budget.class, Expense.class, Savings.class, FuturePayment.class},
        version = 111, exportSchema = false)
@TypeConverters({LocalDateConverter.class, LocalDateTimeConverter.class})
public abstract class PersonalFinancesDatabase extends RoomDatabase {

    public abstract BudgetDao budgetDao();

    public abstract ExpenseDao expenseDao();

    public abstract SavingsDao savingsDao();

    public abstract FuturePaymentDao futurePaymentDao();

    // volatile ensures that it can be used by multiple threads
    private static volatile PersonalFinancesDatabase INSTANCE;

    /**
     * Singleton for database
     */
    public static PersonalFinancesDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (PersonalFinancesDatabase.class) {
                // double-check to ensure another thread hasn't initialized a database
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    PersonalFinancesDatabase.class, "personalFinance_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
