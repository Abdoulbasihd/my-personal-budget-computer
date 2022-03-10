package cm.abimmobiledev.mybudgetizer.ui.expense;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;

public class ExpenseDashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_dashboard);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExNavigation.openMainHome(ExpenseDashboardActivity.this);
    }
}