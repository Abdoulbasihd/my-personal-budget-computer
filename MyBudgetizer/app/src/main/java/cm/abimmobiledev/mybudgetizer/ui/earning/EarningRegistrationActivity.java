package cm.abimmobiledev.mybudgetizer.ui.earning;

import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseRegistrationActivity.mandatoryFilled;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Account;
import cm.abimmobiledev.mybudgetizer.database.entity.Earning;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityEarningRegistrationBinding;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.nav.IncNavigator;
import cm.abimmobiledev.mybudgetizer.useful.Util;
import cm.abimmobiledev.mybudgetizer.viewmodel.IncomeRegViewModel;

public class EarningRegistrationActivity extends AppCompatActivity {

    ActivityEarningRegistrationBinding earningRegistrationBinding;

    AlertDialog.Builder incomeRegDialog;
    ProgressDialog incomeRegProgress;

    IncomeRegViewModel incomeRegViewModel;

    private static final String INC_REG_TAG = "INC_REG";
    private String accountName;
    private String currency;

    List<String> accountTypes;
    ArrayAdapter<String> accountAdapter;
    int selectedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        incomeRegProgress = Util.initProgressDialog(this, getString(R.string.saving));

        incomeRegViewModel = new IncomeRegViewModel();

        earningRegistrationBinding = DataBindingUtil.setContentView(this, R.layout.activity_earning_registration);
        earningRegistrationBinding.setIncomeModel(incomeRegViewModel);
        earningRegistrationBinding.executePendingBindings();

