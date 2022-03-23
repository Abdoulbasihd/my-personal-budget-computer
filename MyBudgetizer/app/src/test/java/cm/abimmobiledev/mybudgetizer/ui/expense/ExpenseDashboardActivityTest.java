package cm.abimmobiledev.mybudgetizer.ui.expense;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cm.abimmobiledev.mybudgetizer.database.entity.Expense;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;

public class ExpenseDashboardActivityTest {

   // ExpenseDashboardActivity expenseDashboardActivity;
    List<Expense> expenses;

    @Before
    public void setUp() throws Exception {
     //   expenseDashboardActivity = new ExpenseDashboardActivity();

        expenses = new ArrayList<>();

        Expense expense = new Expense("test", 300, "2012/12/12 12:12:12", "Not set");
        Expense expense1 = new Expense("test1", 1000, "2012/12/12 12:12:12", "Not set");
        Expense expense2 = new Expense("test2", 2000, "2012/12/12 12:12:12", "Not set");
        Expense expense3 = new Expense("test3", 3000, "2012/12/12 12:12:12", "Not set");
        Expense expense4 = new Expense("test4", 4000, "2012/12/12 12:12:12", "Not set");

        expenses.add(expense);
        expenses.add(expense1);
        expenses.add(expense2);
        expenses.add(expense3);
        expenses.add(expense4);
    }

    @After
    public void tearDown() throws Exception {
       // expenseDashboardActivity = null;
        expenses = null;
    }

    @Test
    public void computeExpendedAmount() throws BudgetizerGeneralException {
        double total = ExpenseDashboardActivity.computeExpendedAmount(expenses);
        assertEquals(10300, total, 0.1);
    }

    @Test
    public void computeExpendedAmount_null() {
        assertThrows(BudgetizerGeneralException.class, ()->ExpenseDashboardActivity.computeExpendedAmount(null));
    }


    @Test
    public void getCurrentDayFormatted() {
        //suivant la journée, mettre à jour la valeur de expected
        assertEquals("2022/03/18", ExpenseDashboardActivity.getCurrentDayFormatted(Calendar.getInstance()));
    }

    @Test
    public void getCurrentMonthFormatted() {
        //suivant la mois, mettre à jour la valeur de expected
        assertEquals("2022/03", ExpenseDashboardActivity.getCurrentMonthFormatted(Calendar.getInstance()));
    }

    @Test
    public void getCurrentYearFormatted() {
        //suivant l'année, mettre à jour la valeur de expected
        assertEquals("2022", ExpenseDashboardActivity.getCurrentYearFormatted(Calendar.getInstance()));
    }
}