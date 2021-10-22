package com.docusign.sdksamplejava.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();
    private static final int BUFFER_SIZE = 1024 * 2;

    public static boolean isNetworkAvailable(@NonNull Context context) {
        boolean result = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            if (networkCapabilities != null) {
                result =
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI_AWARE) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_LOWPAN);
            }
        } else {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    result = true;
                }
            }
        }
        return result;
    }

    @Nullable
    public static File convertAssetToFile(@NonNull Context context, @NonNull String assetFileName, @NonNull String filePath) {
        try {
            InputStream inputStream = context.getAssets().open(assetFileName);
            File newFile = new File(filePath);
            FileOutputStream outputStream = new FileOutputStream(newFile);
            copy(inputStream, outputStream);
            return newFile;
        } catch (IOException exception) {
            Log.e(TAG, exception.getMessage());
        }
        return null;
    }

    private static int copy(@NonNull InputStream input, @NonNull OutputStream output) {
        byte[] buffer = new byte[BUFFER_SIZE];
        BufferedInputStream inputStream = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream outputStream = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0;

        try {
            while (true) {
                int n = inputStream.read(buffer, 0, BUFFER_SIZE);

                if (n == -1) {
                    outputStream.flush();
                    break;
                }

                outputStream.write(buffer, 0, n);
                count += n;
            }
        } catch (IOException exception) {
            Log.e(TAG, exception.getMessage());
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException exception) {
                Log.e(TAG, exception.getMessage());
            }

        }
        return count;
    }
}
