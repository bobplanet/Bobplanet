package kr.bobplanet.android;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by hkjinlee on 15. 9. 29..
 */
public interface AppConstants {
    String ANDROID_CLIENT_ID_RELEASE =
            "603054087850-2kjko3ai98mdk3j2igap589ovni27jbp.apps.googleusercontent.com";
    String ANDROID_CLIENT_ID_DEV =
            "603054087850-2e4d6q2t8992f9j6nedl8qp1ejr9ibaj.apps.googleusercontent.com";
    String ANDROID_CLIENT_ID = BuildConfig.DEV_VERSION ?
            ANDROID_CLIENT_ID_DEV : ANDROID_CLIENT_ID_RELEASE;

    String BACKEND_ROOT_URL = "https://kr-bobplanet.appspot.com/_ah/api/";
    String GCM_SENDER_ID = "603054087850";


    String DATE_ARGUMENT = "DATE_ARGUMENT";
    String DATE_DISPLAY = "DATE_DISPLAY";
    String SENT_GCM_TOKEN_TO_SERVER = "SENT_GCM_TOKEN_TO_SERVER";
    String HAS_LAUNCHED_BEFORE = "HAS_LAUNCHED_BEFORE";

    DateFormat DATEFORMAT_YMD = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat DATEFORMAT_YMDE = new SimpleDateFormat("yyyy/MM/dd(EEE)");

    String DAILY_VIEW_ACTION = "DAILY_VIEW_ACTION";
    String ACTION_THUMB_UP = "ACTION_THUMB_UP";
    String ACTION_THUMB_DOWN = "ACTION_THUMB_DOWN";

    int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
}
