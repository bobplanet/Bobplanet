package kr.bobplanet.android.event;

import android.app.Activity;
import android.support.v4.app.Fragment;

/**
 * 사용자의 액션에 의해 일어나는 이벤트를 측정하기 위한 객체.
 *
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
public final class UserLogEvent extends LogEvent {
    private static final String ACTIVITY_VIEW = "ACTIVITY_VIEW";
    private static final String FRAGMENT_VIEW = "FRAGMENT_VIEW";

    private UserLogEvent(String category, String label) {
        super(category, label);
    }

    /**
     * Activity가 실행될 때 onResume() 안에서 호출.
     *
     */
    public static UserLogEvent activityView(Activity src) {
        return new UserLogEvent(ACTIVITY_VIEW, src.getClass().getName());
    }

    /**
     * Fragment가 실행될 때 onResume() 안에서 호출.
     *
     */
    public static LogEvent fragmentView(Fragment src) {
        return new UserLogEvent(FRAGMENT_VIEW, src.getClass().getName());
    }

    /**
     * 스크린뷰인가?
     *
     */
    public boolean isScreenView() {
        switch (category) {
            case ACTIVITY_VIEW:
            case FRAGMENT_VIEW:
                return true;
            default:
                return false;
        }
    }
}
