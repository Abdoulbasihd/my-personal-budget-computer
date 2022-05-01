package cm.abimmobiledev.mybudgetizer.ui.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Account;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityAccountFormBinding;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class AccountFormActivity extends AppCompatActivity {

    ActivityAccountFormBinding accountFormBinding;
    ArrayAdapter<String> currenciesSpinnerAdapter;
    List<String> currencies;
    int selectedCurrency;

    ProgressDialog accountSavingProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        accountFormBinding = DataBindingUtil.setContentView(this, R.layout.activity_account_form);
        accountSavingProgress = Util.initProgressDialog(this, getString(R.string.saving));
        accountFormBinding.accountContentLoading.setVisibility(View.VISIBLE);
        accountFormBinding.accountFormOnCard.setVisibility(View.GONE);
        accountFormBinding.saveAccount.setVisibility(View.GONE);



        currencies = getCurrenciesDefault();
        currenciesSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        currenciesSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        accountFormBinding.currencySpinner.setAdapter(currenciesSpinnerAdapter);

        accountFormBinding.currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCurrency = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //nothing to do here
            }
        });

        accountFormBinding.saveAccount.setOnClickListener(v -> {
            String accName = accountFormBinding.accountNameEdit.getText().toString().trim();
            String currency = currencies.get(selectedCurrency);

            accountSavingProgress.show();
            insertNewAccount(accName, currency);

        });

        verifyAccountExist();


    }

    public void insertNewAccount(String accountName, String currency) {
        ExecutorService accountRegExecService = Executors.newSingleThreadExecutor();
        accountRegExecService.execute(() -> {

            Account account = new Account(accountName, currency);
            BudgetizerAppDatabase appDatabaseAccountReg = BudgetizerAppDatabase.getInstance(getApplicationContext());
            appDatabaseAccountReg.accountDAO().insertAll(account);

            runOnUiThread(() -> {
                accountSavingProgress.dismiss();
                ExNavigation.openMainHome(AccountFormActivity.this, accountName, currency);
            });
        });
    }

    public void verifyAccountExist(){
        ExecutorService accountVerificationExecService = Executors.newSingleThreadExecutor();
        accountVerificationExecService.execute(()->{
            BudgetizerAppDatabase appDatabaseAccountMan = BudgetizerAppDatabase.getInstance(getApplicationContext());
            List<Account> accounts = appDatabaseAccountMan.accountDAO().getAccounts();

            if(accounts!=null && !accounts.isEmpty()) {
                //open main menu here
                Account acc = accounts.get(0);
                ExNavigation.openMainHome(AccountFormActivity.this, acc.getEntitled(), acc.getCurrency());
            } else {
                //dismiss loader here
                accountFormBinding.accountContentLoading.setVisibility(View.GONE);
                accountFormBinding.accountFormOnCard.setVisibility(View.VISIBLE);
                accountFormBinding.saveAccount.setVisibility(View.VISIBLE);
            }
        });
    }

    public List<String> getCurrenciesDefault(){
        List<String> curr = new ArrayList<>();
        curr.add("F. CFA");
        curr.add("N");
        curr.add("€");
        curr.add("£");
        curr.add("$");

        return curr;
    }
}