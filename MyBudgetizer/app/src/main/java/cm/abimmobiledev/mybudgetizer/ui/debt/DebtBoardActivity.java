package cm.abimmobiledev.mybudgetizer.ui.debt;

import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.PERIOD_DAILY;
import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.PERIOD_MONTHLY;
import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.PERIOD_YEARLY;
import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.getCurrentDayFormatted;
import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.getCurrentMonthFormatted;
import static cm.abimmobiledev.mybudgetizer.ui.expense.ExpenseDashboardActivity.getCurrentYearFormatted;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
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
import cm.abimmobiledev.mybudgetizer.database.entity.Earning;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityDebtBoardBinding;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.nav.DebtNavigator;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.ui.debt.adapter.DebtAdapter;
import cm.abimmobiledev.mybudgetizer.ui.earning.BottomSheetMoreEarningsFragment;
import cm.abimmobiledev.mybudgetizer.ui.earning.adapter.IncomeAdapter;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class DebtBoardActivity extends AppCompatActivity {

    ActivityDebtBoardBinding debtBoardBinding;


    AlertDialog.Builder debtBoardDialog;
    ProgressDialog debtsListProgress;
    ProgressDialog debtBoardResumeProgress;
    List<Debt> periodicDebts;

    private static final String DEBT_BOARD_TAG = "DbB_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        debtBoardBinding = DataBindingUtil.setContentView(this, R.layout.activity_debt_board);

        debtBoardDialog = Util.initAlertDialogBuilder(this, getString(R.string.new_debt), getString(R.string.save_done));
        debtBoardDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            //NOTHING TO DO, just close
        });
        debtsListProgress = Util.initProgressDialog(this, getString(R.string.looking_up));
        debtBoardResumeProgress = Util.initProgressDialog(this, getString(R.string.looking_up));

        debtBoardBinding.showMore.setOnClickListener(v -> {
            BottomSheetMoreDebtsFragment moreDebtsBottomSheet = new BottomSheetMoreDebtsFragment();
            moreDebtsBottomSheet.show(getSupportFragmentManager(), "MDebtModalBottomSheet");
        });

        debtBoardBinding.newDebt.setOnClickListener(newDebtView -> DebtNavigator.openNewDebt(DebtBoardActivity.this));
        debtBoardBinding.cardRefresh.setOnClickListener(refreshView -> earningBoardViewDataSetup());

        earningBoardViewDataSetup();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExNavigation.openMainHome(DebtBoardActivity.this);
    }


    public void earningBoardViewDataSetup(){
        final Calendar calendar = Calendar.getInstance();
        setPeriodicDebts(getCurrentDayFormatted(calendar), PERIOD_DAILY);
        setPeriodicDebts(getCurrentMonthFormatted(calendar), PERIOD_MONTHLY);
        setPeriodicDebts(getCurrentYearFormatted(calendar), PERIOD_YEARLY);

        // : get 10 last entries of earnings and show it
        getLastDebts(10);
    }

    /**
     * <h4>Select and compute incomes of a year, a month or a year</h4>
     * @param period String formatted either for day (yyyy/mm/dd) or for month (yyyy/mm) of just a year (yyyy). Later, looks how you could add  for weeds
     * @param periodicity int : PERIOD_DAILY, PERIOD_MONTHLY or PERIOD_YEARLY. Add for weeks later. to identify which value is to modify
     */
    public void setPeriodicDebts(String period, int periodicity) {

        // : add loader
        debtBoardResumeProgress.show();
        ExecutorService periodicEarningBoardComputerServ = Executors.newSingleThreadExecutor();

        periodicEarningBoardComputerServ.execute(() -> {

            try {
                BudgetizerAppDatabase earningSelectorAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());

                periodicDebts = earningSelectorAppDatabase.debtDAO().loadAllDebtLikeFormattedLoanDate("%"+period+"%");


                runOnUiThread(() -> {
                    try {
                        double computedAmount = computeTotalDebts(periodicDebts);
                        if (periodicity==PERIOD_DAILY)
                            debtBoardBinding.todayDebtValue.setText(String.format("%s F. CFA", computedAmount));
                        else if (periodicity==PERIOD_MONTHLY)
                            debtBoardBinding.thisMonthDebtValue.setText(String.format("%s F. CFA", computedAmount));
                        else if (periodicity==PERIOD_YEARLY)
                            debtBoardBinding.thisYearDebtValue.setText(String.format("%s F. CFA", computedAmount));
                        // may be you could add for week

                    } catch (BudgetizerGeneralException exception) {
                        Log.d(DEBT_BOARD_TAG, "selectPeriodicDebts: "+exception.getLocalizedMessage(), exception);
                    }
                    debtBoardResumeProgress.dismiss();
                });
            }
            catch (Exception exception) {
                Log.d(DEBT_BOARD_TAG, "selectPeriodicDebts: "+exception.getLocalizedMessage(), exception);
            }

        });


    }

    /**
     * <h1>Should move this method some where else. may be in the view model ?</h1>
     * <h2>Compute total earning amount corresponding to a list of incomes (or earnings)</h2>
     * @param debts List of debts
     * @return a double, total income
     * @throws BudgetizerGeneralException when list is null
     */
    public static double computeTotalDebts(List<Debt> debts) throws BudgetizerGeneralException {
        if (debts == null)
            throw new BudgetizerGeneralException("Debts list should not be null");

        double totalAmount = 0;

        for (int counter=0; counter<debts.size(); counter++) {

            Debt debt = debts.get(counter);

            if (debt==null)
                throw new BudgetizerGeneralException("Element of the debt list shouldn't be null");

            totalAmount = totalAmount + debt.getAmount();

        }

        return totalAmount;
    }

    public void getLastDebts(int numberOfElements) {

        debtsListProgress.show();
        ExecutorService lastExpensesServ = Executors.newSingleThreadExecutor();

        lastExpensesServ.execute(() -> {
            BudgetizerAppDatabase lastIncomesAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());
            periodicDebts = lastIncomesAppDatabase.debtDAO().getLastDebts(numberOfElements);

            DebtAdapter debtAdapter = new DebtAdapter(periodicDebts);

            runOnUiThread(() -> {


                debtBoardBinding.myLastDebtsRecycler.setHasFixedSize(true);
                debtBoardBinding.myLastDebtsRecycler.setLayoutManager(new LinearLayoutManager(this));
                debtBoardBinding.myLastDebtsRecycler.setAdapter(debtAdapter);
                //earningBoardBinding.myLastIncomesRecycler.setVerticalScrollBarEnabled(true);
                //earningBoardBinding.myLastIncomesRecycler.setVerticalFadingEdgeEnabled(true);
                //earningBoardBinding.myLastIncomesRecycler.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);

                //dismiss progress here
                debtsListProgress.dismiss();

                if (periodicDebts!=null && !periodicDebts.isEmpty()) {
                    //create a method for all this...
                    debtBoardBinding.layoutLastDebts.setVisibility(View.VISIBLE);
                    debtBoardBinding.noDebtFound.setVisibility(View.GONE);
                    Log.d(DEBT_BOARD_TAG, "get last debts :  expenses size is "+periodicDebts.size());
                }
                else {
                    debtBoardBinding.layoutLastDebts.setVisibility(View.GONE);
                    debtBoardBinding.noDebtFound.setVisibility(View.VISIBLE);
                }
            });


        });

    }


}