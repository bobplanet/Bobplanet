package kr.bobplanet.android.event;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * 사용자의 액션에 의해 일어나는 이벤트를 측정하기 위한 객체.
 * 현재는 로그인 이벤트 등을 측정하는 용도로 사용.
 *
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
public final class UserLogEvent extends LogEvent {
    private static final String LOGIN = "LOGIN";

    private String category;
    private String label;

    private UserLogEvent(String category, String label) {
        this.category = category;
        this.label = label;
    }

    /**
     *
     */
    public static void login(String accountType) {
        new UserLogEvent(LOGIN, accountType).dispatch();
    }

    protected void dispatch(Tracker tracker) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setLabel(label)
                .build());
    }
}
