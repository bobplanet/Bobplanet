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
	 * 데이터를 가져올 Google AppEngine의 root URL
	 */
    String BACKEND_ROOT_URL = "https://kr-bobplanet.appspot.com/_ah/api/";
	
	/**
	 * Google Cloud Message의 기본 발신자ID.
	 * Google Cloud의 프로젝트 단위로 지정되므로 개발/상용이 동일한 값을 가짐
	 */
    String GCM_SENDER_ID = "603054087850";

	/**
	 * Intent나 Bundle, Preferences에서 이용될 key값들.
	 */
    String KEY_DATE = "KEY_DATE";
    String KEY_MENU = "KEY_MENU";
    String KEY_LAUNCHED_YN = "KEY_LAUNCHED_YN";
    String KEY_DISMISSED_NOTICE_YN = "KEY_DISMISSED_NOTICE_YN";
    String SENT_GCM_TOKEN_TO_SERVER = "SENT_GCM_TOKEN_TO_SERVER";

	/**
	 * 공용으로 사용할 DateFormat 객체들
	 */
    DateFormat DATEFORMAT_YMD = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat DATEFORMAT_YMDE = new SimpleDateFormat("yyyy/MM/dd(EEE)");
}
