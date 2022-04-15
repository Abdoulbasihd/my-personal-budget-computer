package cm.abimmobiledev.mybudgetizer.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cm.abimmobiledev.mybudgetizer.database.entity.Debt;
import cm.abimmobiledev.mybudgetizer.database.entity.Expense;
import cm.abimmobiledev.mybudgetizer.database.entity.Receivable;

@Dao
public interface ReceivableDAO {



    //here we want to select e last limiter numbers of elements of the table receivable
    @Query("SELECT * FROM receivable ORDER BY receivable_id DESC LIMIT :limiter ")
    List<Receivable> getLastReceivables(int limiter);

    /**
     *
     * @param disbursementDate String. %yyyy% for like year; <br> %yyyy/mm% for like month <br> and %yyyy/mm/dd% for like day formatted..
     * @return a list of debts of a period
     */
    @Query("SELECT * FROM receivable WHERE disbursement_date LIKE :disbursementDate AND disbursement_date !=''")
    List<Receivable> getAllPaidReceivablesOn(String disbursementDate);


    /**
     * @param loanDate String formatted for date, month or day of loan
     * @param expirationDateFormatted String formatted for date, month or day of disbursement due date
     * @return a list of receivables suitable to the given period
     */
    @Query("SELECT * FROM receivable WHERE due_date LIKE :expirationDateFormatted AND loan_date LIKE :loanDate")
    List<Receivable> getAllReceivables(String loanDate, String expirationDateFormatted);


    /**
     *
     * @param likeFormattedLoanDate String. %yyyy% for like year; <br> %yyyy/mm% for like month <br> and %yyyy/mm/dd% for like day formatted..
     * @return a list of debts of a period
     */
    @Query("SELECT * FROM receivable WHERE loan_date LIKE :likeFormattedLoanDate ")
    List<Receivable> loadAllReceivableLikeFormattedLoanDate(String likeFormattedLoanDate); // year formatted : %yyyy%;


    @Insert
    void insertAll(Receivable... receivables);

    @Delete
    void delete(Receivable receivable);

    @Update
    void update(Receivable receivable);
}
