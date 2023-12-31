package cm.abimmobiledev.mybudgetizer.ui.expense;

import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.computeExpendedAmount;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Expense;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityExpensesBinding;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.ui.expense.adapter.ExpenseAdapter;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class ExpensesActivity extends AppCompatActivity {

    private static final String EXPENSES_TAG = "EXP_TAG";

    ActivityExpensesBinding activityExpensesBinding;
    private List<Expense> myPeriodicExpenses;

    AlertDialog.Builder myExpensesDialog;
    ProgressDialog expenseListProgress;


    String searchParam;
    String accountName;
    String currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityExpensesBinding = DataBindingUtil.setContentView(this, R.layout.activity_expenses);
        expInitByIntent(getIntent());
        myExpensesDialog = Util.initAlertDialogBuilder(this, getString(R.string.looking_up), getString(R.string.an_error_occured));
        myExpensesDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            //NOTHING TO DO, just close
        });
        expenseListProgress = Util.initProgressDialog(this, getString(R.string.looking_up));

        try {
            searchParam = getSearchParam(getIntent());

            dateEditVisibility(searchParam);
            getCorrectExpenses(searchParam);


        } catch (BudgetizerGeneralException exception) {
            Log.d(EXPENSES_TAG, "onCreate: "+exception.getLocalizedMessage(), exception);
        }

        activityExpensesBinding.expenseSearch.setOnClickListener(expenseSearchView -> {

            searchParam = activityExpensesBinding.expenseDateInput.getText().toString().trim();
            try {
                getExpenses(searchParam, true);
            } catch (BudgetizerGeneralException exception) {
                Log.d("TAG", "onFling: "+exception, exception);
            }

        });

        activityExpensesBinding.incomeSearch.setOnClickListener(incomeView -> {
            String incomeSticker = activityExpensesBinding.stickerInput.getText().toString();
            try {
                getExpenses(incomeSticker, false);
            } catch (BudgetizerGeneralException exception) {
                Log.d(EXPENSES_TAG, "getting expenses by sticker: "+exception, exception);
            }
        });

        activityExpensesBinding.expenseSearchDatePicker.setOnClickListener(expenseDatePickView -> pickSearchableDate());

        activityExpensesBinding.filterByStickerTitleIcon.setOnClickListener(stickerView -> {
            if (activityExpensesBinding.stickerSearchLayout.getVisibility()==View.VISIBLE) {
                activityExpensesBinding.stickerSearchLayout.setVisibility(View.GONE);
                return;
            }
            activityExpensesBinding.stickerSearchLayout.setVisibility(View.VISIBLE);
            activityExpensesBinding.searchByDateLayout.setVisibility(View.GONE);
        });

        activityExpensesBinding.filterByDateTitleIcon.setOnClickListener(dateFilterView -> {
            if (activityExpensesBinding.searchByDateLayout.getVisibility()==View.VISIBLE) {
                activityExpensesBinding.searchByDateLayout.setVisibility(View.GONE);
                return;
            }
            activityExpensesBinding.searchByDateLayout.setVisibility(View.VISIBLE);
            activityExpensesBinding.stickerSearchLayout.setVisibility(View.GONE);
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExNavigation.openExpensesHome(this, accountName, currency);
    }

    public static boolean isGivenDateSearch(String param) throws BudgetizerGeneralException {
        if(param==null)
            throw new BudgetizerGeneralException("Opening expense search... Param couldn't be null");

        return param.equalsIgnoreCase(ExNavigation.EXPENSE_OF_A_DATE);
    }

    public void dateEditVisibility(String param) throws BudgetizerGeneralException {
        if (isGivenDateSearch(param))
            activityExpensesBinding.searchLayout.setVisibility(View.VISIBLE);
        else
            activityExpensesBinding.searchLayout.setVisibility(View.GONE);

    }

    public static String getSearchParam (Intent intent) throws BudgetizerGeneralException {
        if (intent==null)
            throw new BudgetizerGeneralException("Opening expense/earnings search... Param intent couldn't be null");

        return intent.getStringExtra(ExNavigation.SEARCH_PARAM);
    }

    public void  expInitByIntent(Intent expIntent) {
        // if (mainIntent==null)
        //   throw new BudgetizerGeneralException(getString(R.string.page_not_initialized));

        accountName = expIntent.getStringExtra(ExNavigation.ACC_NAME_PARAM);
        currency = expIntent.getStringExtra(ExNavigation.CURRENCY_PARAM);
    }


    /**
     * <h2>Looking up expenses either by date or by a sticker filter</h2>
     * @param searchPattern : pattern to look up (filter)
     * @param byDate boolean; true if looking up by date, false else (looking up by sticker)
     * @throws BudgetizerGeneralException when pattern is null
     */
    public void getExpenses(String searchPattern, boolean byDate) throws BudgetizerGeneralException {

        if (searchPattern==null)
            throw new BudgetizerGeneralException("search pattern should not be null");

        if (byDate)
            searchPattern = "%"+searchPattern+"%";

        final String searchable = searchPattern;

        expenseListProgress.show();
        ExecutorService expensesPatternSearch = Executors.newSingleThreadExecutor();

        expensesPatternSearch.execute(() -> {
            BudgetizerAppDatabase searchExpensesAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());

            if (byDate)
                myPeriodicExpenses = searchExpensesAppDatabase.expenseDAO().loadAllExpenseOfAGivenMonth(searchable);
            else
                myPeriodicExpenses = searchExpensesAppDatabase.expenseDAO().loadExpensesByStickerFilter(searchable);


            ExpenseAdapter expenseAdapter = new ExpenseAdapter(myPeriodicExpenses);

            runOnUiThread(() -> {

                try {
                    double computedAmount = computeExpendedAmount(myPeriodicExpenses);


                    activityExpensesBinding.periodicExpensesRecycler.setHasFixedSize(true);
                    activityExpensesBinding.periodicExpensesRecycler.setLayoutManager(new LinearLayoutManager(this));
                    activityExpensesBinding.periodicExpensesRecycler.setAdapter(expenseAdapter);
                    activityExpensesBinding.periodicExpensesRecycler.setVerticalScrollBarEnabled(true);
                    activityExpensesBinding.periodicExpensesRecycler.setVerticalFadingEdgeEnabled(true);
                    activityExpensesBinding.periodicExpensesRecycler.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);

                    //set total expended
                    activityExpensesBinding.totalExpended.setText(String.format("%s F. CFA", computedAmount));

                } catch (BudgetizerGeneralException exception) {
                    Log.d(EXPENSES_TAG, "getExpenses: ", exception);
                }
                //dismiss progress here
                expenseListProgress.dismiss();

                if (myPeriodicExpenses!=null && !myPeriodicExpenses.isEmpty()) {
                    //create a method for all this...
                    activityExpensesBinding.periodicExpensesRecycler.setVisibility(View.VISIBLE);
                    activityExpensesBinding.noItemFoundTv.setVisibility(View.GONE);
                }
                else {
                    activityExpensesBinding.periodicExpensesRecycler.setVisibility(View.GONE);
                    activityExpensesBinding.noItemFoundTv.setVisibility(View.VISIBLE);
                }
            });


        });

    }

    public void getCorrectExpenses(String param) throws BudgetizerGeneralException {
        Calendar cal = Calendar.getInstance();

        if (param.equalsIgnoreCase(ExNavigation.EXPENSE_OF_TODAY))
            getExpenses(ExpenseDashboardActivity.getCurrentDayFormatted(cal), true);

        else if (param.equalsIgnoreCase(ExNavigation.EXPENSE_OF_THIS_MONTH))
            getExpenses(ExpenseDashboardActivity.getCurrentMonthFormatted(cal), true);

        else if (param.equalsIgnoreCase(ExNavigation.EXPENSE_OF_THIS_YEAR))
            getExpenses(ExpenseDashboardActivity.getCurrentYearFormatted(cal), true);
    }

    /**
     * pick date and set it to dateTimeEditText
     */
    public void pickSearchableDate() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        final int currentSearchY = c.get(Calendar.YEAR);
        final int currentSearchM = c.get(Calendar.MONTH);
        final int currentSearchD = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog searchableDatePickerDialog = new DatePickerDialog(this,
                (view, pickedSearchYear, pickedSearchMonthOfYear, pickedSearchDayOfMonth) -> {

                    String searchDateDefault = pickedSearchYear + "/" + Util.getMonthFormatted(pickedSearchMonthOfYear) + "/" + Util.getDayFormatted(pickedSearchDayOfMonth);
                    activityExpensesBinding.expenseDateInput.setText(searchDateDefault);

                }, currentSearchY, currentSearchM, currentSearchD);
        searchableDatePickerDialog.show();
    }

}