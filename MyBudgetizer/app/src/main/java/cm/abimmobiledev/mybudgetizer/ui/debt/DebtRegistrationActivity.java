package cm.abimmobiledev.mybudgetizer.ui.debt;

import static cm.abimmobiledev.mybudgetizer.ui.earning.EarningRegistrationActivity.getAccountTypes;
import static cm.abimmobiledev.mybudgetizer.ui.earning.EarningRegistrationActivity.updateCorrectWallet;

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

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Account;
import cm.abimmobiledev.mybudgetizer.database.entity.Debt;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityDebtRegistrationBinding;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.nav.DebtNavigator;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.useful.Util;
import cm.abimmobiledev.mybudgetizer.viewmodel.DebtRegistrationVM;

public class DebtRegistrationActivity extends AppCompatActivity {

    ActivityDebtRegistrationBinding debtRegistrationBinding;
    DebtRegistrationVM debtRegistrationVM;
    private String accountName;
    private String currency;

    private final String TAG_DEBT_REG = "D_REG_TAG";

    AlertDialog.Builder debtRegDialog;
    ProgressDialog debtRegProgress;

    ArrayAdapter<String> accountAdapter;
    int selectedAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        debtRegistrationVM = new DebtRegistrationVM();
        debtRegistrationBinding = DataBindingUtil.setContentView(this, R.layout.activity_debt_registration);
        debtRegistrationBinding.setDebtRegistor(debtRegistrationVM);
        debtRegistrationBinding.executePendingBindings();

        debtsRegInitByIntent(getIntent());

