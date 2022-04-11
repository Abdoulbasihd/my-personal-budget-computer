package cm.abimmobiledev.mybudgetizer.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "debt")
public class Debt extends BasicInfo{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "debt_id")
    public int debtId;

    @ColumnInfo(name = "refunded_or_paid")
    public boolean refundedOrPaid;

    @ColumnInfo(name = "loaning_date")
    public String loaningDate;

    @ColumnInfo(name = "repayment_due_date")
    public String repaymentDueDate;

    @ColumnInfo(name = "repayment_date")
    public String repaymentDate;

    public String description;

    @ColumnInfo(name = "creditor_name")
    public String creditorName;

    @ColumnInfo(name = "creditor_contact")
    public String creditorContact;

    public String telltale; //witness  ==> temoin


    public Debt(String entitled, double amount, String loanDate, String dueDate, String creditorName, String creditorContact, String description, String sticker, String telltale) {
        super(entitled, amount, sticker);
        this.loaningDate = loanDate;
        this.repaymentDueDate = dueDate;
        this.creditorName = creditorName;
        this.creditorContact = creditorContact;
        this.description = description;
        this.telltale = telltale;
    }

    public int getDebtId() {
        return debtId;
    }

    public void setDebtId(int debtId) {
        this.debtId = debtId;
    }

    public boolean isRefundedOrPaid() {
        return refundedOrPaid;
    }

    public void setRefundedOrPaid(boolean refundedOrPaid) {
        this.refundedOrPaid = refundedOrPaid;
    }

    public String getLoaningDate() {
        return loaningDate;
    }

    public void setLoaningDate(String loaningDate) {
        this.loaningDate = loaningDate;
    }

    public String getRepaymentDueDate() {
        return repaymentDueDate;
    }

    public void setRepaymentDueDate(String repaymentDueDate) {
        this.repaymentDueDate = repaymentDueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }

    public String getCreditorContact() {
        return creditorContact;
    }

    public void setCreditorContact(String creditorContact) {
        this.creditorContact = creditorContact;
    }

    public String getRepaymentDate() {
        return repaymentDate;
    }

    public void setRepaymentDate(String repaymentDate) {
        this.repaymentDate = repaymentDate;
    }

    public String getTelltale() {
        return telltale;
    }

    public void setTelltale(String telltale) {
        this.telltale = telltale;
    }
}
