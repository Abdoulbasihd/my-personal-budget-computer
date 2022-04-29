package cm.abimmobiledev.mybudgetizer.ui.budget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Budget;
import cm.abimmobiledev.mybudgetizer.database.entity.Debt;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityBudgetBoardBinding;
import cm.abimmobiledev.mybudgetizer.nav.BudgetNav;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.ui.budget.adapter.BudgetAdapter;
import cm.abimmobiledev.mybudgetizer.ui.debt.adapter.DebtAdapter;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class BudgetBoardActivity extends AppCompatActivity {

    ActivityBudgetBoardBinding budgetBoardBinding;

    ProgressDialog budgetsListProgress;
    ProgressDialog budgetBalanceBoardResumeProgress;
    List<Budget> budgets ;

    private static final String BUDGET_BOARD_TAG = "BB_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        budgetBoardBinding = DataBindingUtil.setContentView(this, R.layout.activity_budget_board);

        budgetsListProgress = Util.initProgressDialog(this, getString(R.string.looking_up));
        budgetBalanceBoardResumeProgress = Util.initProgressDialog(this, getString(R.string.looking_up));

        budgetBoardBinding.newBudget.setOnClickListener(budgetNewView -> BudgetNav.openNewBudget(BudgetBoardActivity.this));

        budgetBoardBinding.showMore.setOnClickListener(moreBudgetView -> {

        });

        //getting list of budgets (last 20 elements is ok nor)
        getLastBudgets(20);

        //todo : complete with the balance summary
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExNavigation.openMainHome(BudgetBoardActivity.this);
    }

    public void getLastBudgets(int numberOfElements) {

        budgetsListProgress.show();
        ExecutorService lastExpensesServ = Executors.newSingleThreadExecutor();

        lastExpensesServ.execute(() -> {
            BudgetizerAppDatabase lastBudgetsAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());
            budgets = lastBudgetsAppDatabase.budgetDAO().getLastBudgets(numberOfElements);

            BudgetAdapter budgetAdapter = new BudgetAdapter(budgets);

            runOnUiThread(() -> {


                budgetBoardBinding.myBudgetsRecycler.setHasFixedSize(true);
                budgetBoardBinding.myBudgetsRecycler.setLayoutManager(new LinearLayoutManager(this));
                budgetBoardBinding.myBudgetsRecycler.setAdapter(budgetAdapter);
               // budgetBoardBinding.myBudgetsRecycler.setVerticalScrollBarEnabled(true);
               // budgetBoardBinding.myBudgetsRecycler.setVerticalFadingEdgeEnabled(true);
             //   budgetBoardBinding.myBudgetsRecycler.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);

                //dismiss progress here
                budgetsListProgress.dismiss();

                if (budgets!=null && !budgets.isEmpty()) {
                    //create a method for all this...
                    budgetBoardBinding.layoutBudgets.setVisibility(View.VISIBLE);
                    budgetBoardBinding.noBudgetFound.setVisibility(View.GONE);
                    Log.d(BUDGET_BOARD_TAG, "get last budgets :  expenses size is "+budgets.size());
                }
                else {
                    budgetBoardBinding.layoutBudgets.setVisibility(View.GONE);
                    budgetBoardBinding.noBudgetFound.setVisibility(View.VISIBLE);
                }
            });


        });

    }

}