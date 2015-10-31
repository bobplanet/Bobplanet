package kr.bobplanet.android.event;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Map;

/**
 * 사용자의 액션에 의해 일어나는 이벤트를 측정하기 위한 객체.
 * 현재는 로그인 이벤트 등을 측정하는 용도로 사용.
 *
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
public final class UserLogEvent extends LogEvent {
    private static final String LOGIN = "LOGIN";
    private static final String ACCOUNT_SELECT = "ACCOUNT_SELECT";

    private String category;
    private String label;
    private long value;

    private UserLogEvent(String category, String label, long value) {
        this.category = category;
        this.label = label;
        this.value = value;
    }

    private UserLogEvent(String category, String label) {
        this(category, label, -1);
    }

    /**
     *
     */
    public static void login(String accountType) {
        new UserLogEvent(LOGIN, accountType).dispatch();
    }

    /**
     *
     * @param accountType
     * @param displayOrder
     */
    public static void accountSelect(String accountType, int displayOrder) {
        new UserLogEvent(ACCOUNT_SELECT, accountType).dispatch();
    }

    protected void dispatch(Tracker tracker) {
        HitBuilders.EventBuilder builder = new HitBuilders.EventBuilder()
                .setCategory(category)
                .setLabel(label);

        if (value > 0) {
            builder.setValue(value);
        }

        tracker.send(builder.build());
    }
}
