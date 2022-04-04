package cm.abimmobiledev.mybudgetizer.ui.debt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.nav.DebtNavigator;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;

public class DebtRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt_registration);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DebtNavigator.openDebtsHome(DebtRegistrationActivity.this);
    }
}