        debtRegDialog = Util.initAlertDialogBuilder(this, "Nouvelle Dette", getString(R.string.save_done));
        debtRegDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            debtRegistrationVM.setEntitle("");
            debtRegistrationVM.setLoanDate("");
            debtRegistrationVM.setRepaymentDueDate("");
            debtRegistrationVM.setDescription("");
            debtRegistrationVM.setAmount("");
            debtRegistrationVM.setCreditorNames("");
            debtRegistrationVM.setCreditorContact("");
            debtRegistrationVM.setSticker("");
            debtRegistrationBinding.executePendingBindings();
        });

        debtRegProgress = Util.initProgressDialog(this, getString(R.string.saving));

        debtRegistrationBinding.saveDebt.setOnClickListener(v -> {
            // : save debt here
            try {
                if (!requiredDebtRegistrationDataFilled(debtRegistrationVM.getEntitle(), debtRegistrationVM.getAmount(), debtRegistrationVM.getLoanDate(), debtRegistrationVM.getRepaymentDueDate(), debtRegistrationVM.getCreditorNames())) {
                    setDebtRequiredField();
                    return;
                }

                debtRegProgress.show();
                debtDataBaseRegistrationService();

            } catch (BudgetizerGeneralException exception) {
                Log.d(TAG_DEBT_REG, "onCreate: "+exception.getLocalizedMessage(), exception);
            }
        });

        debtRegistrationBinding.pickLoaningDateAndTime.setOnClickListener(v -> pickLoaningDate(Calendar.getInstance()));
        debtRegistrationBinding.pickRepaymentDateAndTime.setOnClickListener(v -> pickPaymentDate(Calendar.getInstance()));
        debtRegistrationBinding.pickRepayDueDate.setOnClickListener(v -> pickDueDate(Calendar.getInstance()));

        accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, getAccountTypes());
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        debtRegistrationBinding.spinnerAccounts.setAdapter(accountAdapter);
        debtRegistrationBinding.spinnerAccounts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        DebtNavigator.openDebtsHome(DebtRegistrationActivity.this, accountName, currency);
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
                    debtRegistrationBinding.loaningDateTimeEdit.setText(currentDate);

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
            debtRegistrationBinding.loaningDateTimeEdit.setText(currentDateTime);

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
                    debtRegistrationBinding.repayementDueDateEdit.setText(pickedDueDate);

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
            debtRegistrationBinding.repayementDueDateEdit.setText(currentDateTime);

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
                    debtRegistrationBinding.repaymentDateTimeEdit.setText(payDate);

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
            debtRegistrationBinding.repaymentDateTimeEdit.setText(payDateTime);

        }, hh, mm, true);

        payTimePickerDialog.show();

    }

    public  boolean requiredDebtRegistrationDataFilled(String entitled, String amount, String loanDate, String dueDate, String creditorNames) {
        try {
            if (Double.parseDouble(amount)<0)
                return  false;
        }catch (NumberFormatException exception) {
            return false;
        }
        return entitled != null && !entitled.isEmpty() && loanDate != null && !loanDate.isEmpty() &&  dueDate !=null && !dueDate.isEmpty() && creditorNames != null && !creditorNames.isEmpty();
    }

    public void setDebtRequiredField() throws BudgetizerGeneralException {
        if (debtRegistrationVM==null || debtRegistrationBinding==null)
            throw new BudgetizerGeneralException("not initialized");

        if (debtRegistrationVM.getEntitle().isEmpty())
            debtRegistrationBinding.entitledEdit.setError(getString(R.string.mandatory_field));

        if (debtRegistrationVM.getCreditorContact().isEmpty())
            debtRegistrationBinding.creditorContactEdit.setError(getString(R.string.mandatory_field));

        if (debtRegistrationVM.getLoanDate().isEmpty())
            debtRegistrationBinding.loaningDateTimeEdit.setError(getString(R.string.mandatory_field));

        if (debtRegistrationVM.getRepaymentDueDate().isEmpty())
            debtRegistrationBinding.repayementDueDateEdit.setError(getString(R.string.mandatory_field));

        try {
            if (debtRegistrationVM.getAmount().isEmpty() || Double.parseDouble(debtRegistrationVM.getAmount())<0)
                debtRegistrationBinding.amountEdit.setError(getString(R.string.mandatory_field));
        }catch (NumberFormatException exception) {
            debtRegistrationBinding.amountEdit.setError(getString(R.string.must_be_a_number));
        }

    }

    public Debt debtSetPay(Debt debt) throws BudgetizerGeneralException {
        if (debt==null)
            throw new BudgetizerGeneralException("Param debt much be initialized");

        debt.setRefundedOrPaid(debt.getRepaymentDate()!=null && !debt.getRepaymentDate().isEmpty());
        return debt;
    }

    public void debtDataBaseRegistrationService(){

        ExecutorService debtRegExecService = Executors.newSingleThreadExecutor();
        debtRegExecService.execute(() -> {

            try {
                Debt debt = new Debt(debtRegistrationVM.getEntitle(), Double.parseDouble(debtRegistrationVM.getAmount()), debtRegistrationVM.getLoanDate(), debtRegistrationVM.getRepaymentDueDate(), debtRegistrationVM.getCreditorNames(), debtRegistrationVM.getCreditorContact(), debtRegistrationVM.getDescription(), debtRegistrationVM.getSticker(), debtRegistrationVM.getTelltale());
                debt.setRepaymentDate(debtRegistrationVM.getRepaymentDate());
                debt = debtSetPay(debt);

                BudgetizerAppDatabase appDatabaseDebtReg = BudgetizerAppDatabase.getInstance(getApplicationContext());

                //get the account and update amount value by adding the gotten value ...
                Account acc =
                        updateCorrectWallet(
                                appDatabaseDebtReg.accountDAO().getAccounts().get(0),
                                debt.getAmount(),
                                selectedAccount
                        );

                appDatabaseDebtReg.debtDAO().insertAll(debt);
                appDatabaseDebtReg.accountDAO().update(acc);
            }
            catch (Exception exception) {
                Log.d(TAG_DEBT_REG, exception.getLocalizedMessage(), exception);
                runOnUiThread(() -> {
                    debtRegProgress.dismiss();
                    debtRegDialog.setMessage(getString(R.string.an_error_occured)+"\n"+exception.getLocalizedMessage());

                });
            }

            runOnUiThread(() -> {
                debtRegDialog.setMessage(getString(R.string.saved));
                debtRegDialog.setNegativeButton(getString(R.string.back), (dialog, which) -> DebtNavigator.openDebtsHome(DebtRegistrationActivity.this, accountName, currency));
                debtRegDialog.show();
                debtRegProgress.dismiss();
            });


        });

    }

    public void  debtsRegInitByIntent(Intent debtRegIntent) {
        accountName = debtRegIntent.getStringExtra(ExNavigation.ACC_NAME_PARAM);
        currency = debtRegIntent.getStringExtra(ExNavigation.CURRENCY_PARAM);
    }


}