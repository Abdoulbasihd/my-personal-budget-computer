package cm.abimmobiledev.mybudgetizer.database.entity;

import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;

public class BudgetWithExpenses {
    @Embedded public Budget budget;
    @Relation(
            parentColumn = "budgetId",
            entityColumn = "fkBudgetId"
    )
    public List<Expense> expenses;

    public BudgetWithExpenses(Budget budget, List<Expense> expenses) {
        this.budget = budget;
        this.expenses = expenses;
    }
}
