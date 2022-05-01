package cm.abimmobiledev.mybudgetizer.nav;

import android.app.Activity;
import android.content.Intent;

import cm.abimmobiledev.mybudgetizer.ui.budget.BudgetBoardActivity;
import cm.abimmobiledev.mybudgetizer.ui.budget.BudgetFormActivity;

public class BudgetNav {

    public static void openNewBudget(Activity contextToBudgetNew, String acc, String curr) {
        Intent intentBudgetNew = new Intent(contextToBudgetNew, BudgetFormActivity.class);
        intentBudgetNew.putExtra(ExNavigation.ACC_NAME_PARAM, acc);
        intentBudgetNew.putExtra(ExNavigation.CURRENCY_PARAM, curr);
        contextToBudgetNew.startActivity(intentBudgetNew);
        contextToBudgetNew.finish();
    }

    public static void openBudgetsHome(Activity contextToBudgetBoard, String acc, String curr) {
        Intent intentToBudgetBoard = new Intent(contextToBudgetBoard, BudgetBoardActivity.class);
        intentToBudgetBoard.putExtra(ExNavigation.CURRENCY_PARAM, curr);
        intentToBudgetBoard.putExtra(ExNavigation.ACC_NAME_PARAM, acc);
        contextToBudgetBoard.startActivity(intentToBudgetBoard);
        contextToBudgetBoard.finish();
    }

}
