package com.framgia.fel1.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.framgia.fel1.R;

/**
 * Created by PhongTran on 04/14/2016.
 */
public class InternetUtils {
    public static boolean isInternetConnected(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean result = false;
        if (connectivityManager != null) {
            NetworkInfo networkInfo =
                    connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null
                    && (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                    || networkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                result = true;
            }
        }
        if(!result)
            new AlertDialog.Builder(context)
                    .setTitle(R.string.infor)
                    .setMessage(context.getResources().getString(R.string.not_internet))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        return result;
    }
    public static boolean isInternetConnected(Context context, boolean haveDialog) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean result = false;
        if (connectivityManager != null) {
            NetworkInfo networkInfo =
                    connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null
                    && (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE
                    || networkInfo.getType() == ConnectivityManager.TYPE_WIFI)) {
                result = true;
            }
        }
        if(!result && haveDialog)
            new AlertDialog.Builder(context)
                    .setTitle(R.string.infor)
                    .setMessage(context.getResources().getString(R.string.not_internet))
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
        return result;
    }
}
