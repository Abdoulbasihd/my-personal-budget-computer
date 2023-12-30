package cm.abimmobiledev.mybudgetizer.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import java.math.BigDecimal;
import java.math.MathContext;

import cm.abimmobiledev.mybudgetizer.BR;
import cm.abimmobiledev.mybudgetizer.database.entity.Budget;

public class BudgetRegVM extends BaseObservable {

    private Budget budgetReg;

    public BudgetRegVM(){
        budgetReg = new Budget("", "", 0, "", "", "");
        budgetReg.setDescription("");
    }

    @Bindable
    public String getEntitle(){
        return budgetReg.getEntitled();
    }

    public void setEntitle(String entitle) {
        budgetReg.setEntitled(entitle);
        notifyPropertyChanged(BR.entitle);
    }

    @Bindable
    public String getAmount(){

        try {
            BigDecimal bigBudget = new BigDecimal(budgetReg.getAmount(),  MathContext.DECIMAL64);
            return bigBudget.toPlainString();
        }catch (NullPointerException exception){
            return "";
        }
    }

    public void  setAmount(String amount) {
        try {
            budgetReg.setAmount(Double.parseDouble(amount));
        } catch (NumberFormatException|NullPointerException ignore){
            budgetReg.setAmount(0);
        }
        notifyPropertyChanged(BR.amount);
    }

    @Bindable
    public String getStartDate(){
        return budgetReg.getBeginningDate();
    }

    public void setStartDate(String startDate){
        budgetReg.setBeginningDate(startDate);
        notifyPropertyChanged(BR.startDate);
    }

    @Bindable
    public String getEndDate(){
        return budgetReg.getEndDate();
    }

    public void  setEndDate(String endDate){
        budgetReg.setEndDate(endDate);
        notifyPropertyChanged(BR.endDate);
    }

    @Bindable
    public String getSticker(){
        return budgetReg.getSticker();
    }

    public void  setSticker(String sticker) {
        budgetReg.setSticker(sticker);
        notifyPropertyChanged(BR.sticker);
    }

    @Bindable
    public String getDescription(){
        return budgetReg.getDescription();
    }

    public void setDescription(String desc){
        budgetReg.setDescription(desc);
        notifyPropertyChanged(BR.description);
    }

}
