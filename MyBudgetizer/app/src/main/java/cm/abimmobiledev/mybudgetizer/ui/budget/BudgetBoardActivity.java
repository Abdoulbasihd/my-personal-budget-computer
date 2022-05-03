package cm.abimmobiledev.mybudgetizer.ui.budget;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Account;
import cm.abimmobiledev.mybudgetizer.database.entity.Budget;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityBudgetBoardBinding;
import cm.abimmobiledev.mybudgetizer.nav.BudgetNav;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.ui.budget.adapter.BudgetAdapter;
import cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseRegistrationActivity;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class BudgetBoardActivity extends AppCompatActivity {

    ActivityBudgetBoardBinding budgetBoardBinding;
    private String accountName;
    private String currency;

    ProgressDialog budgetsListProgress;
    ProgressDialog budgetBalanceBoardResumeProgress;
    List<Budget> budgets ;

    private static final String BUDGET_BOARD_TAG = "BB_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        budgetBoardBinding = DataBindingUtil.setContentView(this, R.layout.activity_budget_board);
        bBoardInitByIntent(getIntent());
        budgetsListProgress = Util.initProgressDialog(this, getString(R.string.looking_up));
        budgetBalanceBoardResumeProgress = Util.initProgressDialog(this, getString(R.string.looking_up));

        budgetBoardBinding.newBudget.setOnClickListener(budgetNewView -> BudgetNav.openNewBudget(BudgetBoardActivity.this, accountName, currency));

        budgetBoardBinding.showMore.setOnClickListener(moreBudgetView -> {

        });

        //getting list of budgets (last 20 elements is ok nor) ==> nok, get all
        getLastBudgets();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExNavigation.openMainHome(BudgetBoardActivity.this, accountName, currency);
    }

    public void getLastBudgets() {

        budgetsListProgress.show();
        ExecutorService lastExpensesServ = Executors.newSingleThreadExecutor();

        lastExpensesServ.execute(() -> {
            BudgetizerAppDatabase lastBudgetsAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());
            budgets = lastBudgetsAppDatabase.budgetDAO().getBudgets();
            double goingOnBudgetsAmount = getUnexpiredBudgetsTotalAmount(budgets, Util.getCurrentDate());

            BudgetAdapter budgetAdapter = new BudgetAdapter(budgets);

            runOnUiThread(() -> {


                budgetBoardBinding.myBudgetsRecycler.setHasFixedSize(true);
                budgetBoardBinding.myBudgetsRecycler.setLayoutManager(new LinearLayoutManager(this));
                budgetBoardBinding.myBudgetsRecycler.setAdapter(budgetAdapter);
                budgetBoardBinding.myBudgetsRecycler.setVerticalScrollBarEnabled(true);
                budgetBoardBinding.myBudgetsRecycler.setVerticalFadingEdgeEnabled(true);
                budgetBoardBinding.myBudgetsRecycler.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);

                budgetBoardBinding.accountSummary.allBudgetsValue.setText(String.valueOf(goingOnBudgetsAmount));
                budgetBoardBinding.accountSummary.allBudgetsCurrency.setText(currency);
                getAccountBalance(budgets);

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

    public static double getUnexpiredBudgetsTotalAmount(List<Budget> budgets, String currentDate){

        List<Budget> filteredBudgets = ExpenseRegistrationActivity.filterUnexpiredBudgets(budgets, currentDate);


        double totalAmount=0;

        for (int counter=0; counter<budgets.size(); counter++){
            totalAmount = totalAmount + filteredBudgets.get(counter).getAmount();
        }

        return totalAmount;
    }

    public void getAccountBalance(List<Budget> budgetList) {
        budgetBalanceBoardResumeProgress.show();
        ExecutorService accountInfoExecServ = Executors.newSingleThreadExecutor();
        accountInfoExecServ.execute(() -> {
            BudgetizerAppDatabase accountAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());
            List<Account> accounts = accountAppDatabase.accountDAO().getAccounts();

            if (accounts == null || accounts.isEmpty())
                return;

            Account account = accounts.get(0);
            double goingOnBudgetsAmount = getUnexpiredBudgetsTotalAmount(budgetList, Util.getCurrentDate());
            double unbudgetized = account.getAmount() - goingOnBudgetsAmount;

            runOnUiThread(() -> {
                budgetBoardBinding.accountSummary.cashValue.setText(String.valueOf(account.getCashBalance()));
                budgetBoardBinding.accountSummary.cashCurrency.setText(currency);
                budgetBoardBinding.accountSummary.bankValue.setText(String.valueOf(account.getBankBalance()));
                budgetBoardBinding.accountSummary.bankCurreny.setText(currency);
                budgetBoardBinding.accountSummary.mobileWalletValue.setText(String.valueOf(account.getMobileWalletBalance()));
                budgetBoardBinding.accountSummary.mobileWalletCurrency.setText(currency);
                budgetBoardBinding.accountSummary.unbugetizedValue.setText(String.valueOf(unbudgetized));
                budgetBoardBinding.accountSummary.unbugetizedCurrency.setText(currency);

                budgetBalanceBoardResumeProgress.dismiss();
            });

        });

    }

    public void  bBoardInitByIntent(Intent bBoardIntent) {
        accountName = bBoardIntent.getStringExtra(ExNavigation.ACC_NAME_PARAM);
        currency = bBoardIntent.getStringExtra(ExNavigation.CURRENCY_PARAM);
    }


}