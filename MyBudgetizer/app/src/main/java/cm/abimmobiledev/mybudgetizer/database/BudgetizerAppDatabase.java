package cm.abimmobiledev.mybudgetizer.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import cm.abimmobiledev.mybudgetizer.database.dao.AccountDAO;
import cm.abimmobiledev.mybudgetizer.database.dao.BudgetDAO;
import cm.abimmobiledev.mybudgetizer.database.dao.DebtDAO;
import cm.abimmobiledev.mybudgetizer.database.dao.EarningDAO;
import cm.abimmobiledev.mybudgetizer.database.dao.ExpenseDAO;
import cm.abimmobiledev.mybudgetizer.database.dao.ReceivableDAO;
import cm.abimmobiledev.mybudgetizer.database.entity.Account;
import cm.abimmobiledev.mybudgetizer.database.entity.Budget;
import cm.abimmobiledev.mybudgetizer.database.entity.Debt;
import cm.abimmobiledev.mybudgetizer.database.entity.Earning;
import cm.abimmobiledev.mybudgetizer.database.entity.Expense;
import cm.abimmobiledev.mybudgetizer.database.entity.Receivable;

@Database(entities = {Expense.class, Earning.class, Debt.class, Receivable.class, Budget.class, Account.class}, version = 6)
public abstract class BudgetizerAppDatabase extends RoomDatabase {

    public abstract ExpenseDAO expenseDAO();
    public abstract EarningDAO earningDAO();
    public abstract DebtDAO debtDAO();
    public abstract ReceivableDAO receivableDAO();
    public abstract BudgetDAO budgetDAO();
    public abstract AccountDAO accountDAO();

    private static  BudgetizerAppDatabase budgetizerAppDatabaseSingleInstance = null;

    public synchronized static  BudgetizerAppDatabase getInstance(Context applicationContext){

        if (budgetizerAppDatabaseSingleInstance==null) {
            budgetizerAppDatabaseSingleInstance  = Room.databaseBuilder(applicationContext.getApplicationContext(), BudgetizerAppDatabase.class, "my-budgets-db")
                    //not allowing main thread queries ? or  allowMainThreadQueries
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return budgetizerAppDatabaseSingleInstance;

    }
}
