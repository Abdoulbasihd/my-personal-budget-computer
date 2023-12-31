package cm.abimmobiledev.mybudgetizer.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "account")
public class Account extends BasicInfo{

    @ColumnInfo(name = "account_id")
    @PrimaryKey
    public int accountId;

    public String currency;

    @ColumnInfo(name = "cash_balance")
    public double cashBalance;

    @ColumnInfo(name = "mobile_wallet_balance")
    public double mobileWalletBalance;

    @ColumnInfo(name = "bank_balance")
    public double bankBalance;

    //@ColumnInfo(name = "balance")
   // public double balance;

    //amount ==> this is computed and could have been unsaved... when we have an expense, we've to substract here and in upfront...

    @ColumnInfo(name = "budgetized_balance")
    public double budgetizedBalance; //this balance should be regularly computed and update... We don't count expired non consumed part of budget... And when amount is consumed, we no more consider it...

    public Account(String entitled, String currency) {
        super(entitled, 0, "");
        this.currency = currency;
        this.cashBalance = 0;
        this.mobileWalletBalance = 0;
        this.bankBalance = 0;
       // this.balance = 0;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getCashBalance() {
        return cashBalance;
    }

    public void setCashBalance(double cashBalance) {
        this.cashBalance = cashBalance;
    }

    public double getMobileWalletBalance() {
        return mobileWalletBalance;
    }

    public void setMobileWalletBalance(double mobileWalletBalance) {
        this.mobileWalletBalance = mobileWalletBalance;
    }

    public double getBankBalance() {
        return bankBalance;
    }

    public void setBankBalance(double bankBalance) {
        this.bankBalance = bankBalance;
    }

    public double getBudgetizedBalance() {
        return budgetizedBalance;
    }

    public void setBudgetizedBalance(double budgetizedBalance) {
        this.budgetizedBalance = budgetizedBalance;
    }

    public void setBalance(){
        this.setAmount(bankBalance + cashBalance + mobileWalletBalance);
    }

    public double getBalance() {
        return this.getAmount();
    }
}
