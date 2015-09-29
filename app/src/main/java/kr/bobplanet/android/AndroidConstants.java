package kr.bobplanet.android;

import kr.bobplanet.android.BuildConfig;

/**
 * Created by hkjinlee on 15. 9. 29..
 */
public class AndroidConstants {
    private static final String ANDROID_CLIENT_ID_RELEASE =
            "603054087850-2kjko3ai98mdk3j2igap589ovni27jbp.apps.googleusercontent.com";
    private static final String ANDROID_CLIENT_ID_DEV =
            "603054087850-2e4d6q2t8992f9j6nedl8qp1ejr9ibaj.apps.googleusercontent.com";
    public static String ANDROID_CLIENT_ID = BuildConfig.DEV_VERSION ?
            ANDROID_CLIENT_ID_DEV : ANDROID_CLIENT_ID_RELEASE;
}
