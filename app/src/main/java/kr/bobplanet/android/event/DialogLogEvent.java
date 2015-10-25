package kr.bobplanet.android.event;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * 대화창을 띄우고 닫는 행위를 추적하기 위한 이벤트 객체.
 * 
 * @author heonkyu.jin
 * @version 15. 10. 25
 */
public class DialogLogEvent extends LogEvent {
    private static final String ACTION_VIEW = "ACTION_VIEW";
    private static final String ACTION_CANCEL = "ACTION_CANCEL";

    private String dialogType;
    private String action;

    private DialogLogEvent(String dialogType, String action) {
        this.dialogType = dialogType;
        this.action = action;
    }

    public static void dialogView(String dialogType) {
        new DialogLogEvent(dialogType, ACTION_VIEW).dispatch();
    }

    public static void dialogCancel(String dialogType) {
        new DialogLogEvent(dialogType, ACTION_CANCEL).dispatch();
    }

    protected void dispatch(Tracker tracker) {
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(dialogType)
                .setAction(action)
                .build());
    }
}
