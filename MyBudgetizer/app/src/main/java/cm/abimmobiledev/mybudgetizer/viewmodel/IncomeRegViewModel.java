package cm.abimmobiledev.mybudgetizer.viewmodel;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import cm.abimmobiledev.mybudgetizer.BR;
import cm.abimmobiledev.mybudgetizer.database.entity.Earning;

public class IncomeRegViewModel extends BaseObservable {

    private Earning earning;

    public IncomeRegViewModel() {
        this.earning = new Earning("", 0, "", "", "", "");
    }

    @Bindable
    public String getEntitle(){
        return earning.getEntitled();
    }

    public void setEntitle(String entitle) {
        earning.setEntitled(entitle);
        notifyPropertyChanged(BR.entitle);
    }

    @Bindable
    public String getAmount(){
        try {
            return String.valueOf(earning.getAmount());
        }catch (NullPointerException exception){
            return "";
        }
    }

    public void  setAmount(String amount) {
        try {
            earning.setAmount(Double.parseDouble(amount));
        } catch (NumberFormatException|NullPointerException ignore){
            earning.setAmount(0);
        }
        notifyPropertyChanged(BR.amount);

    }

    @Bindable
    public String getIncomeDateTime(){
        return earning.getIncomeDateAndTime();
    }

    public void setIncomeDateTime(String dateTime) {
        earning.setIncomeDateAndTime(dateTime);
        notifyPropertyChanged(BR.incomeDateTime);
    }

    @Bindable
    public String getReasonOrDesc(){
        return earning.getReasonOrDesc();
    }

    public void  setReasonOrDesc (String reason){
        earning.setReasonOrDesc(reason);
        notifyPropertyChanged(BR.reasonOrDesc);
    }

    @Bindable
    public String getSourceFunds(){
        return earning.getFundsSource();
    }

    public void  setSourceFunds (String sourceFunds){
        earning.setFundsSource(sourceFunds);
        notifyPropertyChanged(BR.sourceFunds);
    }

    @Bindable
    public String getSticker(){
        return earning.getSticker();
    }

    public void  setSticker (String sticker){
        earning.setFundsSource(sticker);
        notifyPropertyChanged(BR.sticker);
    }

}
