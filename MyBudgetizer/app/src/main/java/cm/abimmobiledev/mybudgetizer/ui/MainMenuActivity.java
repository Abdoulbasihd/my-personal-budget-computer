package cm.abimmobiledev.mybudgetizer.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityMainMenuBinding;
import cm.abimmobiledev.mybudgetizer.nav.BudgetNav;
import cm.abimmobiledev.mybudgetizer.nav.DebtNavigator;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.nav.IncNavigator;
import cm.abimmobiledev.mybudgetizer.nav.ReceivNav;

public class MainMenuActivity extends AppCompatActivity {

    ActivityMainMenuBinding mainMenuBinding;

    private String accountName;
    private String currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainMenuBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_menu);

        mainInitByIntent(getIntent());

        //implement this group first....
        mainMenuBinding.cardBudget.setOnClickListener(aboutView -> BudgetNav.openBudgetsHome(MainMenuActivity.this, accountName, currency));

        mainMenuBinding.cardDebts.setOnClickListener(aboutView -> DebtNavigator.openDebtsHome(MainMenuActivity.this, accountName, currency));

        mainMenuBinding.cardReceivable.setOnClickListener(aboutView -> ReceivNav.openReceivablesHome(MainMenuActivity.this, accountName, currency));

        mainMenuBinding.cardAboutApp.setOnClickListener(aboutView -> {
            BottomSheetDialog aboutBD = new BottomSheetDialog(MainMenuActivity.this);
            aboutBD.setContentView(R.layout.bottom_sheet_about_app);
            aboutBD.show();
        });
        mainMenuBinding.cardAccountManager.setOnClickListener(aboutView -> Snackbar.make(aboutView, getString(R.string.not_yet_implemented), Snackbar.LENGTH_LONG).show());

        mainMenuBinding.cardExpenses.setOnClickListener(expensesView -> ExNavigation.openExpensesHome(MainMenuActivity.this, accountName, currency));
        mainMenuBinding.cardEarning.setOnClickListener(earnView -> IncNavigator.openEarningsHome(MainMenuActivity.this, accountName, currency));
    }

    public void  mainInitByIntent(Intent mainIntent) {
       // if (mainIntent==null)
         //   throw new BudgetizerGeneralException(getString(R.string.page_not_initialized));

        accountName = mainIntent.getStringExtra(ExNavigation.ACC_NAME_PARAM);
        currency = mainIntent.getStringExtra(ExNavigation.CURRENCY_PARAM);
    }

}