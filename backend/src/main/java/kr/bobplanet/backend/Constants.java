package kr.bobplanet.backend;

/**
 * Backend에서 사용되는 공통 상수값을 모아놓은 인터페이스.
 *
 * @author heonkyu.jin
 * @version 15. 9. 29..
 */
public interface Constants {
	/**
	 * 
	 */
    String GCM_API_KEY = "AIzaSyCgCD6Jbj7CqraZxDlOo7ZzaG7wVlN51MU";

	/**
	 *
	 */
    String API_OWNER = "backend.bobplanet.kr";

	/**
	 * 안드로이드 및 웹애플리케이션들의 클라이언트ID.
	 * API접근권한 제어를 위해 사용됨 (이게 없으면 아무나 API를 호출할 수 있음)
	 */
    String CLIENTID_ANDROID_RELEASE = BuildConfig.CLIENTID_ANDROID_RELEASE;
    String CLIENTID_ANDROID_DEV = BuildConfig.CLIENTID_ANDROID_DEV;
    String CLIENTID_WEB = BuildConfig.CLIENTID_WEB;
    String ANDROID_AUDIENCE = CLIENTID_WEB;

	/**
	 * OAuth 접근권한 제어시 사용할 email scope.
	 */
    String EMAIL_SCOPE =
            "https://www.googleapis.com/auth/userinfo.email";
}
