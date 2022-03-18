package cm.abimmobiledev.mybudgetizer.useful;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UtilTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void stringNotFilled_empty() {
        assertTrue(Util.stringNotFilled(""));
    }

    @Test
    public void stringNotFilled_filled() {
        assertFalse(Util.stringNotFilled("a"));
    }

    @Test
    public void isValidDate() {
        String validDateTime = "2020/12/12 13:27:20";
        assertTrue(Util.isValidDate(validDateTime));
    }

    @Test
    public void isValidDate_caseParamNull() {
        assertFalse(Util.isValidDate(null));
    }

    @Test
    public void isValidDate_caseParamLenghtLa() {
        String validDateTime = "2020/12/12 13:27:20:783287";
        assertFalse(Util.isValidDate(validDateTime));
    }

    @Test
    public void isValidDate_separateDate() {
        String validDateTime = "2020/1/1/1 13:27:20";
        assertFalse(Util.isValidDate(validDateTime));
    }

    @Test
    public void isValidDate_SeparateTime() {
        String validDateTime = "2020/12/12 13:2:0:0";
        assertFalse(Util.isValidDate(validDateTime));
    }

    @Test
    public void isValidDate_alpha() {
        String validDateTime = "aa2a/12/12 13:2:0:0";
        assertFalse(Util.isValidDate(validDateTime));
    }


}