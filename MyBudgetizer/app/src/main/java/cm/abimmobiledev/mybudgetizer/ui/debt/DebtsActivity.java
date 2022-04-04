package cm.abimmobiledev.mybudgetizer.ui.debt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.nav.DebtNavigator;

public class DebtsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debts);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DebtNavigator.openDebtsHome(DebtsActivity.this);
    }
}