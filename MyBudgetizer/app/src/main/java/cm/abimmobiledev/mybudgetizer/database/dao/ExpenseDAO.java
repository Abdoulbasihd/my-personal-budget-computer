package cm.abimmobiledev.mybudgetizer.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cm.abimmobiledev.mybudgetizer.database.entity.Expense;

@Dao
public interface ExpenseDAO {


    @Query("SELECT * FROM expense")
    List<Expense> getAll();

    @Query("SELECT * FROM expense WHERE expense_id IN (:expenseIds)")
    List<Expense> loadAllByIds(int[] expenseIds);

    @Query("SELECT * FROM expense WHERE date_time_of_expense =:expenseDate ")
    List<Expense> loadAllByExpenseDate(String expenseDate);

    @Query("SELECT * FROM expense WHERE date_time_of_expense LIKE :monthFormatted ")
    List<Expense> loadAllExpenseOfAGivenMonth(String monthFormatted); // month formatted.... we ain't need others (for month and day) as only the param change

    //here we want to select e last limiter numbers of elements of the table expense
    @Query("SELECT * FROM expense ORDER BY expense_id DESC LIMIT :limiter ")
    List<Expense> getLastExpenses(int limiter);

    @Query("SELECT * FROM expense WHERE amount =:expenseAmount ")
    List<Expense> loadAllExpenseByExpenseAmount(String expenseAmount);

    @Insert
    void insertAll(Expense... expenses);

    @Delete
    void delete(Expense expense);




}
