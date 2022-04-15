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

    public static void openReceivablesHome(Activity contextToReceivableBoard) {
        Intent intentToRecDash = new Intent(contextToReceivableBoard, ReceivableBoardActivity.class);
        contextToReceivableBoard.startActivity(intentToRecDash);
        contextToReceivableBoard.finish();
    }

    public static void openNewReceivable(Activity contextToNewReceivable) {
        Intent intentToNewReceivable = new Intent(contextToNewReceivable, ReceivableRegistrationActivity.class);
        contextToNewReceivable.startActivity(intentToNewReceivable);
        contextToNewReceivable.finish();
    }


    public static void openReceivablesSearch(Activity contextToRecsSearch, String searchParam) {
        Intent intentToRecsSearch = new Intent(contextToRecsSearch, ReceivablesActivity.class);
        intentToRecsSearch.putExtra(SEARCH_PARAM, searchParam);
        contextToRecsSearch.startActivity(intentToRecsSearch);
        contextToRecsSearch.finish();
    }
}
