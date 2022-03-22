package cm.abimmobiledev.mybudgetizer.ui.expense;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.room.Database;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.databinding.ActivityExpensesBinding;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;
import cm.abimmobiledev.mybudgetizer.nav.ExNavigation;

public class ExpensesActivity extends AppCompatActivity {

    private static final String EXPENSES_TAG = "EXP_TAG";

    ActivityExpensesBinding activityExpensesBinding;

    String searchParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityExpensesBinding = DataBindingUtil.setContentView(this, R.layout.activity_expenses);

        try {
            searchParam = getSearchParam(getIntent());

            if (isGivenDateSearch(searchParam))
                activityExpensesBinding.searchLayout.setVisibility(View.VISIBLE);
            else
                activityExpensesBinding.searchLayout.setVisibility(View.INVISIBLE);



        } catch (BudgetizerGeneralException exception) {
            Log.d(EXPENSES_TAG, "onCreate: "+exception.getLocalizedMessage(), exception);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ExNavigation.openExpensesHome(this);
    }

    public boolean isGivenDateSearch(String param) throws BudgetizerGeneralException {
        if(param==null)
            throw new BudgetizerGeneralException("Opening expense search... Param couldn't be null");

        return param.equalsIgnoreCase(ExNavigation.EXPENSE_OF_A_DATE);
    }

    public String getSearchParam (Intent intent) throws BudgetizerGeneralException {
        if (intent==null)
            throw new BudgetizerGeneralException("Opening expense search... Param intent couldn't be null");

        return intent.getStringExtra(ExNavigation.EXPENSE_SEARCH_PARAM);
    }


}