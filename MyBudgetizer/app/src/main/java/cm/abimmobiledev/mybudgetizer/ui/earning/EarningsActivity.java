package cm.abimmobiledev.mybudgetizer.ui.earning;

import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpensesActivity.getSearchParam;
import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpensesActivity.isGivenDateSearch;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Earning;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityEarningsBinding;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.nav.IncNavigator;
import cm.abimmobiledev.mybudgetizer.ui.earning.adapter.IncomeAdapter;
import cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class EarningsActivity extends AppCompatActivity {

    private static final String INCOMES_TAG = "INCs_TAG";

    ActivityEarningsBinding activityEarningsBinding;
    private List<Earning> myPeriodicEarning;

    AlertDialog.Builder myEarningDialog;
    ProgressDialog earningListProgress;


    private String earningSearchParam;
    private String accountName;
    private String currency;
    private String earningTitle = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEarningsBinding = DataBindingUtil.setContentView(this, R.layout.activity_earnings);

        myEarningDialog = Util.initAlertDialogBuilder(this, getString(R.string.looking_up), getString(R.string.an_error_occured));
        myEarningDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            //NOTHING TO DO, just close
        });
        earningListProgress = Util.initProgressDialog(this, getString(R.string.looking_up));

        try {
            earningSearchParam = getSearchParam(getIntent());
            earningInitByIntent(getIntent());

            dateEditVisibility(earningSearchParam);
            getCorrectIncomes(earningSearchParam);


        } catch (BudgetizerGeneralException exception) {
            Log.d(INCOMES_TAG, "onCreate: "+exception.getLocalizedMessage(), exception);
        }

        activityEarningsBinding.incomesSearch.setOnClickListener(expenseSearchView -> {

            earningSearchParam = activityEarningsBinding.incomeDateInput.getText().toString().trim();
            try {
                getIncomes(earningSearchParam);
            } catch (BudgetizerGeneralException exception) {
                Log.d(INCOMES_TAG, exception.getLocalizedMessage(), exception);
            }

        });


        activityEarningsBinding.incomesSearchDatePicker.setOnClickListener(expenseDatePickView -> pickSearchableDate());
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        IncNavigator.openEarningsHome(this, accountName, currency);
    }

    public void dateEditVisibility(String param) throws BudgetizerGeneralException {
        if (isGivenDateSearch(param))
            activityEarningsBinding.searchLayout.setVisibility(View.VISIBLE);
        else
            activityEarningsBinding.searchLayout.setVisibility(View.GONE);

    }

    public void getIncomes(String dateSearchPattern) throws BudgetizerGeneralException {

        if (dateSearchPattern==null)
            throw new BudgetizerGeneralException("date should not be null");

        final String searchable = "%"+dateSearchPattern+"%";

        earningListProgress.show();
        ExecutorService incomesPatternSearch = Executors.newSingleThreadExecutor();

        incomesPatternSearch.execute(() -> {
            BudgetizerAppDatabase searchIncomesAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());
            myPeriodicEarning = searchIncomesAppDatabase.earningDAO().loadAllEarningLikeFormattedDate(searchable);

            IncomeAdapter incomeAdapter = new IncomeAdapter(myPeriodicEarning);

            runOnUiThread(() -> {

                //setting the title
                activityEarningsBinding.earningTitle.setText(earningTitle);


                activityEarningsBinding.periodicIncomesRecycler.setHasFixedSize(true);
                activityEarningsBinding.periodicIncomesRecycler.setLayoutManager(new LinearLayoutManager(this));
                activityEarningsBinding.periodicIncomesRecycler.setAdapter(incomeAdapter);
                activityEarningsBinding.periodicIncomesRecycler.setVerticalScrollBarEnabled(true);
                activityEarningsBinding.periodicIncomesRecycler.setVerticalFadingEdgeEnabled(true);
                activityEarningsBinding.periodicIncomesRecycler.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);

                //dismiss progress here
                earningListProgress.dismiss();

                if (myPeriodicEarning!=null && !myPeriodicEarning.isEmpty()) {
                    //create a method for all this...
                    activityEarningsBinding.periodicIncomesRecycler.setVisibility(View.VISIBLE);
                    activityEarningsBinding.noItemFoundTv.setVisibility(View.GONE);
                }
                else {

                    activityEarningsBinding.periodicIncomesRecycler.setVisibility(View.GONE);
                    activityEarningsBinding.noItemFoundTv.setVisibility(View.VISIBLE);
                }
            });


        });

    }

    public void getCorrectIncomes(String param) throws BudgetizerGeneralException {
        Calendar cal = Calendar.getInstance();

        if (param.equalsIgnoreCase(ExNavigation.EXPENSE_OF_TODAY)) {
            getIncomes(ExpenseDashboardActivity.getCurrentDayFormatted(cal));
            earningTitle = getString(R.string.my_incomes_of_today);
        }

        else if (param.equalsIgnoreCase(ExNavigation.EXPENSE_OF_THIS_MONTH)) {
            getIncomes(ExpenseDashboardActivity.getCurrentMonthFormatted(cal));
            earningTitle = getString(R.string.my_incomes_of_this_month);
        }

        else if (param.equalsIgnoreCase(ExNavigation.EXPENSE_OF_THIS_YEAR)) {
            getIncomes(ExpenseDashboardActivity.getCurrentYearFormatted(cal));
            earningTitle = getString(R.string.my_incomes_of_this_year);
        }
        else {
            getIncomes(param);
            earningTitle = getString(R.string.my_incomes);
        }
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
                    activityEarningsBinding.incomeDateInput.setText(searchDateDefault);

                }, currentSearchY, currentSearchM, currentSearchD);
        searchableDatePickerDialog.show();
    }

    public void  earningInitByIntent(Intent earnBoardIntent) {
        accountName = earnBoardIntent.getStringExtra(ExNavigation.ACC_NAME_PARAM);
        currency = earnBoardIntent.getStringExtra(ExNavigation.CURRENCY_PARAM);
    }

}