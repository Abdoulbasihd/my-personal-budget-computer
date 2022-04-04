package cm.abimmobiledev.mybudgetizer.ui.debt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityDebtBoardBinding;
import cm.abimmobiledev.mybudgetizer.nav.DebtNavigator;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;

public class DebtBoardActivity extends AppCompatActivity {

    ActivityDebtBoardBinding debtBoardBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        debtBoardBinding = DataBindingUtil.setContentView(this, R.layout.activity_debt_board);

        debtBoardBinding.newDebt.setOnClickListener(newDebtView -> DebtNavigator.openNewDebt(DebtBoardActivity.this));
        debtBoardBinding.cardRefresh.setOnClickListener(refreshView -> {
            //TODO refresh board
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExNavigation.openMainHome(DebtBoardActivity.this);
    }
}