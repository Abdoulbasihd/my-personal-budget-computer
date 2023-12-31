package cm.abimmobiledev.mybudgetizer.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.NumberFormat;

import cm.abimmobiledev.mybudgetizer.BR;
import cm.abimmobiledev.mybudgetizer.database.entity.Expense;

public class ExpenseRegViewModel extends BaseObservable {

    private Expense expense;

    public ExpenseRegViewModel() {
        this.expense = new Expense("", 0, "", "", "");
    }

    @Bindable
    public String getEntitle(){
        return expense.getEntitled();
    }

    public void setEntitle(String entitle) {
        expense.setEntitled(entitle);
        notifyPropertyChanged(BR.entitle);
    }

    @Bindable
    public String getAmount(){

        try {
            //return String.valueOf(expense.getAmount());
            BigDecimal bigExp = new BigDecimal(expense.getAmount(),  MathContext.DECIMAL64);
            return  bigExp.toPlainString();

        }catch (NullPointerException exception){
            return "";
        }
    }

    public void  setAmount(String amount) {
        try {
            expense.setAmount(Double.parseDouble(amount));
        } catch (NumberFormatException|NullPointerException ignore){
            expense.setAmount(0);
        }
        notifyPropertyChanged(BR.amount);

    }

    @Bindable
    public String getDateTimeOfExpense(){
        return expense.getDateTimeOfExpense();
    }

    public void setDateTimeOfExpense(String dateTimeOfExpense) {
        expense.setDateTimeOfExpense(dateTimeOfExpense);
        notifyPropertyChanged(BR.dateTimeOfExpense);
    }

    @Bindable
    public String getReason(){
        return expense.getReason();
    }

    public void  setReason (String reason){
        expense.setReason(reason);
        notifyPropertyChanged(BR.reason);
    }

    @Bindable
    public String getSticker(){
        return expense.getSticker();
    }

    public void  setSticker (String sticker){
        expense.setReason(sticker);
        notifyPropertyChanged(BR.sticker);
    }



}
