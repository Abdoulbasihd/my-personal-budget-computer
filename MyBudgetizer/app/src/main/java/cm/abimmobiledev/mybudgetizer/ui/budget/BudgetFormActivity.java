package cm.abimmobiledev.mybudgetizer.ui.budget;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Account;
import cm.abimmobiledev.mybudgetizer.database.entity.Budget;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityBudgetFormBinding;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.nav.BudgetNav;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.useful.Util;
import cm.abimmobiledev.mybudgetizer.viewmodel.BudgetRegVM;

public class BudgetFormActivity extends AppCompatActivity {

    ActivityBudgetFormBinding budgetFormBinding;
    BudgetRegVM budgetRegVM;
    private final String TAG_B_FORM_REG = "BU_F_TAG";
    private String accountName;
    private String currency;

    AlertDialog.Builder budgetRegDialog;
    ProgressDialog budgetRegProgress;
    ProgressDialog balanceGettingProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        budgetRegVM = new BudgetRegVM();
        budgetFormBinding = DataBindingUtil.setContentView(this, R.layout.activity_budget_form);
        budgetFormBinding.setBudgetViewModel(budgetRegVM);
        budgetFormBinding.executePendingBindings();

        budgetInitByIntent(getIntent());

        budgetRegDialog = Util.initAlertDialogBuilder(this, getString(R.string.new_budget), getString(R.string.save_done));
        budgetRegDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            budgetRegVM.setEntitle("");
            budgetRegVM.setStartDate("");
            budgetRegVM.setEndDate("");
            budgetRegVM.setDescription("");
            budgetRegVM.setAmount("");
            budgetRegVM.setSticker("");
            budgetFormBinding.executePendingBindings();
        });

        budgetRegProgress = Util.initProgressDialog(this, getString(R.string.saving));
        balanceGettingProgress = Util.initProgressDialog(this, getString(R.string.saving));

        budgetFormBinding.pickDebutDate.setOnClickListener(debutDatePickerView -> this.pickStartingDate(Calendar.getInstance()));
        budgetFormBinding.pickEndDate.setOnClickListener(debutDatePickerView -> this.pickEndingDate(Calendar.getInstance()));

        budgetFormBinding.saveBudget.setOnClickListener(budgetSaveView -> {
            //0. Select balance .... cash ? bank ? mobile wallet ? ==> if cash sufficient, automatically use cash; then mobile wallet then bank... in this order
            //inform the user with wallet is used...

            //1. verify balance

            //2. insert budget if sufficient balance
            try {
                if (!requiredBudgetRegistrationDataFilled(budgetRegVM.getEntitle(), budgetRegVM.getAmount(), budgetRegVM.getStartDate(), budgetRegVM.getEndDate())) {
                    setBudgetRequiredField();
                    return;
                }

                budgetRegProgress.show();
                budgetDataBaseRegistrationService();

            } catch (BudgetizerGeneralException exception) {
                Log.d(TAG_B_FORM_REG, "onCreate: "+exception.getLocalizedMessage(), exception);
            }

            //3. update balance

        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        BudgetNav.openBudgetsHome(BudgetFormActivity.this, accountName, currency);
    }

    /**
            * pick date and set it to dateTimeEditText
     */
    public void pickStartingDate(Calendar c) {
        // Get Current Date
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    String currentDate = year + "/" + Util.getMonthFormatted(monthOfYear) + "/" + Util.getDayFormatted(dayOfMonth);
                    budgetFormBinding.debutDateEdit.setText(currentDate);

                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void pickEndingDate(Calendar c) {
        // Get Current Date
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog endDatePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    String endDate = year + "/" + Util.getMonthFormatted(monthOfYear) + "/" + Util.getDayFormatted(dayOfMonth);
                    budgetFormBinding.endDateEdit.setText(endDate);

                }, mYear, mMonth, mDay);
        endDatePickerDialog.show();
    }

    public  boolean requiredBudgetRegistrationDataFilled(String entitled, String amount, String startDate, String endDate) {
        try {
            if (Double.parseDouble(amount)<0)
                return  false;
        }catch (NumberFormatException exception) {
            return false;
        }
        return entitled != null && !entitled.isEmpty() && startDate != null && !startDate.isEmpty() &&  endDate !=null && !endDate.isEmpty();
    }

    public void setBudgetRequiredField() throws BudgetizerGeneralException {
        if (budgetRegVM==null)
            throw new BudgetizerGeneralException("not initialized");

        if (budgetRegVM.getEntitle().isEmpty())
            budgetFormBinding.entitledEdit.setError(getString(R.string.mandatory_field));

        if (budgetRegVM.getStartDate().isEmpty())
            budgetFormBinding.debutDateEdit.setError(getString(R.string.mandatory_field));

        if (budgetRegVM.getEndDate().isEmpty())
            budgetFormBinding.endDateEdit.setError(getString(R.string.mandatory_field));

        try {
            if (budgetRegVM.getAmount().isEmpty() || Double.parseDouble(budgetRegVM.getAmount())<0)
                budgetFormBinding.amountEdit.setError(getString(R.string.mandatory_field));
        }catch (NumberFormatException exception) {
            budgetFormBinding.amountEdit.setError(getString(R.string.must_be_a_number));
        }

    }

    public void budgetDataBaseRegistrationService(){

        ExecutorService budgetRegExecService = Executors.newSingleThreadExecutor();
        budgetRegExecService.execute(() -> {

            try {
                Budget budget = new Budget(budgetRegVM.getEntitle(), Double.parseDouble(budgetRegVM.getAmount()), budgetRegVM.getSticker(), budgetRegVM.getStartDate(), budgetRegVM.getEndDate());
                budget.setDescription(budgetRegVM.getDescription());

                BudgetizerAppDatabase appDatabaseBudgetReg = BudgetizerAppDatabase.getInstance(getApplicationContext());

                List<Account> accounts = appDatabaseBudgetReg.accountDAO().getAccounts();

                Account account = accounts.get(0);

                if ((account.getAmount()-account.getBudgetizedBalance())>=budget.getAmount()) {
                    appDatabaseBudgetReg.budgetDAO().insertAll(budget);

                    account.setBudgetizedBalance(account.getBudgetizedBalance()+budget.getAmount());
                    appDatabaseBudgetReg.accountDAO().update(account);
                    budgetRegDialog.setMessage(getString(R.string.saved));

                } else {
                    budgetRegDialog.setMessage(getString(R.string.insufficient_account_balance_to_create_this_budget));
                }

            }
            catch (Exception exception) {
                Log.d(TAG_B_FORM_REG, exception.getLocalizedMessage(), exception);
                runOnUiThread(() -> {
                    budgetRegProgress.dismiss();
                    budgetRegDialog.setMessage(getString(R.string.an_error_occured)+"\n"+exception.getLocalizedMessage());

                });
            }

            runOnUiThread(() -> {
                budgetRegDialog.setNegativeButton(getString(R.string.back), (dialog, which) -> BudgetNav.openBudgetsHome(BudgetFormActivity.this, accountName, currency));
                budgetRegDialog.show();
                budgetRegProgress.dismiss();
            });


        });

    }

    /**
     * <h1>Update accounts when expended</h1>
     * @param acc Account
     * @param amountExpended double amount expended
     * @return Account updated
     * @throws BudgetizerGeneralException
     */
    public static Account updateSubAccounts(Account acc, double amountExpended) throws BudgetizerGeneralException {

        if (acc.getCashBalance() - amountExpended >=0)
            acc.setCashBalance(acc.getCashBalance()-amountExpended);

        else if ((acc.getCashBalance()+acc.getBankBalance())-amountExpended >=0){
            double remains = acc.getCashBalance() + acc.getMobileWalletBalance() - amountExpended;
            acc.setMobileWalletBalance(remains);
            acc.setCashBalance(0);

        }else if (acc.getCashBalance()+acc.getBankBalance()+acc.getMobileWalletBalance() - amountExpended >=0) {
            double bRemains = acc.getCashBalance() + acc.getBankBalance() + acc.getMobileWalletBalance()- amountExpended;
            acc.setBankBalance(bRemains);
            acc.setCashBalance(0);
            acc.setMobileWalletBalance(0);

        }else
            throw new BudgetizerGeneralException("Insufficient balance");

        return acc;
    }

    public void  budgetInitByIntent(Intent budgetIntent) {
        accountName = budgetIntent.getStringExtra(ExNavigation.ACC_NAME_PARAM);
        currency = budgetIntent.getStringExtra(ExNavigation.CURRENCY_PARAM);
    }



}