package cm.abimmobiledev.mybudgetizer.ui.expense;

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
import cm.abimmobiledev.mybudgetizer.database.entity.Expense;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityExpenseDashboardBinding;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;
import cm.abimmobiledev.mybudgetizer.ui.expense.adapter.ExpenseAdapter;
import cm.abimmobiledev.mybudgetizer.useful.Util;

public class ExpenseDashboardActivity extends AppCompatActivity {

    ActivityExpenseDashboardBinding expenseDashboardBinding;

    private static final String EXP_DASH_TAG = "X_D_TAG";
    private static final int PERIOD_DAILY = 0;
    private static final int PERIOD_WEEKLY = 1;
    private static final int PERIOD_MONTHLY = 2;
    private static final int PERIOD_YEARLY = 3;

    AlertDialog.Builder expenseBoardDialog;
    ProgressDialog expenseListBoardProgress;
    ProgressDialog expenseBoardProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        expenseBoardDialog = Util.initAlertDialogBuilder(this, getString(R.string.new_expense), getString(R.string.save_done));
        expenseBoardDialog.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            //NOTHING TO DO, just close
        });
        expenseListBoardProgress = Util.initProgressDialog(this, getString(R.string.looking_up));
        expenseBoardProgress = Util.initProgressDialog(this, getString(R.string.looking_up));

        expenseDashboardBinding = DataBindingUtil.setContentView(this, R.layout.activity_expense_dashboard);

        expenseDashboardBinding.backFromExpenseMenu.setOnClickListener(backView -> this.onBackPressed());

        expenseDashboardBinding.newExpense.setOnClickListener(newExpenseView -> {
            //open expense registration page
            ExNavigation.openNewExpense(ExpenseDashboardActivity.this);
        });

        expenseDashboardBinding.showMore.setOnClickListener(moveExpensesView -> {
            // go to expense search & result page.
        });


        final Calendar calendar = Calendar.getInstance();
        setPeriodicExpenses(getCurrentDayFormatted(calendar), PERIOD_DAILY);
        setPeriodicExpenses(getCurrentMonthFormatted(calendar), PERIOD_MONTHLY);
        setPeriodicExpenses(getCurrentYearFormatted(calendar), PERIOD_YEARLY);

        // : get 10 last entries of expenses and show it
        getLastExpenses(10);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExNavigation.openMainHome(ExpenseDashboardActivity.this);
    }

    /**
     * <h4>Select and compute expenses of a year, a month or a year</h4>
     * @param period String formatted either for day (yyyy/mm/dd) or for month (yyyy/mm) of just a year (yyyy). Later, looks how you could add  for weeds
     * @param periodicity int : PERIOD_DAILY, PERIOD_MONTHLY or PERIOD_YEARLY. Add for weeks later. to identify which value is to modify
     */
    public void setPeriodicExpenses(String period, int periodicity) {

        //TODO : add loader
        expenseBoardProgress.show();
        ExecutorService periodicExpensesComputerServ = Executors.newSingleThreadExecutor();

        periodicExpensesComputerServ.execute(() -> {

            try {
                BudgetizerAppDatabase selectorAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());

                List<Expense> periodicExpenses = selectorAppDatabase.expenseDAO().loadAllExpenseOfAGivenMonth("%"+period+"%");


                runOnUiThread(() -> {
                        try {
                            double computedAmount = computeExpendedAmount(periodicExpenses);
                            if (periodicity==PERIOD_DAILY)
                                expenseDashboardBinding.todayExpenseValue.setText(String.format("%s F. CFA", computedAmount));
                            else if (periodicity==PERIOD_MONTHLY)
                                expenseDashboardBinding.thisMonthExpenseValue.setText(String.format("%s F. CFA", computedAmount));
                            else if (periodicity==PERIOD_YEARLY)
                                expenseDashboardBinding.thisYearExpenseValue.setText(String.format("%s F. CFA", computedAmount));
                            //TODO may be you could add for week

                        } catch (BudgetizerGeneralException exception) {
                            Log.d(EXP_DASH_TAG, "selectPeriodicExpenses: "+exception.getLocalizedMessage(), exception);
                        }
                    expenseBoardProgress.dismiss();
                });
            }
            catch (Exception exception) {
                Log.d(EXP_DASH_TAG, "selectPeriodicExpenses: "+exception.getLocalizedMessage(), exception);
            }

        });


    }


    public void getLastExpenses(int numberOfElements) {
        //TODO : add loader
        expenseListBoardProgress.show();
        ExecutorService lastExpensesServ = Executors.newSingleThreadExecutor();

        lastExpensesServ.execute(() -> {
            BudgetizerAppDatabase lastExpensesAppDatabase = BudgetizerAppDatabase.getInstance(getApplicationContext());
            List<Expense> periodicExpenses = lastExpensesAppDatabase.expenseDAO().getLastExpenses(numberOfElements);

            ExpenseAdapter expenseAdapter = new ExpenseAdapter(periodicExpenses);

            runOnUiThread(() -> {


                expenseDashboardBinding.myLastExpensesRecycler.setHasFixedSize(true);
                expenseDashboardBinding.myLastExpensesRecycler.setLayoutManager(new LinearLayoutManager(this));
                expenseDashboardBinding.myLastExpensesRecycler.setAdapter(expenseAdapter);
                expenseDashboardBinding.myLastExpensesRecycler.setVerticalScrollBarEnabled(true);
                expenseDashboardBinding.myLastExpensesRecycler.setVerticalFadingEdgeEnabled(true);
                expenseDashboardBinding.myLastExpensesRecycler.setVerticalScrollbarPosition(View.SCROLLBAR_POSITION_RIGHT);


                //todo : dismiss progress here
                expenseListBoardProgress.dismiss();

                if (periodicExpenses!=null && !periodicExpenses.isEmpty()) {
                    //TODO : create a method for all this...
                    expenseDashboardBinding.myLastExpensesRecycler.setVisibility(View.VISIBLE);
                    expenseDashboardBinding.noExpenseElement.setVisibility(View.GONE);
                }
                else {

                    expenseDashboardBinding.myLastExpensesRecycler.setVisibility(View.GONE);
                    expenseDashboardBinding.noExpenseElement.setVisibility(View.VISIBLE);
                }
            });


        });

    }

    /**
     * <h1>Should move this method some where else. may be in the view model ?</h1>
     * <h2>Compute total expended amount corresponding to a list of expenses</h2>
     * @param expenses List of expenses
     * @return a double, total expended
     * @throws BudgetizerGeneralException when list is null
     */
    public static double computeExpendedAmount(List<Expense> expenses) throws BudgetizerGeneralException {
        if (expenses == null)
            throw new BudgetizerGeneralException("Expenses list should not be null");

        double totalAmount = 0;

        for (int counter=0; counter<expenses.size(); counter++) {

            Expense expense = expenses.get(counter);

            if (expense==null)
                throw new BudgetizerGeneralException("Element of the expenses list shouldn't be null");

            totalAmount = totalAmount + expense.getAmount();

        }

        return totalAmount;
    }

    public static String getCurrentDayFormatted(Calendar c){
        final int mDay = c.get(Calendar.DAY_OF_MONTH);
        final int mMonth = c.get(Calendar.MONTH);
        final int mYear = c.get(Calendar.YEAR);
        return mYear + "/" + Util.getMonthFormatted(mMonth) + "/" + Util.getDayFormatted(mDay);
    }

    public static String getCurrentMonthFormatted(Calendar c){
        final int mMonth = c.get(Calendar.MONTH);
        final int mYear = c.get(Calendar.YEAR);
        return mYear + "/" + Util.getMonthFormatted(mMonth) ;

    }

    public static String getCurrentYearFormatted(Calendar c){
        final int mYear = c.get(Calendar.YEAR);
        return String.valueOf(mYear);
    }

}