package kr.bobplanet.android;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.plus.model.people.Person;

/**
 * Created by hkjinlee on 15. 9. 29..
 */
public class MainApplication extends Application {
    public static final String TAG = MainApplication.class.getSimpleName();

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    //private User currentUser;

    private static MainApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    protected static synchronized MainApplication getInstance() {
        return instance;
    }

    protected void setCurrentUser(Person person) {
        String iid = InstanceID.getInstance(this).getId();
        Log.d(TAG, "iid = " + iid);

        Log.d(TAG, "ID = " + person.getId());
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return requestQueue;
    }

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
}
