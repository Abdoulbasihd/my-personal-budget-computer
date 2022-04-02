package cm.abimmobiledev.mybudgetizer.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cm.abimmobiledev.mybudgetizer.database.entity.Earning;
import cm.abimmobiledev.mybudgetizer.database.entity.Expense;

@Dao
public interface EarningDAO {

    @Query("SELECT * FROM earning")
    List<Earning> getAllEearnings();

    @Query("SELECT * FROM earning WHERE earning_id IN (:ids)")
    List<Earning> loadAllByIds(int[] ids);

    /**
     *
     * @param likeFormattedDate String. %yyyy% for like year; <br> %yyyy/mm% for like month <br> and %yyyy/mm/dd% for like day formatted..
     * @return a list of earnings
     */
    @Query("SELECT * FROM earning WHERE date_time_of_income LIKE :likeFormattedDate ")
    List<Earning> loadAllEarningLikeFormattedDate(String likeFormattedDate); // year formatted : %yyyy%;

    //here we want to select e last limiter numbers of elements of the table Earning
    @Query("SELECT * FROM earning ORDER BY earning_id DESC LIMIT :limiter ")
    List<Earning> getLastEarnings(int limiter);

    @Query("SELECT * FROM earning WHERE amount =:amount ")
    List<Earning> loadAllEarningByEarningAmount(double amount);

    @Insert
    void insertAll(Earning... earnings);

    @Delete
    void delete(Earning earning);


}
