package kr.bobplanet.android;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 여러 클래스에서 공유하는 상수. 클래스 이름으로 참조하기 귀찮아서 interface로 구현함.
 * 상수값을 이용하고자 하는 클래스에서는 implement만 하면 됨.
 *
 * - 상용빌드와 개발빌드에서 달라지는 값이 있어 BuildConfig를 이용함
 *
 * @author hkjinlee on 15. 9. 29
 */
public interface AppConstants {
	/**
	 * 상용 릴리즈의 클라이언트ID. 
	 */
    String ANDROID_CLIENT_ID_RELEASE =
            "603054087850-2kjko3ai98mdk3j2igap589ovni27jbp.apps.googleusercontent.com";
	/**
	 * 개발 릴리즈의 클라이언트ID.
	 */
    String ANDROID_CLIENT_ID_DEV =
            "603054087850-2e4d6q2t8992f9j6nedl8qp1ejr9ibaj.apps.googleusercontent.com";
	/**
	 * 클라이언트ID. BuildConfig을 이용하여 적절한 값을 선택.
	 */
    String ANDROID_CLIENT_ID = BuildConfig.DEV_VERSION ?
            ANDROID_CLIENT_ID_DEV : ANDROID_CLIENT_ID_RELEASE;

	/**
	 * 데이터를 가져올 Google AppEngine의 root URL
	 */
    String BACKEND_ROOT_URL = "https://kr-bobplanet.appspot.com/_ah/api/";
	
	/**
	 * Google Cloud Message의 기본 발신자ID.
	 * Google Cloud의 프로젝트 단위로 지정되므로 개발 릴리즈와 상용 릴리즈 사이에 차이가 없음.
	 */
    String GCM_SENDER_ID = "603054087850";


	/**
	 * Intent나 Bundle, Preferences에서 이용될 key값들.
	 */
    String DATE_ARGUMENT = "DATE_ARGUMENT";
    String EXTRA_MENU_ICON = "EXTRA_MENU_ICON";
    String EXTRA_MENU_TITLE = "EXTRA_MENU_TITLE";
    String MENUID_ARGUMENT = "MENUID_ARGUMENT";
    String MENU_ARGUMENT = "MENU_ARGUMENT";
    String DATE_DISPLAY = "DATE_DISPLAY";
    String SENT_GCM_TOKEN_TO_SERVER = "SENT_GCM_TOKEN_TO_SERVER";
    String HAS_LAUNCHED_BEFORE = "HAS_LAUNCHED_BEFORE";
    String HAS_DISMISSED_SWIPE_NOTICE = "HAS_DISMISSED_SWIPE_NOTICE";

	/**
	 * 공용으로 사용할 DateFormat 객체들
	 */
    DateFormat DATEFORMAT_YMD = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat DATEFORMAT_YMDE = new SimpleDateFormat("yyyy/MM/dd(EEE)");

    String DAILY_VIEW_ACTION = "DAILY_VIEW_ACTION";
    String ACTION_THUMB_UP = "ACTION_THUMB_UP";
    String ACTION_THUMB_DOWN = "ACTION_THUMB_DOWN";
}
