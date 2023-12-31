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

    public static final String ACC_NAME_PARAM = "ACNAME";
    public static final String CURRENCY_PARAM = "ACCURR";

    public static void openNewExpense(Activity contextToNewExpense, String acc, String curr) {
        Intent intentToNewExpense = new Intent(contextToNewExpense, ExpenseRegistrationActivity.class);
        intentToNewExpense.putExtra(ACC_NAME_PARAM, acc);
        intentToNewExpense.putExtra(CURRENCY_PARAM, curr);
        contextToNewExpense.startActivity(intentToNewExpense);
        contextToNewExpense.finish();
    }

    public static void openExpensesHome(Activity contextToExpenseDash, String acc, String curr) {
        Intent intentToExpenseDashB = new Intent(contextToExpenseDash, ExpenseDashboardActivity.class);
        intentToExpenseDashB.putExtra(ACC_NAME_PARAM, acc);
        intentToExpenseDashB.putExtra(CURRENCY_PARAM, curr);
        contextToExpenseDash.startActivity(intentToExpenseDashB);
        contextToExpenseDash.finish();
    }

    public static void openExpenseSearch(Activity contextToExpenseSearch, String searchParam, String acc, String curr) {
        Intent intentToExpenseSearch = new Intent(contextToExpenseSearch, ExpensesActivity.class);
        intentToExpenseSearch.putExtra(SEARCH_PARAM, searchParam);
        intentToExpenseSearch.putExtra(ACC_NAME_PARAM, acc);
        intentToExpenseSearch.putExtra(CURRENCY_PARAM, curr);
        contextToExpenseSearch.startActivity(intentToExpenseSearch);
        contextToExpenseSearch.finish();
    }

    public static void openMainHome(Activity contextToMainHome, String accountName, String currency) {
        Intent intentToMainHome = new Intent(contextToMainHome, MainMenuActivity.class);
        intentToMainHome.putExtra(ACC_NAME_PARAM, accountName);
        intentToMainHome.putExtra(CURRENCY_PARAM, currency);
        contextToMainHome.startActivity(intentToMainHome);
        contextToMainHome.finish();
    }

}
