package cm.abimmobiledev.mybudgetizer.ui.expense;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.List;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.entity.Expense;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;

public class ExpenseFilterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_filter);
    }


    /**
     * <h1>Compute the sum of amounts of given list of expenses</h1>
     * @param expenses List of expenses
     * @return a double, representing the total. 0 is returned when list is empty
     * @throws BudgetizerGeneralException when list is null
     */
    public static double computeTotalAmount(List<Expense> expenses) throws BudgetizerGeneralException {

        if (expenses==null)
            throw new BudgetizerGeneralException("Expense list should not be null", 500);

        if (expenses.isEmpty())
            return 0;

        double totalAmt = 0;

        for (int counter=0; counter< expenses.size(); counter++) {
            totalAmt = totalAmt + expenses.get(counter).getAmount();
        }

        return totalAmt;

    }
}