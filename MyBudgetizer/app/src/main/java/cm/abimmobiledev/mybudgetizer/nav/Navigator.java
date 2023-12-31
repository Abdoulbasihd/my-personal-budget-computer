package cm.abimmobiledev.mybudgetizer.nav;

import android.app.Activity;
import android.content.Intent;

import cm.abimmobiledev.mybudgetizer.ui.account.AccountActivity;

public class Navigator {

    public static void openAccountBoard(Activity contextToAccountBoard, String acc, String curr) {
        Intent intentToAccountDash = new Intent(contextToAccountBoard, AccountActivity.class);
        intentToAccountDash.putExtra(ExNavigation.ACC_NAME_PARAM, acc);
        intentToAccountDash.putExtra(ExNavigation.CURRENCY_PARAM, curr);
        contextToAccountBoard.startActivity(intentToAccountDash);
        contextToAccountBoard.finish();
    }
}
