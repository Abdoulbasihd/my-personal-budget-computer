package cm.abimmobiledev.mybudgetizer.database.entity;


import static androidx.room.ForeignKey.CASCADE;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "expense",
        foreignKeys = @ForeignKey(
                entity = Budget.class,
                parentColumns = "budget_id",
                childColumns = "fk_budget_id",
                onDelete = CASCADE
        )
)
public class Expense extends BasicInfo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "expense_id")
    public int expenseId;

    @ColumnInfo(name = "fk_budget_id", index = true)
    private int fkBudgetId;


    @ColumnInfo(name = "date_time_of_expense")
    public String dateTimeOfExpense;

    public String reason;

    public Expense(String entitled, double amount, String dateTimeOfExpense, String reason, String sticker) {
        super(entitled, amount, sticker);
        this.dateTimeOfExpense = dateTimeOfExpense;
        this.reason = reason;
    }

    public int getFkBudgetId() {
        return fkBudgetId;
    }

    public void setFkBudgetId(int fkBudgetId) {
        this.fkBudgetId = fkBudgetId;
    }

    public int getExpenseId() {
        return expenseId;
    }

    public void setExpenseId(int expenseId) {
        this.expenseId = expenseId;
    }

    public String getDateTimeOfExpense() {
        return dateTimeOfExpense;
    }

    public void setDateTimeOfExpense(String dateTimeOfExpense) {
        this.dateTimeOfExpense = dateTimeOfExpense;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
