package kr.bobplanet.android;

import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import kr.bobplanet.android.signin.SignInManager;

/**
 * 커스텀 애플리케이션 클래스.
 * 애플리케이션이 전체적으로 사용할 객체들은 이 클래스의 멤버로 선언하여 lifecycle 관리함.
 * <p>
 * - 공용 유틸리티 로직 제공을 위해 Singleton 패턴 차용: App.getInstance()
 * - GA Tracker는 여러개 생성하면 PV가 n배로 잡히므로 이 Singleton 내에서 관리하는 게 맞음.
 *
 * @author hkjinlee on 15. 9. 29..
 */
public class App extends MultiDexApplication {
    public static final String TAG = App.class.getSimpleName();

    /**
     * 싱글턴 인스턴스
     */
    private static App instance;

    /**
     * 사용자정보 저장/관리
     */
    private UserManager userManager;

    /**
     * OAuth 로그인 관리
     */
    private SignInManager signInManager;

    private CacheManager cacheManager;

    /**
     * ApiProxy 객체.
     */
    private ApiProxy apiProxy;

    /**
     * Google Analytics 이용을 위한 Tracker 객체.
     */
    private Tracker tracker;

    /**
     * Volley의 RequestQueue.
     */
    private RequestQueue requestQueue;

    /**
     * Volley의 ImageLoader. 비동기식 이미지 다운로드를 위해 사용함.
     */
    private ImageLoader imageLoader;

    /**
     * Preferences 저장관리
     */
    private Preferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        prefs = new Preferences(this);
        cacheManager = new CacheManager();
        apiProxy = new ApiProxy(cacheManager);
        initializeTracker();

        userManager = new UserManager(this, prefs);
        signInManager = new SignInManager(this);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        imageLoader = new ImageLoader(requestQueue, new LruBitmapCache());
    }

    /**
     *
     */
    private void initializeTracker() {
        if (tracker == null) {
            Log.i(TAG, "Initializing Google Analytics");
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            tracker = ga.newTracker(R.xml.ga_config);
            ga.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }

    /**
     *
     */
    public static UserManager getUserManager() {
        return instance.userManager;
    }

    /**
     *
     * @return
     */
    public static SignInManager getSignInManager() {
        return instance.signInManager;
    }

    /**
     *
     * @return
     */
    public static Tracker getTracker() {
        return instance.tracker;
    }

    /**
     * ApiProxy 조회
     */
    public static ApiProxy getApiProxy() {
        return instance.apiProxy;
    }

    /**
     *
     * @return
     */
    public static Preferences getPreferences() {
        return instance.prefs;
    }

    /**
     *
     * @return
     */
    public static RequestQueue getRequestQueue() {
        return instance.requestQueue;
    }

    /**
     * ImageLoader 조회
     */
    public static ImageLoader getImageLoader() {
        return instance.imageLoader;
    }
}
