package cm.abimmobiledev.mybudgetizer.ui.debt;


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
import cm.abimmobiledev.mybudgetizer.databinding.ActivityDebtsBinding;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.nav.DebtNavigator;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.ui.debt.adapter.DebtAdapter;
import cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class DebtsActivity extends AppCompatActivity {

    ActivityDebtsBinding debtsBinding;

    private String debtSearchParam;
    private String accountName;
    private String currency;
    private static final String DEBTS_TAG = "D_TAG";

    List<Debt> myPeriodicDebts;
    ProgressDialog debtsListProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        debtsBinding = DataBindingUtil.setContentView(this, R.layout.activity_debts);
        debtsListProgress = Util.initProgressDialog(this, getString(R.string.looking_up));

        debtsInitByIntent(getIntent());

        try {
            debtSearchParam = getDebtSearchParam(getIntent());
            getCorrectDefaultDebts(debtSearchParam);

        } catch (BudgetizerGeneralException exception) {
            Log.d(DEBTS_TAG, "onCreate: ", exception);
        }

        debtsBinding.debtsSearch.setOnClickListener(v -> {

            debtSearchParam = DebtNavigator.DEBT_OTHER_SEARCH;

            boolean byContract = debtsBinding.brContractionDate.isChecked();
            boolean byExpiration = debtsBinding.brExpirationDate.isChecked();

            String searchable = debtsBinding.debtDateInput.getText().toString();

            try {

                getCorrectFilteredDebts(debtSearchParam, getSearchParamFilled(byContract, byExpiration), searchable);

            } catch (BudgetizerGeneralException exception) {
                Log.d(DEBTS_TAG, "onCreate: ", exception);

            }

        });

        debtsBinding.debtsSearchDatePicker.setOnClickListener(v -> pickDebtSearchableDate());


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DebtNavigator.openDebtsHome(DebtsActivity.this, accountName, currency);
    }

    public static String getDebtSearchParam (Intent debtIntent) throws BudgetizerGeneralException {
        if (debtIntent==null)
            throw new BudgetizerGeneralException("Opening Debt search... Param intent couldn't be null");

        return debtIntent.getStringExtra(ExNavigation.SEARCH_PARAM);
    }

    public void getCorrectDefaultDebts(String param) throws BudgetizerGeneralException {
        Calendar cal = Calendar.getInstance();

        if (param.equalsIgnoreCase(DebtNavigator.DEBT_CONTRACTED_TODAY)) {
            Log.d(DEBTS_TAG, "getCorrectDefaultDebts: 0");
            getDebts(ExpenseDashboardActivity.getCurrentDayFormatted(cal), "");
        }
        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_CONTRACTED_THIS_MONTH))
            getDebts(ExpenseDashboardActivity.getCurrentMonthFormatted(cal),  "");
        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_CONTRACTED_THIS_YEAR))
            getDebts(ExpenseDashboardActivity.getCurrentYearFormatted(cal), "");

        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_EXPIRES_TODAY))
            getDebts("", ExpenseDashboardActivity.getCurrentDayFormatted(cal));
        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_EXPIRES_THIS_MONTH))
            getDebts("", ExpenseDashboardActivity.getCurrentMonthFormatted(cal));
        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_EXPIRES_THIS_YEAR))
            getDebts("", ExpenseDashboardActivity.getCurrentYearFormatted(cal));

        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_PAID_TODAY))
            getDebtsPaidOn(ExpenseDashboardActivity.getCurrentDayFormatted(cal));
        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_PAID_THIS_MONTH))
            getDebtsPaidOn( ExpenseDashboardActivity.getCurrentMonthFormatted(cal));
        else if (param.equalsIgnoreCase(DebtNavigator.DEBT_PAID_THIS_YEAR)) {
            Log.d(DEBTS_TAG, "getCorrectDefaultDebts: paid this year ?");

            getDebtsPaidOn(ExpenseDashboardActivity.getCurrentYearFormatted(cal));
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
    public void getCorrectFilteredDebts(String param, String searchParamFilled, String searchable) throws BudgetizerGeneralException {

        if (param.equalsIgnoreCase(DebtNavigator.DEBT_OTHER_SEARCH)) {

            if (searchParamFilled.equalsIgnoreCase("loan")) {
                getDebts(searchable, "");
            } else if (searchParamFilled.equalsIgnoreCase("due")) {
                getDebts("", searchable);
            } else {
                getDebtsPaidOn(searchable);
            }

        }

    }

    public void getDebts(String loanDateSearchPattern, String dueDateSearchPattern) throws BudgetizerGeneralException {
        if (loanDateSearchPattern==null || dueDateSearchPattern==null)
            throw new BudgetizerGeneralException("search pattern should not be null");

        final String loanDatePattern = "%"+loanDateSearchPattern+"%";
        final String expDatePattern = "%"+dueDateSearchPattern+"%";

        Log.d(DEBTS_TAG, "getDebts: 0");

        ExecutorService debtPatternSearch = Executors.newSingleThreadExecutor();
        debtPatternSearch.execute(() -> {
            BudgetizerAppDatabase searchDebtAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());

            myPeriodicDebts = searchDebtAppDatabase.debtDAO().getAllDebts(loanDatePattern, expDatePattern);

            DebtAdapter debtAdapter = new DebtAdapter(myPeriodicDebts);

            Log.d(DEBTS_TAG, "getDebts, results ==>  "+myPeriodicDebts.size());
            if(!myPeriodicDebts.isEmpty())
                Log.d(DEBTS_TAG, "getDebts, results ==>  "+myPeriodicDebts.get(0).refundedOrPaid);
            runOnUiThread(() -> {

                // double computedAmount = computeExpendedAmount(debtAdapter);
                try {
                    double totalAmount = DebtBoardActivity.computeTotalDebts(myPeriodicDebts);
                    debtsBinding.totalDebtValue.setText(String.format("%s F. CFA", totalAmount));
                } catch (BudgetizerGeneralException ignore) { }


                debtsBinding.periodicDebtRecycler.setHasFixedSize(true);
                debtsBinding.periodicDebtRecycler.setLayoutManager(new LinearLayoutManager(this));
                debtsBinding.periodicDebtRecycler.setAdapter(debtAdapter);
                debtsBinding.periodicDebtRecycler.setVerticalScrollBarEnabled(true);
                debtsBinding.periodicDebtRecycler.setVerticalFadingEdgeEnabled(true);
                debtsBinding.periodicDebtRecycler.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);

                //set total expended
                //  debtsBinding.totalExpended.setText(String.format("%s F. CFA", computedAmount));

                //dismiss progress here
                debtsListProgress.dismiss();

                if (myPeriodicDebts!=null && !myPeriodicDebts.isEmpty()) {
                    //create a method for all this...
                    debtsBinding.periodicDebtRecycler.setVisibility(View.VISIBLE);
                    debtsBinding.noItemFoundTv.setVisibility(View.GONE);
                }
                else {
                    debtsBinding.periodicDebtRecycler.setVisibility(View.GONE);
                    debtsBinding.noItemFoundTv.setVisibility(View.VISIBLE);
                }
            });


        });


    }


    public void getDebtsPaidOn(String paymentDateSearchPattern) throws BudgetizerGeneralException {
        if (paymentDateSearchPattern==null)
            throw new BudgetizerGeneralException("search pattern should not be null");

        final String payDatePattern = "%"+paymentDateSearchPattern+"%";
        Log.d(DEBTS_TAG, "getDebtsPaidOn: 0");

        debtsListProgress.show();

        ExecutorService debtPatternSearch = Executors.newSingleThreadExecutor();
        debtPatternSearch.execute(() -> {
            BudgetizerAppDatabase searchDebtAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());

            myPeriodicDebts = searchDebtAppDatabase.debtDAO().getAllPaidDebtsOn(payDatePattern);
            DebtAdapter debtAdapter = new DebtAdapter(myPeriodicDebts);
            runOnUiThread(() -> {

                // double computedAmount = computeExpendedAmount(debtAdapter);
                try {
                    double totalPaidAmount = DebtBoardActivity.computeTotalDebts(myPeriodicDebts);
                    debtsBinding.totalDebtValue.setText(String.format("%s F. CFA", totalPaidAmount));
                } catch (BudgetizerGeneralException ignore) { }


                debtsBinding.periodicDebtRecycler.setHasFixedSize(true);
                debtsBinding.periodicDebtRecycler.setLayoutManager(new LinearLayoutManager(this));
                debtsBinding.periodicDebtRecycler.setAdapter(debtAdapter);
                debtsBinding.periodicDebtRecycler.setVerticalScrollBarEnabled(true);
                debtsBinding.periodicDebtRecycler.setVerticalFadingEdgeEnabled(true);
                debtsBinding.periodicDebtRecycler.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);

                //set total expended
                //  debtsBinding.totalExpended.setText(String.format("%s F. CFA", computedAmount));

                //dismiss progress here
                debtsListProgress.dismiss();

                if (myPeriodicDebts!=null && !myPeriodicDebts.isEmpty()) {
                    //create a method for all this...
                    debtsBinding.periodicDebtRecycler.setVisibility(View.VISIBLE);
                    debtsBinding.noItemFoundTv.setVisibility(View.GONE);
                }
                else {
                    debtsBinding.periodicDebtRecycler.setVisibility(View.GONE);
                    debtsBinding.noItemFoundTv.setVisibility(View.VISIBLE);
                }
            });


        });

    }

    public void pickDebtSearchableDate() {
        // Get Current Date
        final Calendar debtCalendar = Calendar.getInstance();
        final int currentSearchY = debtCalendar.get(Calendar.YEAR);
        final int currentSearchM = debtCalendar.get(Calendar.MONTH);
        final int currentSearchD = debtCalendar.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog searchableDebtDatePickerDialog = new DatePickerDialog(this,
                (view, pickedSearchYear, pickedSearchMonthOfYear, pickedSearchDayOfMonth) -> {

                    String searchDebtDateDefault = pickedSearchYear + "/" + Util.getMonthFormatted(pickedSearchMonthOfYear) + "/" + Util.getDayFormatted(pickedSearchDayOfMonth);
                    debtsBinding.debtDateInput.setText(searchDebtDateDefault);

                }, currentSearchY, currentSearchM, currentSearchD);
        searchableDebtDatePickerDialog.show();
    }

    public void  debtsInitByIntent(Intent debtsIntent) {
        accountName = debtsIntent.getStringExtra(ExNavigation.ACC_NAME_PARAM);
        currency = debtsIntent.getStringExtra(ExNavigation.CURRENCY_PARAM);
    }

}