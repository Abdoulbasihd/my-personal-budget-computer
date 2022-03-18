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
            int year = Integer.parseInt(y);
            int month = Integer.parseInt(m);
            int day = Integer.parseInt(d);
            int hh = Integer.parseInt(h);
            int mm = Integer.parseInt(min);
        }
        catch (Exception exception){
            return false;
        }

        return dateSplit.length == 3 && timeSplit.length == 3;
    }


}
