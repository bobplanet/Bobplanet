package kr.bobplanet.android;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by hkjinlee on 15. 9. 29..
 */
public class DeviceEnvironment {
    private static final String TAG = DeviceEnvironment.class.getSimpleName();
    /**
     * Time limit for the application to wait on a response from Play Services.
     */
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

}
