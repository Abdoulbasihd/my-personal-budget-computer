package cm.abimmobiledev.mybudgetizer.ui.expense;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cm.abimmobiledev.mybudgetizer.database.entity.Expense;
import cm.abimmobiledev.mybudgetizer.exception.BudgetizerGeneralException;

public class ExpenseFilterActivityTest {

    List<Expense> expenses;

    @Before
    public void setUp() throws Exception {

        expenses = new ArrayList<>();
        Expense expense = new Expense( "test", 500, "12/12/2022 12:12:00", "R-A-S");
        expenses.add(expense);
        expenses.add(expense);
        expenses.add(expense);
        expenses.add(expense);
        expenses.add(expense);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void computeTotalAmount() throws BudgetizerGeneralException {
        double total = ExpenseFilterActivity.computeTotalAmount(expenses);
        assertEquals(2500, total, 0.1);
    }

    @Test
    public void computeTotalAmount_caseEmptyList() throws BudgetizerGeneralException {
        double total = ExpenseFilterActivity.computeTotalAmount(new ArrayList<>());
        assertEquals(0, total, 0.1);
    }



    @Test
    public void computeTotalAmount_error() throws BudgetizerGeneralException {
        assertThrows("Expense list should not be null", BudgetizerGeneralException.class,  ()->ExpenseFilterActivity.computeTotalAmount(null));
    }
}