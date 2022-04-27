package cm.abimmobiledev.mybudgetizer.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budget")
public class Budget extends BasicInfo{

    @PrimaryKey(autoGenerate = true)
    public int budgetId;

    @ColumnInfo(name = "beginning_date")
    public String beginningDate;

    @ColumnInfo(name = "end_date")
    public String endDate;

    public Budget(String entitled, double amount, String sticker, String beginningDate, String endDate) {
        super(entitled, amount, sticker);
        this.beginningDate = beginningDate;
        this.endDate =endDate;
    }


    public int getBudgetId() {
        return budgetId;
    }

    public void setBudgetId(int budgetId) {
        this.budgetId = budgetId;
    }

    public String getBeginningDate() {
        return beginningDate;
    }

    public void setBeginningDate(String beginningDate) {
        this.beginningDate = beginningDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
