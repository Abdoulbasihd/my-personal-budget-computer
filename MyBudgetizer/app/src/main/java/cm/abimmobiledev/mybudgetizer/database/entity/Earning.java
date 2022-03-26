package cm.abimmobiledev.mybudgetizer.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "earning")
public class Earning extends BasicInfo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "earning_id")
    public int earningId;

    @ColumnInfo(name = "funds_source")
    public String fundsSource;


    @ColumnInfo(name = "date_time_of_income")
    public String incomeDateAndTime;

    @ColumnInfo(name = "reason_or_desc")
    public String reasonOrDesc;

    public Earning(String entitled, double amount, String fundsSource, String incomeDateAndTime, String reasonOrDesc) {
        super(entitled, amount);
        this.fundsSource = fundsSource;
        this.incomeDateAndTime = incomeDateAndTime;
        this.reasonOrDesc = reasonOrDesc;
    }

    public int getEarningId() {
        return earningId;
    }

    public void setEarningId(int earningId) {
        this.earningId = earningId;
    }

    public String getIncomeDateAndTime() {
        return incomeDateAndTime;
    }

    public void setIncomeDateAndTime(String incomeDateAndTime) {
        this.incomeDateAndTime = incomeDateAndTime;
    }

    public String getReasonOrDesc() {
        return reasonOrDesc;
    }

    public void setReasonOrDesc(String reasonOrDesc) {
        this.reasonOrDesc = reasonOrDesc;
    }

    public String getFundsSource() {
        return fundsSource;
    }

    public void setFundsSource(String fundsSource) {
        this.fundsSource = fundsSource;
    }
}
