package cm.abimmobiledev.mybudgetizer.ui.receivable;

import static cm.abimmobiledev.mybudgetizer.ui.earning.EarningRegistrationActivity.getAccountTypes;
import static cm.abimmobiledev.mybudgetizer.ui.earning.EarningRegistrationActivity.updateCorrectWallet;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Account;
import cm.abimmobiledev.mybudgetizer.database.entity.Receivable;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityReceivableRegistrationBinding;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.nav.IncNavigator;
import cm.abimmobiledev.mybudgetizer.nav.ReceivNav;
import cm.abimmobiledev.mybudgetizer.ui.earning.EarningRegistrationActivity;
import cm.abimmobiledev.mybudgetizer.useful.Util;
import cm.abimmobiledev.mybudgetizer.viewmodel.RecRegVM;

public class ReceivableRegistrationActivity extends AppCompatActivity {

    ActivityReceivableRegistrationBinding receivableRegistrationBinding;

    RecRegVM recRegVM;
    AlertDialog.Builder recRegDialog;
    ProgressDialog recRegProgress;
    String accountName;
    String currency;

    ArrayAdapter<String> accountAdapter;
    int selectedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recRegVM = new RecRegVM();

        recRegProgress = Util.initProgressDialog(this, getString(R.string.saving));

        recRegDialog = Util.initAlertDialogBuilder(this, "Nouvelle Créance", getString(R.string.save_done));
        recRegDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            recRegVM.setEntitle("");
            recRegVM.setAmount("");
            recRegVM.setDescription("");
            recRegVM.setLoanDate("");
            recRegVM.setTelltale("");
            recRegVM.setDebtorContact("");
            recRegVM.setDebtorNames("");
            recRegVM.setDueDate("");
            recRegVM.setDisbursementDate("");
            receivableRegistrationBinding.executePendingBindings();
        });


        receivableRegistrationBinding = DataBindingUtil.setContentView(this, R.layout.activity_receivable_registration);
        receivableRegistrationBinding.setReceivable(recRegVM);
        receivableRegistrationBinding.executePendingBindings();

        receivableRegistrationBinding.saveReceivable.setOnClickListener(saveRecView -> {
            if (!recRequiredFilled(recRegVM.getEntitle(), recRegVM.getAmount(), recRegVM.getDebtorNames(), recRegVM.getDebtorContact(), recRegVM.getLoanDate(), recRegVM.getDueDate())){
                setRecRequiredUnfilled(recRegVM.getEntitle(), recRegVM.getAmount(), recRegVM.getDebtorNames(), recRegVM.getDebtorContact(), recRegVM.getLoanDate(), recRegVM.getDueDate());
                return;
            }

            insertReceivable();


        });

        receivableRegistrationBinding.pickLoaningDateAndTime.setOnClickListener(loanDateView -> pickLoaningDate(Calendar.getInstance()));
        receivableRegistrationBinding.pickDisbursementDueDate.setOnClickListener(dueDateView -> pickDueDate(Calendar.getInstance()));
        receivableRegistrationBinding.disbursementDateAndTime.setOnClickListener(dueDateView -> pickPaymentDate(Calendar.getInstance()));

        recRecInitByIntent(getIntent());

        accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getAccountTypes());
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        receivableRegistrationBinding.spinnerAccounts.setAdapter(accountAdapter);
        receivableRegistrationBinding.spinnerAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedAccount = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ReceivNav.openReceivablesHome(this, accountName, currency);
    }

    /**
     * pick date and set it to dateTimeEditText
     */
    public void pickLoaningDate(Calendar c) {
        // Get Current Date
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    String currentDate = year + "/" + Util.getMonthFormatted(monthOfYear) + "/" + Util.getDayFormatted(dayOfMonth);
                    receivableRegistrationBinding.loaningDateTimeEdit.setText(currentDate);

                    pickLoaningTime(c, currentDate);

                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    /**
     * pick time and set date and time picked to edit text
     * @param cal Calendar object
     * @param currentDate String, picked date in format yyyy-mm-dd
     */
    public void pickLoaningTime(Calendar cal, String currentDate) {
        final int hh = cal.get(Calendar.HOUR);
        final int mm = cal.get(Calendar.MINUTE);
        final int ss = cal.get(Calendar.SECOND);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {

            String currentDateTime = currentDate+" "+hourOfDay + ":" + minute+":"+ss;
            receivableRegistrationBinding.loaningDateTimeEdit.setText(currentDateTime);

        }, hh, mm, true);

        timePickerDialog.show();

    }

    /**
     * pick date and set it to dateTimeEditText
     */
    public void pickDueDate(Calendar dueDateCal) {
        // Get Current Date
        final int mYear = dueDateCal.get(Calendar.YEAR);
        final int mMonth = dueDateCal.get(Calendar.MONTH);
        final int mDay = dueDateCal.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog dueDatePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    String pickedDueDate = year + "/" + Util.getMonthFormatted(monthOfYear) + "/" + Util.getDayFormatted(dayOfMonth);
                    receivableRegistrationBinding.disbursementDueDateEdit.setText(pickedDueDate);

                    pickDueTime(dueDateCal, pickedDueDate);

                }, mYear, mMonth, mDay);
        dueDatePickerDialog.show();
    }

    /**
     * pick time and set date and time picked to edit text
     * @param dueCal Calendar object
     * @param dueDate String, picked date in format yyyy-mm-dd
     */
    public void pickDueTime(Calendar dueCal, String dueDate) {
        final int hh = dueCal.get(Calendar.HOUR);
        final int mm = dueCal.get(Calendar.MINUTE);
        final int ss = dueCal.get(Calendar.SECOND);

        TimePickerDialog dueTimePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {

            String currentDateTime = dueDate+" "+hourOfDay + ":" + minute+":"+ss;
            receivableRegistrationBinding.disbursementDueDateEdit.setText(currentDateTime);

        }, hh, mm, true);

        dueTimePickerDialog.show();
    }

    /**
     * pick date and set it to dateTimeEditText
     */
    public void pickPaymentDate(Calendar payCal) {
        // Get Current Date
        final int mYear = payCal.get(Calendar.YEAR);
        final int mMonth = payCal.get(Calendar.MONTH);
        final int mDay = payCal.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog payDatePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    String payDate = year + "/" + Util.getMonthFormatted(monthOfYear) + "/" + Util.getDayFormatted(dayOfMonth);
                    receivableRegistrationBinding.disbursementDateTimeEdit.setText(payDate);

                    pickPayTime(payCal, payDate);

                }, mYear, mMonth, mDay);
        payDatePickerDialog.show();
    }

    /**
     * pick time and set date and time picked to edit text
     * @param payCal Calendar object
     * @param payDate String, picked date in format yyyy-mm-dd
     */
    public void pickPayTime(Calendar payCal, String payDate) {
        final int hh = payCal.get(Calendar.HOUR);
        final int mm = payCal.get(Calendar.MINUTE);
        final int ss = payCal.get(Calendar.SECOND);

        TimePickerDialog payTimePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {

            String payDateTime = payDate+" "+hourOfDay + ":" + minute+":"+ss;
            receivableRegistrationBinding.disbursementDateTimeEdit.setText(payDateTime);

        }, hh, mm, true);

        payTimePickerDialog.show();

    }


    public boolean recRequiredFilled(String entitled, String amount, String debtorName, String debtorContact, String loanDate, String dueDate){
        if (entitled==null || entitled.isEmpty() || amount==null || amount.isEmpty() || debtorName==null || debtorName.isEmpty() || debtorContact == null || debtorContact.isEmpty() || loanDate == null || loanDate.isEmpty() || dueDate == null || dueDate.isEmpty())
            return  false;

        try {
            Double.parseDouble(amount);
        }catch (NumberFormatException exception){
            return  false;
        }
        return true;
    }

    public void setRecRequiredUnfilled(String entitled, String amount, String debtorName, String debtorContact, String loanDate, String dueDate){
        if (entitled==null || entitled.isEmpty())
            receivableRegistrationBinding.entitledEdit.setError(getString(R.string.mandatory_field));

        try {
            Double.parseDouble(amount);
        }catch (Exception exception){
            receivableRegistrationBinding.amountEdit.setError(getString(R.string.mandatory_field));
        }

        if (debtorName == null || debtorName.isEmpty())
            receivableRegistrationBinding.debtorNamesEdit.setError(getString(R.string.mandatory_field));

        if (debtorContact == null || debtorContact.isEmpty())
            receivableRegistrationBinding.debtorContactEdit.setError(getString(R.string.mandatory_field));

        if (loanDate==null || loanDate.isEmpty())
            receivableRegistrationBinding.loaningDateTimeEdit.setError(getString(R.string.mandatory_field));

        if (dueDate == null || dueDate.isEmpty())
            receivableRegistrationBinding.disbursementDueDateEdit.setError(getString(R.string.mandatory_field));
    }

    public void insertReceivable() {
        recRegProgress.show();

        ExecutorService receiveInsertService = Executors.newSingleThreadExecutor();
        receiveInsertService.execute(() -> {

            try {
                Receivable receivableNew = new Receivable(recRegVM.getEntitle(),
                        Double.parseDouble(recRegVM.getAmount()),
                        recRegVM.getSticker(),
                        recRegVM.getLoanDate(),
                        recRegVM.getDueDate(),
                        recRegVM.getDisbursementDate(),
                        recRegVM.getDebtorNames(),
                        recRegVM.getDebtorContact(),
                        recRegVM.getDescription(),
                        recRegVM.getTelltale());

                if (recRegVM.getDisbursementDate()!=null && !recRegVM.getDisbursementDate().isEmpty())
                    receivableNew.setRefundedOrPaid(true);

                BudgetizerAppDatabase appDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());

                //get the account and update amount value by substract the receivable given ...
                Account acc =
                        updateCorrectWallet(
                                appDatabase.accountDAO().getAccounts().get(0),
                                -receivableNew.getAmount(),
                                selectedAccount
                        );

                appDatabase.receivableDAO().insertAll(receivableNew);
                appDatabase.accountDAO().update(acc);

            } catch (Exception exception) {
                runOnUiThread(() -> {
                    recRegDialog.setMessage(getString(R.string.an_error_occured)+"\n"+exception.getLocalizedMessage());
                    recRegProgress.dismiss();
                    recRegDialog.show();
                });
                return;
            }

            runOnUiThread(() -> {
                recRegDialog.setMessage(getString(R.string.saved));
                recRegDialog.setNegativeButton(getString(R.string.back), (dialog, which) -> ReceivNav.openReceivablesHome(ReceivableRegistrationActivity.this, accountName, currency));
                recRegDialog.show();
                recRegProgress.dismiss();
            });
        });

    }

    public void  recRecInitByIntent(Intent recRegIntent) {
        // if (mainIntent==null)
        //   throw new BudgetizerGeneralException(getString(R.string.page_not_initialized));

        accountName = recRegIntent.getStringExtra(ExNavigation.ACC_NAME_PARAM);
        currency = recRegIntent.getStringExtra(ExNavigation.CURRENCY_PARAM);
    }


}