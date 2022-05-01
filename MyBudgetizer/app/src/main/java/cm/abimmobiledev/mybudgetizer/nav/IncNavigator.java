package cm.abimmobiledev.mybudgetizer.nav;

import static cm.abimmobiledev.mybudgetizer.nav.ExNavigation.SEARCH_PARAM;

import android.app.Activity;
import android.content.Intent;

import cm.abimmobiledev.mybudgetizer.ui.earning.EarningBoardActivity;
import cm.abimmobiledev.mybudgetizer.ui.earning.EarningRegistrationActivity;
import cm.abimmobiledev.mybudgetizer.ui.earning.EarningsActivity;

public class IncNavigator {

    public static void openNewIncome(Activity contextToNewIncome, String acc, String curr) {
        Intent intentToNewInc = new Intent(contextToNewIncome, EarningRegistrationActivity.class);
        intentToNewInc.putExtra(ExNavigation.ACC_NAME_PARAM, acc);
        intentToNewInc.putExtra(ExNavigation.CURRENCY_PARAM, curr);
        contextToNewIncome.startActivity(intentToNewInc);
        contextToNewIncome.finish();
    }


    public static void openEarningsHome(Activity contextToEarningsDash, String accName, String curr) {
        Intent intentToIncomeDashB = new Intent(contextToEarningsDash, EarningBoardActivity.class);
        intentToIncomeDashB.putExtra(ExNavigation.ACC_NAME_PARAM, accName);
        intentToIncomeDashB.putExtra(ExNavigation.CURRENCY_PARAM, curr);
        contextToEarningsDash.startActivity(intentToIncomeDashB);
        contextToEarningsDash.finish();
    }

    public static void openEarningsSearch(Activity contextToEarningsSearch, String searchParam, String  accName, String curr) {
        Intent intentToEarningsSearch = new Intent(contextToEarningsSearch, EarningsActivity.class);
        intentToEarningsSearch.putExtra(SEARCH_PARAM, searchParam);
        intentToEarningsSearch.putExtra(ExNavigation.CURRENCY_PARAM, curr);
        intentToEarningsSearch.putExtra(ExNavigation.ACC_NAME_PARAM, accName);
        contextToEarningsSearch.startActivity(intentToEarningsSearch);
        contextToEarningsSearch.finish();
    }

}
