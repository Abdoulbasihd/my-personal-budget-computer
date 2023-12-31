package cm.abimmobiledev.mybudgetizer.nav;

import static cm.abimmobiledev.mybudgetizer.nav.ExNavigation.ACC_NAME_PARAM;
import static cm.abimmobiledev.mybudgetizer.nav.ExNavigation.CURRENCY_PARAM;
import static cm.abimmobiledev.mybudgetizer.nav.ExNavigation.SEARCH_PARAM;

import android.app.Activity;
import android.content.Intent;

import cm.abimmobiledev.mybudgetizer.ui.debt.DebtBoardActivity;
import cm.abimmobiledev.mybudgetizer.ui.debt.DebtRegistrationActivity;
import cm.abimmobiledev.mybudgetizer.ui.debt.DebtsActivity;

public class DebtNavigator {

    public static final String DEBT_CONTRACTED_TODAY = "DEBT_CONTRACTED_TODAY";
    public static final String DEBT_CONTRACTED_THIS_MONTH = "DEBT_CONTRACTED_THIS_MONTH";
    public static final String DEBT_CONTRACTED_THIS_YEAR = "DEBT_CONTRACTED_THIS_YEAR";


    public static final String DEBT_PAID_TODAY = "DEBT_PAID_TODAY";
    public static final String DEBT_PAID_THIS_MONTH = "DEBT_PAID_THIS_MONTH";
    public static final String DEBT_PAID_THIS_YEAR = "DEBT_PAID_THIS_YEAR";


    public static final String DEBT_EXPIRES_TODAY = "DEBT_EXPIRES_TODAY";
    public static final String DEBT_EXPIRES_THIS_MONTH = "DEBT_EXPIRES_THIS_MONTH";
    public static final String DEBT_EXPIRES_THIS_YEAR = "DEBT_EXPIRES_THIS_YEAR";

    public static final String DEBT_OTHER_SEARCH = "DEBT_OTHER_SEARCH";

    public static void openNewDebt(Activity contextToNewDebt, String acc, String curr) {
        Intent intentToNewDebt = new Intent(contextToNewDebt, DebtRegistrationActivity.class);
        intentToNewDebt.putExtra(ACC_NAME_PARAM, acc);
        intentToNewDebt.putExtra(CURRENCY_PARAM, curr);
        contextToNewDebt.startActivity(intentToNewDebt);
        contextToNewDebt.finish();
    }


    public static void openDebtsHome(Activity contextToDebtBoard, String acc, String curr) {
        Intent intentToDebtDash = new Intent(contextToDebtBoard, DebtBoardActivity.class);
        intentToDebtDash.putExtra(ACC_NAME_PARAM, acc);
        intentToDebtDash.putExtra(CURRENCY_PARAM, curr);
        contextToDebtBoard.startActivity(intentToDebtDash);
        contextToDebtBoard.finish();
    }

    public static void openDebtsSearch(Activity contextToDebtsSearch, String searchParam, String acc, String curr) {
        Intent intentToDebtsSearch = new Intent(contextToDebtsSearch, DebtsActivity.class);
        intentToDebtsSearch.putExtra(ACC_NAME_PARAM, acc);
        intentToDebtsSearch.putExtra(CURRENCY_PARAM, curr);
        intentToDebtsSearch.putExtra(SEARCH_PARAM, searchParam);
        contextToDebtsSearch.startActivity(intentToDebtsSearch);
        contextToDebtsSearch.finish();
    }
}
