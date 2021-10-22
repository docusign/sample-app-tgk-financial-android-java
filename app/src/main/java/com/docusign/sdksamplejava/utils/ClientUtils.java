package com.docusign.sdksamplejava.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public class ClientUtils {

    public static void setSignedStatus(@NonNull Context context, @NonNull String clientPref, boolean signed) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String signedStatus;
        if (clientPref.equals(Constants.CLIENT_A_PREF))
            signedStatus = Constants.CLIENT_A_SIGNED_STATUS;
        else if(clientPref.equals(Constants.CLIENT_B_PREF)) signedStatus = Constants.CLIENT_B_SIGNED_STATUS;
        else signedStatus = Constants.CLIENT_C_SIGNED_STATUS;

        sharedPreferences.edit().putBoolean(signedStatus, signed).apply();
    }

    public static boolean getSignedStatus(@NonNull Context context, @NonNull String clientPref) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String signedStatus;
        if (clientPref.equals(Constants.CLIENT_A_PREF))
            signedStatus = Constants.CLIENT_A_SIGNED_STATUS;
        else if (clientPref.equals(Constants.CLIENT_B_PREF)) signedStatus = Constants.CLIENT_B_SIGNED_STATUS;
        else signedStatus = Constants.CLIENT_C_SIGNED_STATUS;

        return sharedPreferences.getBoolean(signedStatus, false);
    }
}
