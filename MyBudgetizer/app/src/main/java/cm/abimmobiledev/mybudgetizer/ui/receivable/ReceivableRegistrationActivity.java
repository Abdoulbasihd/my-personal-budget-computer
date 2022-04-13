package cm.abimmobiledev.mybudgetizer.ui.receivable;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import cm.abimmobiledev.mybudgetizer.R;
import cm.abimmobiledev.mybudgetizer.nav.ReceivNav;

public class ReceivableRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receivable_registration);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        ReceivNav.openReceivablesHome(this);
    }
}