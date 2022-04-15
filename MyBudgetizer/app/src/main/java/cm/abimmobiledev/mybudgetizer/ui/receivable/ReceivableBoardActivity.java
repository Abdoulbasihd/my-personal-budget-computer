package cm.abimmobiledev.mybudgetizer.ui.receivable;

import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.PERIOD_DAILY;
import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.PERIOD_MONTHLY;
import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.PERIOD_YEARLY;
import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.getCurrentDayFormatted;
import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.getCurrentMonthFormatted;
import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.getCurrentYearFormatted;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.database.BudgetizerAppDatabase;
import cm.abimmobiledev.mybudgetizer.database.entity.Receivable;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityReceivableBoardBinding;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.nav.ReceivNav;
import cm.abimmobiledev.mybudgetizer.ui.debt.BottomSheetMoreDebtsFragment;
import cm.abimmobiledev.mybudgetizer.ui.receivable.adapter.ReceivableAdapter;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class ReceivableBoardActivity extends AppCompatActivity {

    ActivityReceivableBoardBinding receivableBoardBinding;

    ProgressDialog recsListProgress;
    ProgressDialog recBoardResumeProgress;
    List<Receivable> periodicRecs;

    private static final String REC_BOARD_TAG = "RcB_TAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        receivableBoardBinding = DataBindingUtil.setContentView(this, R.layout.activity_receivable_board);

        recsListProgress = Util.initProgressDialog(this, getString(R.string.looking_up));
        recBoardResumeProgress = Util.initProgressDialog(this, getString(R.string.looking_up));

        receivableBoardBinding.showMore.setOnClickListener(v -> {
            BottomSheetMoreReceivableMenuFragment moreReceivableMenuFragment = new BottomSheetMoreReceivableMenuFragment();
            moreReceivableMenuFragment.show(getSupportFragmentManager(), "MDebtModalBottomSheet");
        });

        receivableBoardBinding.cardRefresh.setOnClickListener(refreshView -> recBoardViewDataSetup());
        receivableBoardBinding.newReceivable.setOnClickListener(receivable -> ReceivNav.openNewReceivable(ReceivableBoardActivity.this));
        recBoardViewDataSetup();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExNavigation.openMainHome(ReceivableBoardActivity.this);
    }

    public void recBoardViewDataSetup(){
        final Calendar receivableCalendar = Calendar.getInstance();
        setPeriodicRecs(getCurrentDayFormatted(receivableCalendar), PERIOD_DAILY);
        setPeriodicRecs(getCurrentMonthFormatted(receivableCalendar), PERIOD_MONTHLY);
        setPeriodicRecs(getCurrentYearFormatted(receivableCalendar), PERIOD_YEARLY);

        // : get 10 last entries of earnings and show it
        getLastRecs(10);
    }

    /**
     * <h4>Select and compute receivables of a year, a month or a year</h4>
     * @param period String formatted either for day (yyyy/mm/dd) or for month (yyyy/mm) of just a year (yyyy). Later, looks how you could add  for weeds
     * @param periodicity int : PERIOD_DAILY, PERIOD_MONTHLY or PERIOD_YEARLY. Add for weeks later. to identify which value is to modify
     */
    public void setPeriodicRecs(String period, int periodicity) {

        // : add loader
        recBoardResumeProgress.show();
        ExecutorService periodicReceivableBoardComputerServ = Executors.newSingleThreadExecutor();

        periodicReceivableBoardComputerServ.execute(() -> {

            try {
                BudgetizerAppDatabase recSelectorAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());

                periodicRecs = recSelectorAppDatabase.receivableDAO().loadAllReceivableLikeFormattedLoanDate("%"+period+"%");


                runOnUiThread(() -> {
                    try {
                        double computedAmount = computeTotalReceivable(periodicRecs);
                        if (periodicity==PERIOD_DAILY)
                            receivableBoardBinding.todayReceivableValue.setText(String.format("%s F. CFA", computedAmount));
                        else if (periodicity==PERIOD_MONTHLY)
                            receivableBoardBinding.thisMonthReceivableValue.setText(String.format("%s F. CFA", computedAmount));
                        else if (periodicity==PERIOD_YEARLY)
                            receivableBoardBinding.thisYearReceivableValue.setText(String.format("%s F. CFA", computedAmount));
                        // may be you could add for week

                    } catch (BudgetizerGeneralException exception) {
                        Log.d(REC_BOARD_TAG, "selectPeriodicDebts: "+exception.getLocalizedMessage(), exception);
                    }
                    recBoardResumeProgress.dismiss();
                });
            }
            catch (Exception exception) {
                Log.d(REC_BOARD_TAG, "selectPeriodicDebts: "+exception.getLocalizedMessage(), exception);
            }

        });


    }

    /**
     * <h1>Should move this method some where else. may be in the view model ?</h1>
     * <h2>Compute total earning amount corresponding to a list of incomes (or earnings)</h2>
     * @param receivables List of debts
     * @return a double, total income
     * @throws BudgetizerGeneralException when list is null
     */
    public static double computeTotalReceivable(List<Receivable> receivables) throws BudgetizerGeneralException {
        if (receivables == null)
            throw new BudgetizerGeneralException("Receivable list should not be null");

        double totalAmount = 0;

        for (int counter=0; counter<receivables.size(); counter++) {

            Receivable receivable = receivables.get(counter);

            if (receivable==null)
                throw new BudgetizerGeneralException("Element of the receivables list shouldn't be null");

            totalAmount = totalAmount + receivable.getAmount();

        }

        return totalAmount;
    }

    public void getLastRecs(int numberOfElements) {

        recsListProgress.show();
        ExecutorService lastExpensesServ = Executors.newSingleThreadExecutor();

        lastExpensesServ.execute(() -> {
            BudgetizerAppDatabase lastReceivablesAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());
            periodicRecs = lastReceivablesAppDatabase.receivableDAO().getLastReceivables(numberOfElements);

            ReceivableAdapter receivableAdapter = new ReceivableAdapter(periodicRecs);

            runOnUiThread(() -> {


                receivableBoardBinding.myLastReceivablesRecycler.setHasFixedSize(true);
                receivableBoardBinding.myLastReceivablesRecycler.setLayoutManager(new LinearLayoutManager(this));
                receivableBoardBinding.myLastReceivablesRecycler.setAdapter(receivableAdapter);
                receivableBoardBinding.myLastReceivablesRecycler.setVerticalScrollBarEnabled(true);
                receivableBoardBinding.myLastReceivablesRecycler.setVerticalFadingEdgeEnabled(true);
                receivableBoardBinding.myLastReceivablesRecycler.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);

                //dismiss progress here
                recsListProgress.dismiss();

                if (periodicRecs!=null && !periodicRecs.isEmpty()) {
                    //create a method for all this...
                    receivableBoardBinding.layoutLastReceivables.setVisibility(View.VISIBLE);
                    receivableBoardBinding.noReceivableFound.setVisibility(View.GONE);
                    Log.d(REC_BOARD_TAG, "get last debts :  expenses size is "+periodicRecs.size());
                }
                else {
                    receivableBoardBinding.layoutLastReceivables.setVisibility(View.GONE);
                    receivableBoardBinding.noReceivableFound.setVisibility(View.VISIBLE);
                }
            });


        });

    }

}