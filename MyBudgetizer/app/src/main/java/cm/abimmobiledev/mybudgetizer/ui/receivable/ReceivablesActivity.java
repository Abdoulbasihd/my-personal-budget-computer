package cm.abimmobiledev.mybudgetizer.ui.receivable;

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
import cm.abimmobiledev.mybudgetizer.database.entity.Debt;
import cm.abimmobiledev.mybudgetizer.database.entity.Receivable;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityReceivablesBinding;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.nav.DebtNavigator;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.nav.IncNavigator;
import cm.abimmobiledev.mybudgetizer.nav.ReceivNav;
import cm.abimmobiledev.mybudgetizer.ui.debt.DebtBoardActivity;
import cm.abimmobiledev.mybudgetizer.ui.debt.adapter.DebtAdapter;
import cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity;
import cm.abimmobiledev.mybudgetizer.ui.receivable.adapter.ReceivableAdapter;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class ReceivablesActivity extends AppCompatActivity {

    ActivityReceivablesBinding receivablesBinding;

    String recsSearchParam;
    private static final String RECS_TAG = "Rs_TAG";

    List<Receivable> myPeriodicRecs;
    ProgressDialog recsListProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receivablesBinding = DataBindingUtil.setContentView(this, R.layout.activity_receivables);

        recsListProgress = Util.initProgressDialog(this, getString(R.string.looking_up));

        receivablesBinding.recsSearchDatePicker.setOnClickListener(v -> pickRecSearchableDate());

        try {
            recsSearchParam = getRecsSearchParam(getIntent());
            getCorrectDefaultDebts(recsSearchParam);

        } catch (BudgetizerGeneralException exception) {
            Log.d(RECS_TAG, "onCreate: ", exception);
        }

        receivablesBinding.recsSearch.setOnClickListener(v -> {

            recsSearchParam = DebtNavigator.DEBT_OTHER_SEARCH;

            boolean byContract = receivablesBinding.brContractionDate.isChecked();
            boolean byExpiration = receivablesBinding.brExpirationDate.isChecked();
            String searchable = receivablesBinding.receivableDateInput.getText().toString();

            try {

                getCorrectFilteredRecs(recsSearchParam, getSearchParamFilled(byContract, byExpiration), searchable);

            } catch (BudgetizerGeneralException exception) {
                Log.d(RECS_TAG, "onCreate: ", exception);

            }

        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ReceivNav.openReceivablesHome(this);
    }

    public static String getRecsSearchParam (Intent debtIntent) throws BudgetizerGeneralException {
        if (debtIntent==null)
            throw new BudgetizerGeneralException("Opening Debt search... Param intent couldn't be null");

        return debtIntent.getStringExtra(ExNavigation.SEARCH_PARAM);
    }

    public void getCorrectDefaultDebts(String param) throws BudgetizerGeneralException {
        Calendar cal = Calendar.getInstance();

        if (param.equalsIgnoreCase(DebtNavigator.DEBT_CONTRACTED_TODAY)) {
            Log.d(RECS_TAG, "getCorrectDefaultDebts: 0");
            getRecs(ExpenseDashboardActivity.getCurrentDayFormatted(cal), "");
        }
        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_CONTRACTED_THIS_MONTH))
            getRecs(ExpenseDashboardActivity.getCurrentMonthFormatted(cal),  "");
        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_CONTRACTED_THIS_YEAR))
            getRecs(ExpenseDashboardActivity.getCurrentYearFormatted(cal), "");

        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_EXPIRES_TODAY))
            getRecs("", ExpenseDashboardActivity.getCurrentDayFormatted(cal));
        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_EXPIRES_THIS_MONTH))
            getRecs("", ExpenseDashboardActivity.getCurrentMonthFormatted(cal));
        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_EXPIRES_THIS_YEAR))
            getRecs("", ExpenseDashboardActivity.getCurrentYearFormatted(cal));

        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_PAID_TODAY))
            getRecsPaidOn(ExpenseDashboardActivity.getCurrentDayFormatted(cal));
        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_PAID_THIS_MONTH))
            getRecsPaidOn( ExpenseDashboardActivity.getCurrentMonthFormatted(cal));
        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_PAID_THIS_YEAR)) {
            Log.d(RECS_TAG, "getCorrectDefaultDebts: paid this year ?");

            getRecsPaidOn(ExpenseDashboardActivity.getCurrentYearFormatted(cal));
        }

    }

    public String getSearchParamFilled(boolean byContract, boolean byExp){
        if (byContract)
            return "loan";
        else if (byExp)
            return "due";
        else
            return "pay";
    }
    public void getCorrectFilteredRecs(String param, String searchParamFilled, String searchable) throws BudgetizerGeneralException {

        if (param.equalsIgnoreCase(DebtNavigator.DEBT_OTHER_SEARCH)) {

            if (searchParamFilled.equalsIgnoreCase("loan")) {
                getRecs(searchable, "");
            } else if (searchParamFilled.equalsIgnoreCase("due")) {
                getRecs("", searchable);
            } else {
                getRecsPaidOn(searchable);
            }

        }

    }

    public void getRecs(String loanDateSearchPattern, String dueDateSearchPattern) throws BudgetizerGeneralException {
        if (loanDateSearchPattern==null || dueDateSearchPattern==null)
            throw new BudgetizerGeneralException("search pattern should not be null");

        final String loanDatePattern = "%"+loanDateSearchPattern+"%";
        final String expDatePattern = "%"+dueDateSearchPattern+"%";


        ExecutorService debtPatternSearch = Executors.newSingleThreadExecutor();
        debtPatternSearch.execute(() -> {
            BudgetizerAppDatabase searchRecsAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());

            myPeriodicRecs = searchRecsAppDatabase.receivableDAO().getAllReceivables(loanDatePattern, expDatePattern);

            ReceivableAdapter debtAdapter = new ReceivableAdapter(myPeriodicRecs);

            Log.d(RECS_TAG, "get recs, results ==>  "+myPeriodicRecs.size());
            if(!myPeriodicRecs.isEmpty())
                Log.d(RECS_TAG, "get recs, results ==>  "+myPeriodicRecs.get(0).refundedOrPaid);
            runOnUiThread(() -> {

                try {
                    double totalAmount = ReceivableBoardActivity.computeTotalReceivable(myPeriodicRecs);
                    receivablesBinding.totalDebtValue.setText(String.format("%s F. CFA", totalAmount));
                } catch (BudgetizerGeneralException ignore) { }


                receivablesBinding.periodicRecRecycler.setHasFixedSize(true);
                receivablesBinding.periodicRecRecycler.setLayoutManager(new LinearLayoutManager(this));
                receivablesBinding.periodicRecRecycler.setAdapter(debtAdapter);
                receivablesBinding.periodicRecRecycler.setVerticalScrollBarEnabled(true);
                receivablesBinding.periodicRecRecycler.setVerticalFadingEdgeEnabled(true);
                receivablesBinding.periodicRecRecycler.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);

                //dismiss progress here
                recsListProgress.dismiss();

                if (myPeriodicRecs!=null && !myPeriodicRecs.isEmpty()) {
                    //create a method for all this...
                    receivablesBinding.periodicRecRecycler.setVisibility(View.VISIBLE);
                    receivablesBinding.noItemFoundTv.setVisibility(View.GONE);
                }
                else {
                    receivablesBinding.periodicRecRecycler.setVisibility(View.GONE);
                    receivablesBinding.noItemFoundTv.setVisibility(View.VISIBLE);
                }
            });


        });


    }


    public void getRecsPaidOn(String paymentDateSearchPattern) throws BudgetizerGeneralException {
        if (paymentDateSearchPattern==null)
            throw new BudgetizerGeneralException("search pattern should not be null");

        final String payDatePattern = "%"+paymentDateSearchPattern+"%";
        Log.d(RECS_TAG, "getDebtsPaidOn: 0");

        recsListProgress.show();

        ExecutorService debtPatternSearch = Executors.newSingleThreadExecutor();
        debtPatternSearch.execute(() -> {
            BudgetizerAppDatabase searchRecAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());

            myPeriodicRecs = searchRecAppDatabase.receivableDAO().getAllPaidReceivablesOn(payDatePattern);
            ReceivableAdapter recAdapter = new ReceivableAdapter(myPeriodicRecs);
            runOnUiThread(() -> {

                // double computedAmount = computeExpendedAmount(debtAdapter);
                try {
                    double totalPaidAmount = ReceivableBoardActivity.computeTotalReceivable(myPeriodicRecs);
                    receivablesBinding.totalDebtValue.setText(String.format("%s F. CFA", totalPaidAmount));
                } catch (BudgetizerGeneralException ignore) { }


                receivablesBinding.periodicRecRecycler.setHasFixedSize(true);
                receivablesBinding.periodicRecRecycler.setLayoutManager(new LinearLayoutManager(this));
                receivablesBinding.periodicRecRecycler.setAdapter(recAdapter);
                receivablesBinding.periodicRecRecycler.setVerticalScrollBarEnabled(true);
                receivablesBinding.periodicRecRecycler.setVerticalFadingEdgeEnabled(true);
                receivablesBinding.periodicRecRecycler.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);

                //set total expended
                //  debtsBinding.totalExpended.setText(String.format("%s F. CFA", computedAmount));

                //dismiss progress here
                recsListProgress.dismiss();

                if (myPeriodicRecs!=null && !myPeriodicRecs.isEmpty()) {
                    //create a method for all this...
                    receivablesBinding.periodicRecRecycler.setVisibility(View.VISIBLE);
                    receivablesBinding.noItemFoundTv.setVisibility(View.GONE);
                }
                else {
                    receivablesBinding.periodicRecRecycler.setVisibility(View.GONE);
                    receivablesBinding.noItemFoundTv.setVisibility(View.VISIBLE);
                }
            });


        });

    }

    public void pickRecSearchableDate() {
        // Get Current Date
        final Calendar recCalendar = Calendar.getInstance();
        final int currentSearchY = recCalendar.get(Calendar.YEAR);
        final int currentSearchM = recCalendar.get(Calendar.MONTH);
        final int currentSearchD = recCalendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog searchableDebtDatePickerDialog = new DatePickerDialog(this,
                (view, pickedSearchYear, pickedSearchMonthOfYear, pickedSearchDayOfMonth) -> {

                    String searchDebtDateDefault = pickedSearchYear + "/" + Util.getMonthFormatted(pickedSearchMonthOfYear) + "/" + Util.getDayFormatted(pickedSearchDayOfMonth);
                    receivablesBinding.receivableDateInput.setText(searchDebtDateDefault);

                }, currentSearchY, currentSearchM, currentSearchD);
        searchableDebtDatePickerDialog.show();
    }
}