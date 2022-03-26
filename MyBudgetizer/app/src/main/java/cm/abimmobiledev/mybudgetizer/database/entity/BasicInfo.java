package cm.abimmobiledev.mybudgetizer.database.entity;

public class BasicInfo {

    public String entitled;

    public double amount;

    public BasicInfo(String entitled, double amount) {
        this.entitled = entitled;
        this.amount = amount;
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

}
