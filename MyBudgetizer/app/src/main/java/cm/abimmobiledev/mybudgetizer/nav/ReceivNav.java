package cm.abimmobiledev.mybudgetizer.nav;

import android.app.Activity;
import android.content.Intent;

import cm.abimmobiledev.mybudgetizer.ui.receivable.ReceivableBoardActivity;

public class ReceivNav {

    public static void openReceivablesHome(Activity contextToReceivableBoard) {
        Intent intentToRecDash = new Intent(contextToReceivableBoard, ReceivableBoardActivity.class);
        contextToReceivableBoard.startActivity(intentToRecDash);
        contextToReceivableBoard.finish();
    }
}
