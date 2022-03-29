package cm.abimmobiledev.mybudgetizer.nav;

import static cm.abimmobiledev.mybudgetizer.nav.ExNavigation.SEARCH_PARAM;

import android.app.Activity;
import android.content.Intent;

import cm.abimmobiledev.mybudgetizer.ui.earning.EarningBoardActivity;
import cm.abimmobiledev.mybudgetizer.ui.earning.EarningRegistrationActivity;
import cm.abimmobiledev.mybudgetizer.ui.earning.EarningsActivity;

public class IncNavigator {

    public static void openNewIncome(Activity contextToNewIncome) {
        Intent intentToNewInc = new Intent(contextToNewIncome, EarningRegistrationActivity.class);
        contextToNewIncome.startActivity(intentToNewInc);
        contextToNewIncome.finish();
    }


    public static void openEarningsHome(Activity contextToEarningsDash) {
        Intent intentToIncomeDashB = new Intent(contextToEarningsDash, EarningBoardActivity.class);
        contextToEarningsDash.startActivity(intentToIncomeDashB);
        contextToEarningsDash.finish();
    }

    public static void openEarningsSearch(Activity contextToEarningsSearch, String searchParam) {
        Intent intentToEarningsSearch = new Intent(contextToEarningsSearch, EarningsActivity.class);
        intentToEarningsSearch.putExtra(SEARCH_PARAM, searchParam);
        contextToEarningsSearch.startActivity(intentToEarningsSearch);
        contextToEarningsSearch.finish();
    }

}
