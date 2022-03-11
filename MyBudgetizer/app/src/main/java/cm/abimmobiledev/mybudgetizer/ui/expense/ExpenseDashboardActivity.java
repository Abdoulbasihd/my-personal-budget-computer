package cm.abimmobiledev.mybudgetizer.ui.expense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityExpenseDashboardBinding;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;

public class ExpenseDashboardActivity extends AppCompatActivity {

    ActivityExpenseDashboardBinding expenseDashboardBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        expenseDashboardBinding = DataBindingUtil.setContentView(this, R.layout.activity_expense_dashboard);

        expenseDashboardBinding.backFromExpenseMenu.setOnClickListener(backView -> this.onBackPressed());

        expenseDashboardBinding.newExpense.setOnClickListener(newExpenseView -> {
            //TODO : open expense registration page
        });

        expenseDashboardBinding.showMore.setOnClickListener(moveExpensesView -> {
            //TODO go to expense search & result page.
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExNavigation.openMainHome(ExpenseDashboardActivity.this);
    }
}