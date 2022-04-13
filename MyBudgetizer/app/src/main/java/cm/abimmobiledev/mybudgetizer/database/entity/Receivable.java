package cm.abimmobiledev.mybudgetizer.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "receivable")
public class Receivable extends BasicInfo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "receivable_id")
    public int receivableId;

    @ColumnInfo(name = "refunded_or_paid")
    public boolean refundedOrPaid;

    @ColumnInfo(name = "loan_date")
    public String loanDate;

    @ColumnInfo(name = "due_date")
    public String dueDate;

    @ColumnInfo(name = "disbursement_date")
    public String disbursementDate;

    @ColumnInfo(name = "debtor_name")
    public String debtorName;

    @ColumnInfo(name = "debtor_contact")
    public String debtorContact;

    public String description;

    public Receivable(String entitled, double amount, String sticker, String loanDate, String dueDate, String disbursementDate, String debtorName, String debtorContact, String description) {
        super(entitled, amount, sticker);
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.disbursementDate = disbursementDate;
        this.debtorName = debtorName;
        this.debtorContact = debtorContact;
        this.description = description;
    }

    public int getReceivableId() {
        return receivableId;
    }

    public void setReceivableId(int receivableId) {
        this.receivableId = receivableId;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDisbursementDate() {
        return disbursementDate;
    }

    public void setDisbursementDate(String disbursementDate) {
        this.disbursementDate = disbursementDate;
    }

    public String getDebtorName() {
        return debtorName;
    }

    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }

    public String getDebtorContact() {
        return debtorContact;
    }

    public void setDebtorContact(String debtorContact) {
        this.debtorContact = debtorContact;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRefundedOrPaid() {
        return refundedOrPaid;
    }

    public void setRefundedOrPaid(boolean refundedOrPaid) {
        this.refundedOrPaid = refundedOrPaid;
    }
}
