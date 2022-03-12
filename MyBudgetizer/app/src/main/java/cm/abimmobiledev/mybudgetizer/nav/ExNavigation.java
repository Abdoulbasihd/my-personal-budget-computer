package cm.abimmobiledev.mybudgetizer.nav;

import android.app.Activity;
import android.content.Intent;

import cm.abimmobiledev.mybudgetizer.ui.MainMenuActivity;
import cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity;
import cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseRegistrationActivity;

public class ExNavigation {

    public static void openNewExpense(Activity contextToNewExpense) {
        Intent intentToNewExpense = new Intent(contextToNewExpense, ExpenseRegistrationActivity.class);
        contextToNewExpense.startActivity(intentToNewExpense);
        contextToNewExpense.finish();
    }

    public static void openExpensesHome(Activity contextToExpenseDash) {
        Intent intentToExpenseDashB = new Intent(contextToExpenseDash, ExpenseDashboardActivity.class);
        contextToExpenseDash.startActivity(intentToExpenseDashB);
        contextToExpenseDash.finish();
    }

    public static void openMainHome(Activity contextToMainHome) {
        Intent intentToMainHome = new Intent(contextToMainHome, MainMenuActivity.class);
        contextToMainHome.startActivity(intentToMainHome);
        contextToMainHome.finish();
    }

}
