package cm.abimmobiledev.mybudgetizer.useful;

import android.app.ProgressDialog;
import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class Util {

    public static AlertDialog.Builder initAlertDialogBuilder(Context context, String title, String message){
        AlertDialog.Builder appDialog = new AlertDialog.Builder(context);
        appDialog.setTitle(title);
        appDialog.setMessage(message);
        appDialog.setCancelable(true);
        return appDialog;
    }

    public static ProgressDialog initProgressDialog(Context context, String title) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle(title);

        return progressDialog;
    }

    public static boolean stringNotFilled(String  stToControl){
        return stToControl == null || stToControl.isEmpty();
    }

    public static boolean isValidDate(String dateT) {
        //format valid date : yyyy/MM/dd hh:mm:ss (19)
        if (dateT==null || dateT.length()!=19)
            return false;

        String[] dateSplit = dateT.split("/");
        String[] timeSplit = dateT.split(":");

        try {
            String y = dateT.substring(0, 3);
            String m = dateT.substring(5, 6);
            String d = dateT.substring(8, 9);
            String h = dateT.substring(11, 12);
            String min = dateT.substring(14, 15);
            Integer.parseInt(y);
            Integer.parseInt(m);
            Integer.parseInt(d);
            Integer.parseInt(h);
            Integer.parseInt(min);
        }
        catch (Exception exception){
            return false;
        }

        return dateSplit.length == 3 && timeSplit.length == 3;
    }

    public static String getMonthFormatted(int monthOfYear) {
        String month;
        if (monthOfYear+1<10)
            month = "0"+(monthOfYear+1);
        else
            month = String.valueOf(monthOfYear+1);

        return month;
    }

    public static String getDayFormatted(int dayOfMonth) {
        String month;
        if (dayOfMonth<10)
            month = "0"+dayOfMonth;
        else
            month = String.valueOf(dayOfMonth);

        return month;
    }

}
