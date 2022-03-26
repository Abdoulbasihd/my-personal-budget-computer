package cm.abimmobiledev.mybudgetizer.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityMainMenuBinding;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;

public class MainMenuActivity extends AppCompatActivity {

    ActivityMainMenuBinding mainMenuBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainMenuBinding = DataBindingUtil.setContentView(this, R.layout.activity_main_menu);

        //implement this group first....
        mainMenuBinding.cardBudget.setOnClickListener(aboutView -> Snackbar.make(aboutView, getString(R.string.not_yet_implemented), Snackbar.LENGTH_LONG).show());
        mainMenuBinding.cardDebts.setOnClickListener(aboutView -> Snackbar.make(aboutView, getText(R.string.not_yet_implemented), Snackbar.LENGTH_LONG).show());
        mainMenuBinding.cardReceivable.setOnClickListener(aboutView -> Snackbar.make(aboutView, getString(R.string.not_yet_implemented), Snackbar.LENGTH_LONG).show());

        mainMenuBinding.cardAboutApp.setOnClickListener(aboutView -> {
            BottomSheetDialog aboutBD = new BottomSheetDialog(MainMenuActivity.this);
            aboutBD.setContentView(R.layout.bottom_sheet_about_app);
            aboutBD.show();
        });
        mainMenuBinding.cardAccountManager.setOnClickListener(aboutView -> Snackbar.make(aboutView, getString(R.string.not_yet_implemented), Snackbar.LENGTH_LONG).show());

        mainMenuBinding.cardExpenses.setOnClickListener(expensesView -> ExNavigation.openExpensesHome(MainMenuActivity.this));
    }


}