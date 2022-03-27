package cm.abimmobiledev.mybudgetizer.nav;

import android.app.Activity;
import android.content.Intent;

import cm.abimmobiledev.mybudgetizer.ui.MainMenuActivity;
import cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity;
import cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseRegistrationActivity;
import cm.abimmobiledev.mybudgetizer.ui.expense.ExpensesActivity;

public class ExNavigation {

    public static final String SEARCH_PARAM = "SEARCH_PARAM";
    public static final String EXPENSE_OF_TODAY = "EXP_TODAY";
    public static final String EXPENSE_OF_THIS_MONTH = "EXP_D_MONTH";
    public static final String EXPENSE_OF_THIS_YEAR = "EXP_D_YEAR";
    public static final String EXPENSE_OF_A_DATE = "SEARCH_BY_DATE";

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

    public static void openExpenseSearch(Activity contextToExpenseSearch, String searchParam) {
        Intent intentToExpenseSearch = new Intent(contextToExpenseSearch, ExpensesActivity.class);
        intentToExpenseSearch.putExtra(SEARCH_PARAM, searchParam);
        contextToExpenseSearch.startActivity(intentToExpenseSearch);
        contextToExpenseSearch.finish();
    }

    public static void openMainHome(Activity contextToMainHome) {
        Intent intentToMainHome = new Intent(contextToMainHome, MainMenuActivity.class);
        contextToMainHome.startActivity(intentToMainHome);
        contextToMainHome.finish();
    }

}
