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
import kr.bobplanet.android.event.LogEvent;
import kr.bobplanet.android.event.MeasureLogEvent;

/**
 * 커스텀 애플리케이션 클래스.
 * 애플리케이션 전체 scope에서 관리해야 할 EntityVault, Volley, Google Analytics 등을 관리.
 * 
 * - EntityVault의 싱글턴은 여기에서 관리함
 * - 공용 유틸리티 로직 제공을 위해 Singleton 인터페이스 제공함 (casting 없어도 되는 장점)
 * - GA Tracker는 여러개 생성하면 PV가 n배로 잡히므로 이 Singleton 내에서 관리.
 *
 * @author hkjinlee on 15. 9. 29..
 */
public class MainApplication extends Application {
    public static final String TAG = MainApplication.class.getSimpleName();

	/**
	 * 싱글턴 인스턴스
	 */
    private static MainApplication instance;

	/**
	 * EntityVault 객체.
	 */
	private EntityVault entityVault;
	
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
    
	//private User currentUser;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        if (tracker == null) {
            Log.i(TAG, "Initializing Google Analytics");
            GoogleAnalytics ga = GoogleAnalytics.getInstance(this);
            tracker = ga.newTracker(R.xml.ga_config);
            ga.enableAutoActivityReports(this);
            ga.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        }

        EventBus.getDefault().register(this);
    }

	/**
	 * 싱글턴 인스턴스 조회.
	 */
    public static synchronized MainApplication getInstance() {
        return instance;
    }

    protected void setCurrentUser(Person person) {
        String iid = InstanceID.getInstance(this).getId();
        Log.d(TAG, "iid = " + iid);

        Log.d(TAG, "ID = " + person.getId());
    }

	/**
	 * EntityVault 조회
	 */
	public EntityVault getEntityVault() {
		if (entityVault == null) {
			entityVault = new EntityVault();
		}
		
		return entityVault;
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

    @SuppressWarnings("unused")
    public void onEvent(LogEvent logEvent) {
        Log.d(TAG, "LogEvent: " + logEvent.source);
        if (logEvent.isScreenView()) {
            tracker.setScreenName(logEvent.source);
            tracker.send(new HitBuilders.ScreenViewBuilder().build());
        }
    }

    @SuppressWarnings("unused")
    public void onEvent(MeasureLogEvent logEvent) {
        Log.d(TAG, "MeasureLogEvent: " + logEvent.source);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("measure")
                .setAction(logEvent.source)
                .setLabel(logEvent.label)
                .setValue(logEvent.value)
                .build());
    }
}
