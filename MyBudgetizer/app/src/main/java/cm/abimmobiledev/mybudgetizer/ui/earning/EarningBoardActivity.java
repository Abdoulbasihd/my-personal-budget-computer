package cm.abimmobiledev.mybudgetizer.ui.earning;

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
import cm.abimmobiledev.mybudgetizer.databinding.ActivityEarningBoardBinding;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.nav.IncNavigator;
import cm.abimmobiledev.mybudgetizer.ui.earning.adapter.IncomeAdapter;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class EarningBoardActivity extends AppCompatActivity {

    ActivityEarningBoardBinding earningBoardBinding;

    AlertDialog.Builder earningBoardDialog;
    ProgressDialog earningsListProgress;
    ProgressDialog earningBoardResumeProgress;
    List<Earning> periodicEarnings;

    private static final String INCOMES_BOARD_TAG = "EARN_B_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        earningBoardBinding = DataBindingUtil.setContentView(this, R.layout.activity_earning_board);

        earningBoardDialog = Util.initAlertDialogBuilder(this, getString(R.string.new_expense), getString(R.string.save_done));
        earningBoardDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            //NOTHING TO DO, just close
        });
        earningsListProgress = Util.initProgressDialog(this, getString(R.string.looking_up));
        earningBoardResumeProgress = Util.initProgressDialog(this, getString(R.string.looking_up));

        earningBoardBinding.newIncome.setOnClickListener(newExpenseView -> {
            //open expense registration page
            IncNavigator.openNewIncome(EarningBoardActivity.this);
        });

        earningBoardBinding.showMore.setOnClickListener(moveExpensesView -> {
            BottomSheetMoreEarningsFragment moreEarningsBD = new BottomSheetMoreEarningsFragment();
            //moreExpenseBD.setContentView(R.layout.fragment_bottom_sheet_more_expense_menu);
            moreEarningsBD.show(getSupportFragmentManager(), "ModalBottomSheet");

        });


        earningBoardBinding.cardRefresh.setOnClickListener(v -> earningBoardViewDataSetup());
        earningBoardViewDataSetup();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExNavigation.openMainHome(EarningBoardActivity.this);
    }

    public void earningBoardViewDataSetup(){
        final Calendar calendar = Calendar.getInstance();
        setPeriodicIncomes(getCurrentDayFormatted(calendar), PERIOD_DAILY);
        setPeriodicIncomes(getCurrentMonthFormatted(calendar), PERIOD_MONTHLY);
        setPeriodicIncomes(getCurrentYearFormatted(calendar), PERIOD_YEARLY);

        // : get 10 last entries of earnings and show it
        getLastIncomes(10);
    }

    /**
     * <h4>Select and compute incomes of a year, a month or a year</h4>
     * @param period String formatted either for day (yyyy/mm/dd) or for month (yyyy/mm) of just a year (yyyy). Later, looks how you could add  for weeds
     * @param periodicity int : PERIOD_DAILY, PERIOD_MONTHLY or PERIOD_YEARLY. Add for weeks later. to identify which value is to modify
     */
    public void setPeriodicIncomes(String period, int periodicity) {

        // : add loader
        earningBoardResumeProgress.show();
        ExecutorService periodicEarningBoardComputerServ = Executors.newSingleThreadExecutor();

        periodicEarningBoardComputerServ.execute(() -> {

            try {
                BudgetizerAppDatabase earningSelectorAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());

                List<Earning> periodicEarnings = earningSelectorAppDatabase.earningDAO().loadAllEarningLikeFormattedDate("%"+period+"%");


                runOnUiThread(() -> {
                    try {
                        double computedAmount = computeTotalIncome(periodicEarnings);
                        if (periodicity==PERIOD_DAILY)
                            earningBoardBinding.todayIncomesValue.setText(String.format("%s F. CFA", computedAmount));
                        else if (periodicity==PERIOD_MONTHLY)
                            earningBoardBinding.thisMonthIncomesValue.setText(String.format("%s F. CFA", computedAmount));
                        else if (periodicity==PERIOD_YEARLY)
                            earningBoardBinding.thisYearIncomesValue.setText(String.format("%s F. CFA", computedAmount));
                        // may be you could add for week

                    } catch (BudgetizerGeneralException exception) {
                        Log.d(INCOMES_BOARD_TAG, "selectPeriodicExpenses: "+exception.getLocalizedMessage(), exception);
                    }
                    earningBoardResumeProgress.dismiss();
                });
            }
            catch (Exception exception) {
                Log.d(INCOMES_BOARD_TAG, "selectPeriodicExpenses: "+exception.getLocalizedMessage(), exception);
            }

        });


    }

    /**
     * <h1>Should move this method some where else. may be in the view model ?</h1>
     * <h2>Compute total earning amount corresponding to a list of incomes (or earnings)</h2>
     * @param earnings List of earning
     * @return a double, total income
     * @throws BudgetizerGeneralException when list is null
     */
    public static double computeTotalIncome(List<Earning> earnings) throws BudgetizerGeneralException {
        if (earnings == null)
            throw new BudgetizerGeneralException("Earning list should not be null");

        double totalAmount = 0;

        for (int counter=0; counter<earnings.size(); counter++) {

            Earning earning = earnings.get(counter);

            if (earning==null)
                throw new BudgetizerGeneralException("Element of the expenses list shouldn't be null");

            totalAmount = totalAmount + earning.getAmount();

        }

        return totalAmount;
    }

    public void getLastIncomes(int numberOfElements) {

        earningsListProgress.show();
        ExecutorService lastExpensesServ = Executors.newSingleThreadExecutor();

        lastExpensesServ.execute(() -> {
            BudgetizerAppDatabase lastIncomesAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());
            periodicEarnings = lastIncomesAppDatabase.earningDAO().getLastEarnings(numberOfElements);

            IncomeAdapter incomeAdapter = new IncomeAdapter(periodicEarnings);

            runOnUiThread(() -> {


                earningBoardBinding.myLastIncomesRecycler.setHasFixedSize(true);
                earningBoardBinding.myLastIncomesRecycler.setLayoutManager(new LinearLayoutManager(this));
                earningBoardBinding.myLastIncomesRecycler.setAdapter(incomeAdapter);
                //earningBoardBinding.myLastIncomesRecycler.setVerticalScrollBarEnabled(true);
                //earningBoardBinding.myLastIncomesRecycler.setVerticalFadingEdgeEnabled(true);
                //earningBoardBinding.myLastIncomesRecycler.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);

                //dismiss progress here
                earningsListProgress.dismiss();

                if (periodicEarnings!=null && !periodicEarnings.isEmpty()) {
                    //create a method for all this...
                    earningBoardBinding.layoutLastIncomes.setVisibility(View.VISIBLE);
                    earningBoardBinding.noIncomeElement.setVisibility(View.GONE);
                    Log.d(INCOMES_BOARD_TAG, "get last incomes :  expenses size is "+periodicEarnings.size());
                }
                else {

                    earningBoardBinding.layoutLastIncomes.setVisibility(View.GONE);
                    earningBoardBinding.noIncomeElement.setVisibility(View.VISIBLE);
                }
            });


        });

    }


}