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

}
