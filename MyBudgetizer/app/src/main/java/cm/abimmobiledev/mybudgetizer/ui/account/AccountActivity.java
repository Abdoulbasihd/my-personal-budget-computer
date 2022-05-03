package cm.abimmobiledev.mybudgetizer.ui.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Account;
import cm.abimmobiledev.mybudgetizer.database.entity.Budget;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityAccountBinding;
import cm.abimmobiledev.mybudgetizer.nav.BudgetNav;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.ui.budget.BudgetBoardActivity;
import cm.abimmobiledev.mybudgetizer.ui.budget.adapter.BudgetAdapter;
import cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseRegistrationActivity;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class AccountActivity extends AppCompatActivity {

    ActivityAccountBinding accountBinding;

    private String accountName;
    private String currency;

    ProgressDialog budgetsListProgress;
    ProgressDialog budgetBalanceBoardResumeProgress;
    List<Budget> budgets ;

    private static final String AC_BOARD_TAG = "BB_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountBinding = DataBindingUtil.setContentView(this, R.layout.activity_account);

        accBoardInitByIntent(getIntent());
        budgetsListProgress = Util.initProgressDialog(this, getString(R.string.looking_up));
        budgetBalanceBoardResumeProgress = Util.initProgressDialog(this, getString(R.string.looking_up));

        //getting list of budgets (last 20 elements is ok nor) ==> nok, get all
        getBudgetsAndComputeBudgetized();

        accountBinding.welcomeRelativeLayout.name.setText(accountName);
        accountBinding.welcomeRelativeLayout.nameIndicator.setText(String.valueOf(accountName.toUpperCase(Locale.ROOT).charAt(0)));

    }


    public void getBudgetsAndComputeBudgetized() {

        budgetsListProgress.show();
        ExecutorService expendedServ = Executors.newSingleThreadExecutor();

        expendedServ.execute(() -> {
            BudgetizerAppDatabase unexpiredBudgetAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());
            budgets = unexpiredBudgetAppDatabase.budgetDAO().getBudgets();
            double goingOnBudgetsAmount = getUnexpiredBudgetsTotalAmount(budgets, Util.getCurrentDate());

            runOnUiThread(() -> {

                accountBinding.allBudgetsValue.setText(String.valueOf(goingOnBudgetsAmount));
                accountBinding.allBudgetsCurrency.setText(currency);
                getAccountBalance(budgets);

                //dismiss progress here
                budgetsListProgress.dismiss();

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
        ExecutorService subAccountExecServ = Executors.newSingleThreadExecutor();
        subAccountExecServ.execute(() -> {
            BudgetizerAppDatabase subAccountsAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());
            List<Account> subAccounts = subAccountsAppDatabase.accountDAO().getAccounts();

            if (subAccounts == null || subAccounts.isEmpty())
                return;

            Account account = subAccounts.get(0);
            double goingOnBudgetsAmount = getUnexpiredBudgetsTotalAmount(budgetList, Util.getCurrentDate());
            double unbudgetized = account.getAmount() - goingOnBudgetsAmount;

            runOnUiThread(() -> {
                accountBinding.cashValue.setText(String.valueOf(account.getCashBalance()));
                accountBinding.cashCurrency.setText(currency);
                accountBinding.bankValue.setText(String.valueOf(account.getBankBalance()));
                accountBinding.bankCurreny.setText(currency);
                accountBinding.mobileWalletValue.setText(String.valueOf(account.getMobileWalletBalance()));
                accountBinding.mobileWalletCurrency.setText(currency);
                accountBinding.unbugetizedValue.setText(String.valueOf(unbudgetized));
                accountBinding.unbugetizedCurrency.setText(currency);

                budgetBalanceBoardResumeProgress.dismiss();
            });

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExNavigation.openMainHome(AccountActivity.this, accountName, currency);
    }

    public void  accBoardInitByIntent(Intent bBoardIntent) {
        accountName = bBoardIntent.getStringExtra(ExNavigation.ACC_NAME_PARAM);
        currency = bBoardIntent.getStringExtra(ExNavigation.CURRENCY_PARAM);
    }

}