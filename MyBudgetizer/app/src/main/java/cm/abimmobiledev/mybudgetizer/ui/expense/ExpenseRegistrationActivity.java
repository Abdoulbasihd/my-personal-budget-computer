package cm.abimmobiledev.mybudgetizer.ui.expense;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Expense;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityExpenseRegistrationBinding;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.useful.Util;
import cm.abimmobiledev.mybudgetizer.viewmodel.ExpenseRegViewModel;

public class ExpenseRegistrationActivity extends AppCompatActivity {

    private static final String EXP_REG_TAG = "EXP_REG";

    ActivityExpenseRegistrationBinding expenseRegistrationBinding;
    AlertDialog.Builder expenseRegDialog;
    ProgressDialog expenseRegProgress;

    ExpenseRegViewModel expenseRegViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        expenseRegProgress = Util.initProgressDialog(this, getString(R.string.saving));

        expenseRegViewModel = new ExpenseRegViewModel();

        expenseRegistrationBinding = DataBindingUtil.setContentView(this, R.layout.activity_expense_registration);
        expenseRegistrationBinding.setExpenseModel(expenseRegViewModel);
        expenseRegistrationBinding.executePendingBindings();

        expenseRegDialog = Util.initAlertDialogBuilder(this, getString(R.string.new_expense), getString(R.string.save_done));
        expenseRegDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            expenseRegViewModel.setEntitle("");
            expenseRegViewModel.setDateTimeOfExpense("");
            expenseRegViewModel.setReason("");
            expenseRegistrationBinding.executePendingBindings();
        });

        expenseRegistrationBinding.pickDateAndTime.setOnClickListener(pickerView -> pickDate());

        expenseRegistrationBinding.saveExpense.setOnClickListener(saveView -> {


            String entitle = expenseRegViewModel.getEntitle().trim();
            String dateTime = expenseRegViewModel.getDateTimeOfExpense().trim();
            String amount = expenseRegViewModel.getAmount().trim();
            String reason = expenseRegViewModel.getReason().trim();

            try {
                if (mandatoryFilled(entitle, amount, dateTime)) {
                    expenseRegProgress.show();
                    expenseDatabaseInsert(entitle, amount, dateTime, reason);

                    return;
                }

                setBlankEditErrors(entitle, amount, dateTime);
            }
            catch (BudgetizerGeneralException exception) {
                Log.d(EXP_REG_TAG, "onCreate: "+exception.getLocalizedMessage(), exception);
                setBlankEditErrors(entitle, null, dateTime);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExNavigation.openExpensesHome(ExpenseRegistrationActivity.this);
    }

    /**
     * pick date and set it to dateTimeEditText
     */
    public void pickDate() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        final int mYear = c.get(Calendar.YEAR);
        final int mMonth = c.get(Calendar.MONTH);
        final int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {

                    String currentDate = year + "/" + Util.getMonthFormatted(monthOfYear) + "/" + Util.getDayFormatted(dayOfMonth);
                    expenseRegistrationBinding.dateTimeEdit.setText(currentDate);

                    pickTime(c, currentDate);

                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }


    /**
     * pick time and set date and time picked to edit text
     * @param cal Calendar object
     * @param currentDate String, picked date in format yyyy-mm-dd
     */
    public void pickTime(Calendar cal, String currentDate) {
        final int hh = cal.get(Calendar.HOUR);
        final int mm = cal.get(Calendar.MINUTE);
        final int ss = cal.get(Calendar.SECOND);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {

            String currentDateTime = currentDate+" "+hourOfDay + ":" + minute+":"+ss;
            expenseRegistrationBinding.dateTimeEdit.setText(currentDateTime);

        }, hh, mm, true);

        timePickerDialog.show();

    }


    /**
     * Verify if all required field (mandatory) are filled.
     * @param entitle String, the expense entitle
     * @param amount String value of a double, the amount expended
     * @param dateTime String, date and time (yyyy-dd-mm hh:mm) of expense
     * @return a boolean : true when filled.
     * @throws BudgetizerGeneralException when amount ain't a number
     */
    public boolean mandatoryFilled(String entitle, String amount, String dateTime) throws BudgetizerGeneralException {

        //when amount ain't a number (event if is null), let's catch an exception
        try {
           Double.parseDouble(amount);
        }catch (NumberFormatException|NullPointerException exception) {
            throw new BudgetizerGeneralException("Amount format : "+exception.getMessage(), 500);
        }

        return (entitle!=null && !entitle.isEmpty()) && !amount.isEmpty() && dateTime!=null && !dateTime.isEmpty();
    }

    /**
     * set errors on mandatory's edit text
     * @param entitle String
     * @param amount String
     * @param dateTime String
     */
    public void setBlankEditErrors (String entitle, String amount, String dateTime) {
        if (entitle==null || entitle.isEmpty())
            expenseRegistrationBinding.entitledEdit.setError(getString(R.string.mandatory_field));

        if (dateTime==null || dateTime.isEmpty())
            expenseRegistrationBinding.dateTimeEdit.setError(getString(R.string.mandatory_field));

        if (amount==null || amount.isEmpty())
            expenseRegistrationBinding.amountEdit.setError(getString(R.string.amount_must_be_a_number_and_is_mandatory));
    }

    /**
     * insert an expense in data base
     * @param entitle String
     * @param amount String
     * @param dateReg String
     * @param reason String
     */
    public void expenseDatabaseInsert(String entitle, String amount, String dateReg, String reason) {

        ExecutorService insertExpenseExec = Executors.newSingleThreadExecutor();
        insertExpenseExec.execute(() -> {

            try {
                Expense expenseNew = new Expense(entitle, Double.parseDouble(amount), dateReg, reason);


                BudgetizerAppDatabase appDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());
                appDatabase.expenseDAO().insertAll(expenseNew);
            }
            catch (Exception exception) {
                runOnUiThread(() -> {
                    expenseRegDialog.setMessage(getString(R.string.an_error_occured)+"\n"+exception.getLocalizedMessage());
                    expenseRegProgress.dismiss();
                    expenseRegDialog.show();
                });
                return;
            }

            runOnUiThread(() -> {
                expenseRegDialog.setMessage(getString(R.string.saved));
                expenseRegDialog.setNegativeButton(getString(R.string.back), (dialog, which) -> {
                    ExNavigation.openExpensesHome(ExpenseRegistrationActivity.this);
                });
                expenseRegDialog.show();
                expenseRegProgress.dismiss();
            });




        });

    }

}