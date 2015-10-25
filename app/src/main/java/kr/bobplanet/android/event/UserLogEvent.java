package kr.bobplanet.android.event;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

/**
 * 사용자의 액션에 의해 일어나는 이벤트를 측정하기 위한 객체.
 *
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
public final class UserLogEvent extends LogEvent {
    private static final String ACTIVITY_VIEW = "ACTIVITY_VIEW";
    private static final String FRAGMENT_VIEW = "FRAGMENT_VIEW";
    private static final String DIALOG_VIEW = "DIALOG_VIEW";
    private static final String DIALOG_CANCEL = "DIALOG_CANCEL";
    private static final String LOGIN = "LOGIN";

    private UserLogEvent(String category, String label) {
        super(category, label);
    }

    /**
     * Activity가 실행될 때 onResume() 안에서 호출.
     *
     */
    public static void activityView(Activity src) {
        new UserLogEvent(ACTIVITY_VIEW, src.getClass().getName()).submit();
    }

    /**
     * Fragment가 실행될 때 onResume() 안에서 호출.
     *
     */
    public static void fragmentView(Fragment src) {
        new UserLogEvent(FRAGMENT_VIEW, src.getClass().getName()).submit();
    }

    public static void dialogView(String label) {
        new UserLogEvent(DIALOG_VIEW, label).submit();
    }

    public static void dialogCancel(String label) {
        new UserLogEvent(DIALOG_CANCEL, label).submit();
    }

    public static void login(String accountType) {
        new UserLogEvent(LOGIN, accountType).submit();
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
