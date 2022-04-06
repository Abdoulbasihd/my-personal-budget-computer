package cm.abimmobiledev.mybudgetizer.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cm.abimmobiledev.mybudgetizer.database.entity.Debt;
import cm.abimmobiledev.mybudgetizer.database.entity.Earning;

@Dao
public interface DebtDAO {
    @Query("SELECT * FROM debt")
    List<Earning> getAllDebts();

    @Query("SELECT * FROM debt WHERE debt_id IN (:ids)")
    List<Earning> loadAllByIds(int[] ids);

    /**
     *<h1>Get paid or unpaid debts</h1>
     * @param paid boolean : true when looking for refunded debt, false else
     * @return list of either refunded or not debt (depending on param)
     */
    @Query("SELECT * FROM debt WHERE refunded_or_paid =:paid ")
    List<Debt> loadAllPaidOrUnpaid(boolean paid);

    /**
     *
     * @param likeFormattedLoanDate String. %yyyy% for like year; <br> %yyyy/mm% for like month <br> and %yyyy/mm/dd% for like day formatted..
     * @return a list of debts of a period
     */
    @Query("SELECT * FROM debt WHERE loaning_date LIKE :likeFormattedLoanDate ")
    List<Debt> loadAllDebtLikeFormattedLoanDate(String likeFormattedLoanDate); // year formatted : %yyyy%;


    /**
     *
     * @param likeFormattedPayDate String. %yyyy% for like year; <br> %yyyy/mm% for like month <br> and %yyyy/mm/dd% for like day formatted..
     * @return a list of debts of a period
     */
    @Query("SELECT * FROM debt WHERE repayment_date LIKE :likeFormattedPayDate ")
    List<Debt> loadAllDebtLikeFormattedPaymentDate(String likeFormattedPayDate); // year formatted : %yyyy%;

    /**
     *
     * @param likeFormattedDueDate String. %yyyy% for like year; <br> %yyyy/mm% for like month <br> and %yyyy/mm/dd% for like day formatted..
     * @return a list of debts expiring in a period
     */
    @Query("SELECT * FROM debt WHERE repayment_due_date LIKE :likeFormattedDueDate ")
    List<Debt> loadAllDebtLikeFormattedDueDate(String likeFormattedDueDate); // year formatted : %yyyy%;

    //here we want to select e last limiter numbers of elements of the table Debt
    @Query("SELECT * FROM debt ORDER BY debt_id DESC LIMIT :limiter ")
    List<Earning> getLastDebts(int limiter);

    @Query("SELECT * FROM debt WHERE amount =:amount ")
    List<Debt> loadAllDebtByAmount(double amount);

    @Insert
    void insertAll(Debt... debts);

    @Delete
    void delete(Debt debt);

}
