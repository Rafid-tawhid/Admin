package com.hk.meditechadmin.CommonStatic;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.hk.meditechadmin.ModelClass.Patient;

public class Common {
    public static Patient currentPatient;

    //internet connection investigate
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
            if (networkInfo != null) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
