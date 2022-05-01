package cm.abimmobiledev.mybudgetizer.nav;

import static cm.abimmobiledev.mybudgetizer.nav.ExNavigation.SEARCH_PARAM;

import android.app.Activity;
import android.content.Intent;

import cm.abimmobiledev.mybudgetizer.ui.debt.DebtBoardActivity;
import cm.abimmobiledev.mybudgetizer.ui.debt.DebtRegistrationActivity;
import cm.abimmobiledev.mybudgetizer.ui.debt.DebtsActivity;
import cm.abimmobiledev.mybudgetizer.ui.receivable.ReceivableBoardActivity;
import cm.abimmobiledev.mybudgetizer.ui.receivable.ReceivableRegistrationActivity;
import cm.abimmobiledev.mybudgetizer.ui.receivable.ReceivablesActivity;

public class ReceivNav {

    public static void openReceivablesHome(Activity contextToReceivableBoard, String acc, String curr) {
        Intent intentToRecDash = new Intent(contextToReceivableBoard, ReceivableBoardActivity.class);
        intentToRecDash.putExtra(ExNavigation.ACC_NAME_PARAM, acc);
        intentToRecDash.putExtra(ExNavigation.CURRENCY_PARAM, curr);
        contextToReceivableBoard.startActivity(intentToRecDash);
        contextToReceivableBoard.finish();
    }

    public static void openNewReceivable(Activity contextToNewReceivable, String acc, String curr) {
        Intent intentToNewReceivable = new Intent(contextToNewReceivable, ReceivableRegistrationActivity.class);
        intentToNewReceivable.putExtra(ExNavigation.CURRENCY_PARAM, curr);
        intentToNewReceivable.putExtra(ExNavigation.ACC_NAME_PARAM, acc);
        contextToNewReceivable.startActivity(intentToNewReceivable);
        contextToNewReceivable.finish();
    }


    public static void openReceivablesSearch(Activity contextToRecsSearch, String searchParam, String acc, String curr) {
        Intent intentToRecsSearch = new Intent(contextToRecsSearch, ReceivablesActivity.class);
        intentToRecsSearch.putExtra(ExNavigation.ACC_NAME_PARAM, acc);
        intentToRecsSearch.putExtra(ExNavigation.CURRENCY_PARAM, curr);
        intentToRecsSearch.putExtra(SEARCH_PARAM, searchParam);
        contextToRecsSearch.startActivity(intentToRecsSearch);
        contextToRecsSearch.finish();
    }
}
