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
    List<Expense> loadAllExpenseOfAGivenMonth(String monthFormatted); // month formatted :

    @Query("SELECT * FROM expense WHERE date_time_of_expense LIKE :yearFormatted ")
    List<Expense> loadAllExpenseOfAGivenYear(String yearFormatted);

    @Query("SELECT * FROM expense WHERE amount =:expenseAmount ")
    List<Expense> loadAllExpenseByExpenseAmount(String expenseAmount);

    @Insert
    void insertAll(Expense... expenses);

    @Delete
    void delete(Expense expense);




}
