package cm.abimmobiledev.mybudgetizer.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import cm.abimmobiledev.mybudgetizer.database.entity.Account;

@Dao
public interface AccountDAO {

    @Query("SELECT * FROM account")
    List<Account> getAccounts(); //for now, it'll return just one element

    @Insert
    void insertAll(Account... budgets);

    @Delete
    void delete(Account budget);

    @Update
    void update(Account budget);
}
