package cm.abimmobiledev.mybudgetizer.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cm.abimmobiledev.mybudgetizer.database.entity.Debt;

@Dao
public interface DebtDAO {

    /**
     *
     * @param likeFormattedLoanDate String. %yyyy% for like year; <br> %yyyy/mm% for like month <br> and %yyyy/mm/dd% for like day formatted..
     * @return a list of debts of a period
     */
    @Query("SELECT * FROM debt WHERE loaning_date LIKE :likeFormattedLoanDate ")
    List<Debt> loadAllDebtLikeFormattedLoanDate(String likeFormattedLoanDate); // year formatted : %yyyy%;

    /**
     *
     * @param likeFormattedLoanDate String. %yyyy% for like year; <br> %yyyy/mm% for like month <br> and %yyyy/mm/dd% for like day formatted..
     * @return a list of debts of a period
     */
    @Query("SELECT * FROM debt WHERE loaning_date LIKE :likeFormattedLoanDate and repayment_date=''")
    List<Debt> loadAllUnpaidDebtLikeFormattedLoanDate(String likeFormattedLoanDate); // year formatted : %yyyy%;

    //here we want to select e last limiter numbers of elements of the table Debt
    @Query("SELECT * FROM debt ORDER BY debt_id DESC LIMIT :limiter ")
    List<Debt> getLastDebts(int limiter);

    @Query("SELECT * FROM debt WHERE amount =:amount ")
    List<Debt> loadAllDebtByAmount(double amount);

    @Query("SELECT * FROM debt WHERE repayment_due_date LIKE :expirationDateFormatted AND loaning_date LIKE :loanDate")
    List<Debt> getAllDebts(String loanDate, String expirationDateFormatted);

    /**
     *
     * @param paymentDate String. %yyyy% for like year; <br> %yyyy/mm% for like month <br> and %yyyy/mm/dd% for like day formatted..
     * @return a list of debts of a period
     */
    @Query("SELECT * FROM debt WHERE repayment_date LIKE :paymentDate AND repayment_date !=''")
    List<Debt> getAllPaidDebtsOn(String paymentDate);

    @Insert
    void insertAll(Debt... debts);

    @Delete
    void delete(Debt debt);

    @Update
    void update(Debt debt);

}