        earnRegInitByIntent(getIntent());
        accountTypes = getAccountTypes();
        accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountTypes);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        earningRegistrationBinding.spinnerAccounts.setAdapter(accountAdapter);
        earningRegistrationBinding.spinnerAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAccount = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        incomeRegDialog = Util.initAlertDialogBuilder(this, "Gain", getString(R.string.save_done));
        incomeRegDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            incomeRegViewModel.setEntitle("");
            incomeRegViewModel.setIncomeDateTime("");
            incomeRegViewModel.setReasonOrDesc("");
            incomeRegViewModel.setSourceFunds("");
            earningRegistrationBinding.stickerEdit.setText("");
            earningRegistrationBinding.executePendingBindings();
        });

        earningRegistrationBinding.pickDateAndTime.setOnClickListener(pickerView -> pickDate());

        earningRegistrationBinding.saveIncome.setOnClickListener(saveView -> {


            String entitle = incomeRegViewModel.getEntitle().trim();
            String dateTime = incomeRegViewModel.getIncomeDateTime().trim();
            String amount = incomeRegViewModel.getAmount().trim();
            String fundSource = incomeRegViewModel.getSourceFunds().trim();
            String reason = incomeRegViewModel.getReasonOrDesc().trim();
            String sticker = earningRegistrationBinding.stickerEdit.getText().toString();

            try {
                if (mandatoryFilled(entitle, amount, dateTime)) {
                    incomeRegProgress.show();
                    incomeDatabaseInsert(entitle, amount, dateTime, fundSource, reason, sticker);

                    return;
                }

                setBlankEditErrors(entitle, amount, dateTime);
            }
            catch (BudgetizerGeneralException exception) {
                Log.d(INC_REG_TAG, "onCreate: "+exception.getLocalizedMessage(), exception);
                setBlankEditErrors(entitle, null, dateTime);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        IncNavigator.openEarningsHome(EarningRegistrationActivity.this, accountName, currency);
    }

    /**
     * pick date and set it to dateTimeEditText
     */
    public void pickDate() {
        // Get Current Date
        final Calendar incomeCal = Calendar.getInstance();
        final int mYear = incomeCal.get(Calendar.YEAR);
        final int mMonth = incomeCal.get(Calendar.MONTH);
        final int mDay = incomeCal.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    String currentDate = year + "/" + Util.getMonthFormatted(monthOfYear) + "/" + Util.getDayFormatted(dayOfMonth);
                    earningRegistrationBinding.dateTimeEdit.setText(currentDate);

                    pickTime(incomeCal, currentDate);

                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    /**
     * pick time and set date and time picked to edit text
     * @param incomeCalendar Calendar object
     * @param currentDate String, picked date in format yyyy-mm-dd
     */
    public void pickTime(Calendar incomeCalendar, String currentDate) {
        final int hh = incomeCalendar.get(Calendar.HOUR);
        final int mm = incomeCalendar.get(Calendar.MINUTE);
        final int ss = incomeCalendar.get(Calendar.SECOND);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {

            String currentDateTime = currentDate+" "+hourOfDay + ":" + minute+":"+ss;
            earningRegistrationBinding.dateTimeEdit.setText(currentDateTime);

        }, hh, mm, true);

        timePickerDialog.show();

    }



    /**
     * set errors on mandatory's edit text
     * @param entitle String
     * @param amount String
     * @param dateTime String
     */
    public void setBlankEditErrors (String entitle, String amount, String dateTime) {
        if (entitle==null || entitle.isEmpty())
            earningRegistrationBinding.entitledEdit.setError(getString(R.string.mandatory_field));

        if (dateTime==null || dateTime.isEmpty())
            earningRegistrationBinding.dateTimeEdit.setError(getString(R.string.mandatory_field));

        if (amount==null || amount.isEmpty())
            earningRegistrationBinding.amountEdit.setError(getString(R.string.amount_must_be_a_number_and_is_mandatory));
    }

    /**
     * insert an expense in data base
     * @param entitle String
     * @param amount String
     * @param dateReg String
     * @param fundSource String
     * @param reason String
     * @param sticker string
     */
    public void incomeDatabaseInsert(String entitle, String amount, String dateReg, String fundSource, String reason, String sticker) {

        ExecutorService insertEarnExec = Executors.newSingleThreadExecutor();
        insertEarnExec.execute(() -> {

            try {
                Earning earningNew = new Earning(entitle, Double.parseDouble(amount), fundSource, dateReg,  reason, sticker);


                BudgetizerAppDatabase appDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());

                //get the account and update amount value by adding the earned ...
                Account acc =
                        updateCorrectWallet(
                                appDatabase.accountDAO().getAccounts().get(0),
                                earningNew.getAmount(),
                                selectedAccount
                        );

                //insert the earning and update acc in data base
                appDatabase.earningDAO().insertAll(earningNew);
                appDatabase.accountDAO().update(acc);



            }
            catch (Exception exception) {
                runOnUiThread(() -> {
                    incomeRegDialog.setMessage( getString(R.string.an_error_occured)+"\n"+exception.getLocalizedMessage());
                    incomeRegProgress.dismiss();
                    incomeRegDialog.show();
                });
                return;
            }

            runOnUiThread(() -> {
                incomeRegDialog.setMessage(getString(R.string.saved));
                incomeRegDialog.setNegativeButton(getString(R.string.back), (dialog, which) -> IncNavigator.openEarningsHome(EarningRegistrationActivity.this, accountName, currency));
                incomeRegDialog.show();
                incomeRegProgress.dismiss();
            });




        });

    }

    public void  earnRegInitByIntent(Intent earnRIntent) {
        accountName = earnRIntent.getStringExtra(ExNavigation.ACC_NAME_PARAM);
        currency = earnRIntent.getStringExtra(ExNavigation.CURRENCY_PARAM);
    }

    public static Account updateCorrectWallet(Account acc, double amount, int wallet){
        if (wallet==2)
            acc.setBankBalance(acc.getBankBalance()+amount);
        else if (wallet==1)
            acc.setMobileWalletBalance(acc.getMobileWalletBalance()+amount);
        else
            acc.setCashBalance(acc.getCashBalance()+amount);

        acc.setAmount(acc.getBankBalance()+acc.getMobileWalletBalance()+acc.getCashBalance());

        return acc;
    }

    public static List<String> getAccountTypes(){
        List<String> aT = new ArrayList<>();
        aT.add("Cash");
        aT.add("Mobile wallet (mobile money)");
        aT.add("Bank");
        return aT;
    }


}