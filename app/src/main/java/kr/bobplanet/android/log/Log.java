package kr.bobplanet.android.log;

import com.google.android.gms.analytics.Tracker;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONObject;

import kr.bobplanet.android.App;

/**
 * 각종 로그를 측정하기 위한 모든 이벤트들의 엄마 클래스.
 * 이벤트 객체를 생성한 뒤 dispatch()를 실행하면 됨.
 * 
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
abstract public class Log {
    protected Log() {
    }

    protected void dispatch() {
        Tracker tracker = App.getTracker();
        Tracker rollup = App.getRollupTracker();
        MixpanelAPI mixpanel = App.getMixpanel();

        String deviceId = App.getUserManager().getDevice().getId();
        mixpanel.identify(deviceId);

        if (App.getUserManager().hasAccount()) {
            String userId = App.getUserManager().getUserId();
            tracker.set("&uid", userId);
            rollup.set("&uid", userId);
            mixpanel.alias(userId, deviceId);
        }

        dispatch(tracker);
        if (this instanceof Rollupable) {
            dispatch(rollup);
        }
    }

    /**
     * 이벤트를 서버로 전송 요청
     */
    abstract protected void dispatch(Tracker tracker);
}