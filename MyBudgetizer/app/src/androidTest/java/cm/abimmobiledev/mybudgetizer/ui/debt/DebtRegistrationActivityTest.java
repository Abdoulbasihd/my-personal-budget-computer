package cm.abimmobiledev.mybudgetizer.ui.debt;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DebtRegistrationActivityTest {

    DebtRegistrationActivity debtRegistrationActivity;

    @Before
    public void setUp() {
        debtRegistrationActivity = new DebtRegistrationActivity();
    }

    @After
    public void tearDown() {
        debtRegistrationActivity = null;
    }

    @Test
   public void requiredDebtRegistrationDataFilled() {
        assertTrue
                (
                        debtRegistrationActivity.requiredDebtRegistrationDataFilled(
                                "title",
                                "200",
                                "2022-04-06",
                                "2022-05-05",
                                "testor"
                        )
                );
   }
}