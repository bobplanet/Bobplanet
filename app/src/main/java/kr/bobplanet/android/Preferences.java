package kr.bobplanet.android;

import android.content.Context;
import android.content.Entity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import kr.bobplanet.backend.bobplanetApi.model.User;

/**
 * SharedPreferences 키가 여기저기 흩어지는 일을 막기 위해 한군데로 몰아놓은 객체.
 * (솔직히 이게 정말 좋은 디자인인지는 모르겠음)
 *
 * @author heonkyu.jin
 * @version 15. 10. 17
 */
public class Preferences {
    private static final String TAG = Preferences.class.getSimpleName();

    private static final String USER = "USER";

    private static final String HAS_LAUNCHED = "HAS_LAUNCHED";
    private static final String HAS_DISMISSED_SWIPE_NOTICE = "HAS_DISMISSED_SWIPE_NOTICE";

    final SharedPreferences prefs;


    public Preferences(Context context) {
        this.prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public User loadUser() {
        String userString = prefs.getString(USER, null);
        Log.v(TAG, "userString = " + userString);
        return userString != null ? EntityParser.parseEntity(User.class, userString) : null;
    }

    public void storeUser(User user) {
        prefs.edit().putString(USER, EntityParser.toString(user)).apply();
    }

    public boolean hasDismissedSwipeNotice() {
        return prefs.getBoolean(HAS_DISMISSED_SWIPE_NOTICE, false);
    }

    public void setDismissedSwipeNotice() {
        prefs.edit().putBoolean(HAS_DISMISSED_SWIPE_NOTICE, true).apply();
    }

    public boolean hasLaunched() {
        return prefs.getBoolean(HAS_LAUNCHED, false);
    }

    public void setLaunched() {
        prefs.edit().putBoolean(HAS_LAUNCHED, true);
    }
}
