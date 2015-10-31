package kr.bobplanet.android;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * 여러 클래스에서 공유하는 상수. 클래스 이름으로 참조하기 귀찮아서 interface로 구현함.
 * 소수값을 이용하고자 하는 클래스에서는 implement만 하면 됨.
 *
 * - 상용빌드와 개발빌드에서 달라지는 값이 있어 BuildConfig를 이용함
 *
 * @author hkjinlee on 15. 9. 29
 */
public interface Constants {
    /**
     * 구글 계정 type
     */
    String ACCOUNT_GOOGLE = "Google";

    /**
     * 페이스북 계정 type
     */
    String ACCOUNT_FACEBOOK = "Facebook";

    /**
     * 네이버 계정 type
     */
    String ACCOUNT_NAVER = "Naver";

    /**
	 * 데이터를 가져올 Google AppEngine의 root URL
	 */
    String BACKEND_ROOT_URL = BuildConfig.BACKEND_ROOT_URL;

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

	/**
	 * 공용으로 사용할 DateFormat 객체들
	 */
    DateFormat DATEFORMAT_YMD = new SimpleDateFormat("yyyy-MM-dd");
    DateFormat DATEFORMAT_YMDE = new SimpleDateFormat("yyyy/MM/dd(EEE)");

    /**
     * '좋아요'의 기본점수
     */
    int VOTE_UP = 1;
    /**
     * '싫어요'의 기본점수
     */
    int VOTE_DOWN = -1;

    /**
     * WebView에 사용자ID를 넘길 때 사용되는 패러미터 이름
     */
    String WEBVIEW_USERID = "bobp_userId";
}
