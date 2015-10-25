package kr.bobplanet.android;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.plus.model.people.Person;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import kr.bobplanet.android.event.MeasureLogEvent;
import kr.bobplanet.android.event.UserLogEvent;
import kr.bobplanet.android.gcm.GcmServices;
import kr.bobplanet.backend.bobplanetApi.model.User;

/**
 * 커스텀 애플리케이션 클래스.
 * 애플리케이션 전체 scope에서 관리해야 할 ApiProxy, Volley, Google Analytics 등을 관리.
 * <p>
 * - EntityVault의 싱글턴은 여기에서 관리함
 * - 공용 유틸리티 로직 제공을 위해 Singleton 인터페이스 제공함 (casting 없어도 되는 장점)
 * - GA Tracker는 여러개 생성하면 PV가 n배로 잡히므로 이 Singleton 내에서 관리.
 *
 * @author hkjinlee on 15. 9. 29..
 */
public class App extends Application {
    public static final String TAG = App.class.getSimpleName();

    /**
     * 싱글턴 인스턴스
     */
    private static App instance;

    /**
     *
     */
    private UserManager userManager;

    /**
     * ApiProxy 객체.
     */
    private ApiProxy apiProxy;

    /**
     * Google Analytics 이용을 위한 Tracker 객체.
     */
    private Tracker tracker;

    /**
     * Volley의 RequestQueue. 현재는 이용하지 않음.
     */
    private RequestQueue requestQueue;

    /**
     * Volley의 ImageLoader. 비동기식 이미지 다운로드를 위해 사용함.
     */
    private ImageLoader imageLoader;

    private Preferences prefs;

    /**
     * 싱글턴 인스턴스 조회.
     */
    public static synchronized App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        prefs = new Preferences(this);
        apiProxy = new ApiProxy();

        userManager = new UserManager(this, prefs);

        initializeTracker();

        EventBus.getDefault().register(this);
    }

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
    public UserManager getUserManager() {
        return userManager;
    }

    /**
     * ApiProxy 조회
     */
    public ApiProxy getApiProxy() {
        return apiProxy;
    }

    public Preferences getPreferences() {
        return prefs;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;
    }

    /**
     * ImageLoader 조회
     */
    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (imageLoader == null) {
            imageLoader = new ImageLoader(this.requestQueue, new LruBitmapCache());
        }

        return imageLoader;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }

    /**
     * @param logEvent
     */
    @SuppressWarnings("unused")
    public void onEvent(UserLogEvent logEvent) {
        Log.v(TAG, "UserLogEvent: " + logEvent.source);
        if (logEvent.isScreenView()) {
            tracker.setScreenName(logEvent.source);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        } else {
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory(logEvent.source)
                    .setAction(logEvent.category)
                    .build());
        }
    }

    /**
     * @param logEvent
     */
    @SuppressWarnings("unused")
    public void onEvent(MeasureLogEvent logEvent) {
        Log.v(TAG, String.format("MeasureLogEvent: %s (%s)", logEvent.source, logEvent.label));
        tracker.send(new HitBuilders.TimingBuilder()
                .setCategory(logEvent.source)
                .setLabel(logEvent.label)
                .setValue(logEvent.value)
                .build());
    }
}
