package cm.abimmobiledev.mybudgetizer.nav;

import android.app.Activity;
import android.content.Intent;

import cm.abimmobiledev.mybudgetizer.ui.budget.BudgetBoardActivity;
import cm.abimmobiledev.mybudgetizer.ui.budget.BudgetFormActivity;

public class BudgetNav {

    public static void openNewBudget(Activity contextToBudgetNew) {
        Intent intentBudgetNew = new Intent(contextToBudgetNew, BudgetFormActivity.class);
        contextToBudgetNew.startActivity(intentBudgetNew);
        contextToBudgetNew.finish();
    }

    public static void openBudgetsHome(Activity contextToBudgetBoard) {
        Intent intentToBudgetBoard = new Intent(contextToBudgetBoard, BudgetBoardActivity.class);
        contextToBudgetBoard.startActivity(intentToBudgetBoard);
        contextToBudgetBoard.finish();
    }

}
