package cm.abimmobiledev.mybudgetizer.ui.expense;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Budget;
import cm.abimmobiledev.mybudgetizer.database.entity.BudgetWithExpenses;
import cm.abimmobiledev.mybudgetizer.database.entity.Expense;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityExpenseRegistrationBinding;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.ui.budget.adapter.BudgetAdapter;
import cm.abimmobiledev.mybudgetizer.useful.Util;
import cm.abimmobiledev.mybudgetizer.viewmodel.ExpenseRegViewModel;

public class ExpenseRegistrationActivity extends AppCompatActivity {

    private static final String EXP_REG_TAG = "EXP_REG";

    ActivityExpenseRegistrationBinding expenseRegistrationBinding;
    AlertDialog.Builder expenseRegDialog;
    ProgressDialog expenseRegProgress;

    ExpenseRegViewModel expenseRegViewModel;
    List<Budget> unexpiredBudget;
    List<String> unexpiredBudgetTitle;
    ArrayAdapter<String> budgetSpinnerAdapter;
    int selectedBudgetPosition;
    Budget selectedBudget;

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
            expenseRegistrationBinding.stickerEdit.setText("");
            expenseRegistrationBinding.executePendingBindings();
        });

        expenseRegistrationBinding.pickDateAndTime.setOnClickListener(pickerView -> pickDate());

        expenseRegistrationBinding.saveExpense.setOnClickListener(saveView -> {


            String entitle = expenseRegViewModel.getEntitle().trim();
            String dateTime = expenseRegViewModel.getDateTimeOfExpense().trim();
            String amount = expenseRegViewModel.getAmount().trim();
            String reason = expenseRegViewModel.getReason().trim();
            String sticker = expenseRegistrationBinding.stickerEdit.getText().toString();

            try {
                if (mandatoryFilled(entitle, amount, dateTime)) {
                    expenseRegProgress.show();
                    expenseDatabaseInsert(entitle, amount, dateTime, reason, sticker);

                    return;
                }

                setBlankEditErrors(entitle, amount, dateTime);
            }
            catch (BudgetizerGeneralException exception) {
                Log.d(EXP_REG_TAG, "onCreate: "+exception.getLocalizedMessage(), exception);
                setBlankEditErrors(entitle, null, dateTime);
            }
        });

        unexpiredBudget = new ArrayList();
        unexpiredBudgetTitle = new ArrayList();
        budgetSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unexpiredBudgetTitle);
        budgetSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        expenseRegistrationBinding.spinnerBudgets.setAdapter(budgetSpinnerAdapter);
        getNotExpiredBudgets();

        expenseRegistrationBinding.spinnerBudgets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedBudgetPosition = position;
                if (unexpiredBudget!=null && unexpiredBudget.size()>position)
                    selectedBudget = unexpiredBudget.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
    public static boolean mandatoryFilled(String entitle, String amount, String dateTime) throws BudgetizerGeneralException {

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
     * @param sticker String
     */
    public void expenseDatabaseInsert(String entitle, String amount, String dateReg, String reason, String sticker) {

        ExecutorService insertExpenseExec = Executors.newSingleThreadExecutor();
        insertExpenseExec.execute(() -> {

            try {

                BudgetizerAppDatabase appDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());
                BudgetWithExpenses budgetWithExpenses = appDatabase.budgetDAO().getExpensesOfGivenBudget(selectedBudget.getBudgetId());
                double consumedAmount = BudgetAdapter.getConsumedAmount(budgetWithExpenses);

                Expense expenseNew = new Expense(entitle, Double.parseDouble(amount), dateReg, reason, sticker);
                expenseNew.setFkBudgetId(selectedBudget.budgetId);

                if (consumedAmount+expenseNew.getAmount()<=selectedBudget.getAmount()) {
                    appDatabase.expenseDAO().insertAll(expenseNew);
                    expenseRegDialog.setMessage(getString(R.string.saved));
                } else
                    expenseRegDialog.setMessage("Ce budget n'a pas de montant suffisamment disponible pour cette dépense");

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
                expenseRegDialog.setNegativeButton(getString(R.string.back), (dialog, which) -> ExNavigation.openExpensesHome(ExpenseRegistrationActivity.this));
                expenseRegDialog.show();
                expenseRegProgress.dismiss();
            });
        });
    }

    public void getNotExpiredBudgets(){
        ProgressDialog budgetsSelectProgress = Util.initProgressDialog(this, getString(R.string.saving));
        budgetsSelectProgress.show();

        AlertDialog.Builder budgetSelectDialog = Util.initAlertDialogBuilder(this, "Selection de budget", "");


        ExecutorService getNotExpiredBudgetsExec = Executors.newSingleThreadExecutor();
        getNotExpiredBudgetsExec.execute(() -> {
            try {

                BudgetizerAppDatabase appDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());
                List<Budget> allBudgets = appDatabase.budgetDAO().getBudgets();

                Calendar todayCalendar = Calendar.getInstance();
                int currentYear = todayCalendar.get(Calendar.YEAR);
                int currentMonth = todayCalendar.get(Calendar.MONTH);
                int currentDay = todayCalendar.get(Calendar.DAY_OF_MONTH);
                String currentDate = currentYear + "/" + Util.getMonthFormatted(currentMonth) + "/" + Util.getDayFormatted(currentDay);


                unexpiredBudget = filterUnexpiredBudgets(allBudgets, currentDate);
                unexpiredBudgetTitle = getBudgetTitles(unexpiredBudget);



            }
            catch (Exception exception) {
                runOnUiThread(() -> {
                    budgetSelectDialog.setMessage(getString(R.string.an_error_occured)+"\n"+exception.getLocalizedMessage());
                    budgetsSelectProgress.dismiss();
                    budgetSelectDialog.show();
                });
                return;
            }

            runOnUiThread(() -> {
                budgetsSelectProgress.dismiss();
                budgetSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, unexpiredBudgetTitle);
                budgetSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                budgetSpinnerAdapter.notifyDataSetChanged();
                expenseRegistrationBinding.spinnerBudgets.setAdapter(budgetSpinnerAdapter);

            });
        });
    }

    public List<Budget> filterUnexpiredBudgets(List<Budget> budgets, String currentDate){

        List<Budget> filteredBudgets = new ArrayList<>();

        Date currentDateFormatted = new Date(currentDate);

        for (int counter=0; counter<budgets.size(); counter++){

            Date budgetExpDate = new Date(budgets.get(counter).getEndDate());

            int compared = budgetExpDate.compareTo(currentDateFormatted);

            if (compared>=0)
                filteredBudgets.add(budgets.get(counter));

        }
        return filteredBudgets;
    }

    public List<String> getBudgetTitles(List<Budget> budgets){
        List<String> budgetTitles = new ArrayList<>();
        for (int counter=0; counter<budgets.size(); counter++){
            budgetTitles.add(budgets.get(counter).getEntitled());
        }
        return  budgetTitles;
    }

}