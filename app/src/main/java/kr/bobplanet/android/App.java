package kr.bobplanet.android;

import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
//import com.google.android.gms.analytics.GoogleAnalytics;
//import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import kr.bobplanet.android.beacon.BeaconMonitor;
import kr.bobplanet.android.event.NetworkExceptionEvent;
import kr.bobplanet.android.signin.SignInManager;
import kr.bobplanet.android.util.LruBitmapCache;

/**
 * 커스텀 애플리케이션 클래스.
 * 애플리케이션이 전체적으로 사용할 객체들은 이 클래스의 멤버로 선언하여 lifecycle 관리함.
 * <p>
 * - 공용 유틸리티 로직 제공을 위해 Singleton 패턴 차용: App.getInstance()
 * - GA Tracker는 여러개 생성하면 PV가 n배로 잡히므로 이 Singleton 내에서 관리하는 게 맞음.
 * - 네트웤 에러 발생시 ErrorActivity를 실행함.
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

    /**
     * 서버 API로부터 받아온 데이터의 캐슁 객체.
     */
    private CacheManager cacheManager;

    /**
     * ApiProxy 객체.
     */
    private ApiProxy apiProxy;

    /**
     * 비콘 감시용 객체.
     */
    private BeaconMonitor beaconMonitor;

    /**
     * Google Analytics 이용을 위한 Tracker 객체.
     */
//    private Tracker tracker;

    /**
     * Google Analytics의 rollup 리포트를 위한 Tracker
     */
//    private Tracker rollupTracker;

    /**
     * Firebase Analytics 인스턴스
     */
    private FirebaseAnalytics firebase;

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

        //beaconMonitor = new BeaconMonitor(this);

        EventBus.getDefault().register(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        EventBus.getDefault().unregister(this);
    }

    /**
     *
     */
    private void initializeTracker() {
        Log.i(TAG, "Initializing Trackers");

/*
        GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
        tracker = ga.newTracker(R.xml.ga_config);
        tracker.enableAdvertisingIdCollection(true);
        rollupTracker = ga.newTracker(R.xml.ga_rollup_config);
        rollupTracker.enableAdvertisingIdCollection(true);
*/

        firebase = FirebaseAnalytics.getInstance(this);
    }

    /**
     *
     */
    public static UserManager getUserManager() {
        return instance.userManager;
    }

    /**
     * @return
     */
    public static SignInManager getSignInManager() {
        return instance.signInManager;
    }

    /**
     * @return
     */
//    public static Tracker getTracker() {
//        return instance.tracker;
//    }

//    public static Tracker getRollupTracker() {
//        return instance.rollupTracker;
//    }

    public static FirebaseAnalytics getFirebase() {
        return instance.firebase;
    }

    public static BeaconMonitor getBeaconMonitor() {
        return instance.beaconMonitor;
    }

    /**
     * ApiProxy 조회
     */
    public static ApiProxy getApiProxy() {
        return instance.apiProxy;
    }

    /**
     * @return
     */
    public static Preferences getPreferences() {
        return instance.prefs;
    }

    /**
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    @SuppressWarnings("unused")
    public void onEvent(NetworkExceptionEvent event) {
        Toast.makeText(this, R.string.network_error_text, Toast.LENGTH_LONG).show();

/*
        new BaseDialogBuilder(this, "ERROR")
            .setTitle("error")
            .setView(LayoutInflater.from(this).inflate(R.layout.error_dialog, null))
            .show();
*/
    }
}
