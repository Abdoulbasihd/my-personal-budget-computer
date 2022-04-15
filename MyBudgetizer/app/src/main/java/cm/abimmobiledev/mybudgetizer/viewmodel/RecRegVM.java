package cm.abimmobiledev.mybudgetizer.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import cm.abimmobiledev.mybudgetizer.BR;
import cm.abimmobiledev.mybudgetizer.database.entity.Receivable;

public class RecRegVM extends BaseObservable {
    private Receivable receivable;

    public RecRegVM() {
        this.receivable = new Receivable("", 0, "", "", "", "", "", "", "", "");
    }

    @Bindable
    public String getEntitle(){
        return receivable.getEntitled();
    }

    public void setEntitle(String entitle){
        receivable.setEntitled(entitle);
        notifyPropertyChanged(BR.entitle);
    }

    @Bindable
    public String getAmount(){
        try {
            return String.valueOf(receivable.getAmount());
        }catch (NullPointerException exception){
            return "";
        }
    }

    public void setAmount(String amount){
        try {
            receivable.setAmount(Double.parseDouble(amount));
        } catch (NumberFormatException|NullPointerException ignore){
            receivable.setAmount(0);
        }
        notifyPropertyChanged(BR.amount);
    }

    @Bindable
    public String getDescription(){
        return receivable.getDescription();
    }

    public void  setDescription (String description){
        receivable.setDescription(description);
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public String getSticker(){
        return receivable.getSticker();
    }

    public void  setSticker (String sticker){
        receivable.setSticker(sticker);
        notifyPropertyChanged(BR.sticker);
    }

    @Bindable
    public String getDebtorNames(){
        return receivable.getDebtorName();
    }

    public void  setDebtorNames (String debtorNames){
        receivable.setDebtorName(debtorNames);
        notifyPropertyChanged(BR.debtorNames);
    }

    @Bindable
    public String getDebtorContact(){
        return receivable.getDebtorContact();
    }

    public void  setDebtorContact (String debtorContact){
        receivable.setDebtorContact(debtorContact);
        notifyPropertyChanged(BR.debtorContact);
    }

    @Bindable
    public String getTelltale(){
        return receivable.getTelltale();
    }

    public void  setTelltale (String telltale){
        receivable.setTelltale(telltale);
        notifyPropertyChanged(BR.telltale);
    }

    @Bindable
    public String getLoanDate(){
        return receivable.getLoanDate();
    }

    public void setLoanDate(String dateOfLoaning) {
        receivable.setLoanDate(dateOfLoaning);
        notifyPropertyChanged(BR.loanDate);
    }

    @Bindable
    public String getDueDate(){
        return receivable.getDueDate();
    }

    public void setDueDate(String repaymentDueDate) {
        receivable.setDueDate(repaymentDueDate);
        notifyPropertyChanged(BR.dueDate);
    }

    @Bindable
    public String getDisbursementDate(){
        return receivable.getDisbursementDate();
    }

    public void setDisbursementDate(String disbursementDate) {
        receivable.setDisbursementDate(disbursementDate);
        notifyPropertyChanged(BR.disbursementDate);
    }

}
