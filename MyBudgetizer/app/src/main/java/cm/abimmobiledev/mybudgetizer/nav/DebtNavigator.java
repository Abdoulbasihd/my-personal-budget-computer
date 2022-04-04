package cm.abimmobiledev.mybudgetizer.nav;

import static cm.abimmobiledev.mybudgetizer.nav.ExNavigation.SEARCH_PARAM;

import android.app.Activity;
import android.content.Intent;

import cm.abimmobiledev.mybudgetizer.ui.debt.DebtBoardActivity;
import cm.abimmobiledev.mybudgetizer.ui.debt.DebtRegistrationActivity;
import cm.abimmobiledev.mybudgetizer.ui.debt.DebtsActivity;
import cm.abimmobiledev.mybudgetizer.ui.earning.EarningBoardActivity;
import cm.abimmobiledev.mybudgetizer.ui.earning.EarningRegistrationActivity;
import cm.abimmobiledev.mybudgetizer.ui.earning.EarningsActivity;

public class DebtNavigator {

    public static void openNewDebt(Activity contextToNewDebt) {
        Intent intentToNewDebt = new Intent(contextToNewDebt, DebtRegistrationActivity.class);
        contextToNewDebt.startActivity(intentToNewDebt);
        contextToNewDebt.finish();
    }


    public static void openDebtsHome(Activity contextToDebtBoard) {
        Intent intentToDebtDash = new Intent(contextToDebtBoard, DebtBoardActivity.class);
        contextToDebtBoard.startActivity(intentToDebtDash);
        contextToDebtBoard.finish();
    }

    public static void openDebtsSearch(Activity contextToDebtsSearch, String searchParam) {
        Intent intentToDebtsSearch = new Intent(contextToDebtsSearch, DebtsActivity.class);
        intentToDebtsSearch.putExtra(SEARCH_PARAM, searchParam);
        contextToDebtsSearch.startActivity(intentToDebtsSearch);
        contextToDebtsSearch.finish();
    }
}
