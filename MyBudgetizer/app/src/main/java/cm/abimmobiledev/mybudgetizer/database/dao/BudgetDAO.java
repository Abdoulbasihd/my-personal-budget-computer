package cm.abimmobiledev.mybudgetizer.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cm.abimmobiledev.mybudgetizer.database.entity.Budget;
import cm.abimmobiledev.mybudgetizer.database.entity.Debt;

@Dao
public interface BudgetDAO {
    @Insert
    void insertAll(Budget... budgets);

    @Delete
    void delete(Budget budget);

    @Update
    void update(Budget budget);

    //here we want to select last limiter numbers of elements of the table budget
    @Query("SELECT * FROM budget ORDER BY budgetId DESC LIMIT :limiter ")
    List<Debt> getLastBudgets(int limiter);

    //add select budget for a period
    //

}
