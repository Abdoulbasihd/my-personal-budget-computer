package cm.abimmobiledev.mybudgetizer.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import cm.abimmobiledev.mybudgetizer.BR;
import cm.abimmobiledev.mybudgetizer.database.entity.Debt;

public class DebtRegistrationVM extends BaseObservable {

    private Debt debtReg;

    public DebtRegistrationVM(){
        debtReg = new Debt("", 0, "", "", "", "", "", "", "");
    }

    @Bindable
    public String getEntitle(){
        return debtReg.getEntitled();
    }

    public void setEntitle(String entitle) {
        debtReg.setEntitled(entitle);
        notifyPropertyChanged(BR.entitle);
    }

    @Bindable
    public String getAmount(){
        try {
            return String.valueOf(debtReg.getAmount());
        }catch (NullPointerException exception){
            return "";
        }
    }

    public void  setAmount(String amount) {
        try {
            debtReg.setAmount(Double.parseDouble(amount));
        } catch (NumberFormatException|NullPointerException ignore){
            debtReg.setAmount(0);
        }
        notifyPropertyChanged(BR.amount);

    }

    @Bindable
    public String getLoanDate(){
        return debtReg.getLoaningDate();
    }

    public void setLoanDate(String dateOfLoaning) {
        debtReg.setLoaningDate(dateOfLoaning);
        notifyPropertyChanged(BR.loanDate);
    }

    @Bindable
    public String getRepaymentDueDate(){
        return debtReg.getRepaymentDueDate();
    }

    public void setRepaymentDueDate(String repaymentDueDate) {
        debtReg.setRepaymentDueDate(repaymentDueDate);
        notifyPropertyChanged(BR.repaymentDueDate);
    }

    @Bindable
    public String getRepaymentDate(){
        return debtReg.getRepaymentDate();
    }

    public void setRepaymentDate(String repaymentDate) {
        debtReg.setRepaymentDate(repaymentDate);
        notifyPropertyChanged(BR.repaymentDate);
    }

    @Bindable
    public String getDescription(){
        return debtReg.getDescription();
    }

    public void  setDescription (String description){
        debtReg.setDescription(description);
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public String getSticker(){
        return debtReg.getSticker();
    }

    public void  setSticker (String sticker){
        debtReg.setSticker(sticker);
        notifyPropertyChanged(BR.sticker);
    }

    @Bindable
    public String getCreditorNames(){
        return debtReg.getCreditorName();
    }

    public void  setCreditorNames (String creditorNames){
        debtReg.setCreditorName(creditorNames);
        notifyPropertyChanged(BR.creditorNames);
    }

    @Bindable
    public String getCreditorContact(){
        return debtReg.getCreditorContact();
    }

    public void  setCreditorContact (String creditorContact){
        debtReg.setCreditorContact(creditorContact);
        notifyPropertyChanged(BR.creditorContact);
    }

    @Bindable
    public String getTelltale(){
        return debtReg.getTelltale();
    }

    public void  setTelltale (String telltale){
        debtReg.setTelltale(telltale);
        notifyPropertyChanged(BR.telltale);
    }

}
