package cm.abimmobiledev.mybudgetizer.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import cm.abimmobiledev.mybudgetizer.database.entity.Budget;
import cm.abimmobiledev.mybudgetizer.database.entity.BudgetWithExpenses;

@Dao
public interface BudgetDAO {

    @Query("SELECT * FROM budget ORDER BY budget_id DESC")
    List<Budget> getBudgets();

    //here we want to select last limiter numbers of elements of the table budget
    @Query("SELECT * FROM budget ORDER BY budget_id DESC LIMIT :limiter ")
    List<Budget> getLastBudgets(int limiter);

    /**
     * <h1>Get expenses of one budget</h1>
     * @param budgetId int, the id of the given budget
     * @return BudgetWithExpenses's object, containing the list of expenses of the given budget...
     */
    @Transaction
    @Query("SELECT * FROM budget WHERE budget_id =:budgetId")
    BudgetWithExpenses getExpensesOfGivenBudget(int budgetId);

    @Query("SELECT * FROM budget")
    @Transaction
    List<BudgetWithExpenses> getAllBudgetsWithExpenses();

    //add select budget for a period
    @Query("SELECT * FROM budget WHERE beginning_date LIKE :startDate AND end_date LIKE :endDate")
    List<Budget> getPeriodicBudgets(String startDate, String endDate);

    @Insert
    void insertAll(Budget... budgets);

    @Delete
    void delete(Budget budget);

    @Update
    void update(Budget budget);

}
