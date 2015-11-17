package kr.bobplanet.android.log;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Activity나 Fragment와 같이 화면전환 이벤트를 측정하기 위한 객체.
 *
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
public final class ScreenLog extends Log implements Rollupable {
    private Category category;
    private String screenName;

    private ScreenLog(Category category, String screenName) {
        this.category = category;
        this.screenName = screenName;
    }

    /**
     * Activity가 실행될 때 onResume() 안에서 호출.
     *
     */
    public static void activityView(Activity src) {
        new ScreenLog(Category.ACTIVITY_VIEW, src.getClass().getSimpleName()).dispatch();
    }

    /**
     * Fragment가 실행될 때 onResume() 안에서 호출.
     *
     */
    public static void fragmentView(Fragment src) {
        new ScreenLog(Category.FRAGMENT_VIEW, src.getClass().getSimpleName()).dispatch();
    }

    protected void dispatch(Tracker tracker) {
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    private enum Category {
        ACTIVITY_VIEW,
        FRAGMENT_VIEW
    }
}
