package kr.bobplanet.android;

import android.app.Activity;
import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * 앱이 실행되는 디바이스의 환경정보를 파악하는 용도로 사용되는 유틸리티 클래스.
 *
 * @author heonkyu.jin
 * @version 2015.9.29
 */
public class DeviceEnvironment {
    private static final String TAG = DeviceEnvironment.class.getSimpleName();
	
    /**
     * Time limit for the application to wait on a response from Play Services.
     */
    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	/**
	 * Google Play Services가 설치되어있는지 확인.
	 *
	 * @return 설치여부(true면 있음. false면 없음)
	 */
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
