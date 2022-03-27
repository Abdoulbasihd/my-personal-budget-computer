package cm.abimmobiledev.mybudgetizer.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import cm.abimmobiledev.mybudgetizer.database.dao.EarningDAO;
import cm.abimmobiledev.mybudgetizer.database.dao.ExpenseDAO;
import cm.abimmobiledev.mybudgetizer.database.entity.Earning;
import cm.abimmobiledev.mybudgetizer.database.entity.Expense;

@Database(entities = {Expense.class, Earning.class}, version = 1)
public abstract class BudgetizerAppDatabase extends RoomDatabase {

    public abstract ExpenseDAO expenseDAO();
    public abstract EarningDAO earningDAO();

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
