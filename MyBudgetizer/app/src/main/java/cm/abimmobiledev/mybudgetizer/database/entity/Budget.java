package cm.abimmobiledev.mybudgetizer.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "budget")
public class Budget extends BasicInfo{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "budget_id")
    public int budgetId;

    @ColumnInfo(name = "budget_type")
    public String budgetType;

    @ColumnInfo(name = "beginning_date")
    public String beginningDate;

    @ColumnInfo(name = "end_date")
    public String endDate;

    public String description;

    public double consumed;

    public Budget(String budgetType, String entitled, double amount, String sticker, String beginningDate, String endDate) {
        super(entitled, amount, sticker);
        this.beginningDate = beginningDate;
        this.endDate =endDate;
        this.budgetType = budgetType;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getConsumed() {
        return consumed;
    }

    public void setConsumed(double consumed) {
        this.consumed = consumed;
    }

    public void setBudgetType(String budgetType) {
        this.budgetType =  budgetType;
    }

    public String getBudgetType(){
        return this.budgetType;
    }

    public static String BUDGET_TYPE_PRE = "PREVISION";
    public static String BUDGET_TYPE_POST = "CONSO";
}
