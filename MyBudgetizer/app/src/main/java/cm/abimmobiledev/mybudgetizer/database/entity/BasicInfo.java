package cm.abimmobiledev.mybudgetizer.database.entity;

public class BasicInfo {

    public String sticker; //for filtering purpose. not need for account only ?

    public String entitled;

    public double amount;

    public BasicInfo(String entitled, double amount, String sticker) {
        this.entitled = entitled;
        this.amount = amount;
        this.sticker = sticker;
    }

    public String getEntitled() {
        return entitled;
    }

    public void setEntitled(String entitled) {
        this.entitled = entitled;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getSticker() {
        return sticker;
    }

    public void setSticker(String sticker) {
        this.sticker = sticker;
    }
}
