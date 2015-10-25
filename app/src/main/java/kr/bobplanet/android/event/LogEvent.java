package kr.bobplanet.android.event;

import com.google.android.gms.analytics.Tracker;

import kr.bobplanet.android.App;

/**
 * 각종 로그를 측정하기 위한 모든 이벤트들의 엄마 클래스.
 * 이벤트 객체를 생성한 뒤 dispatch()를 실행하면 됨.
 * 
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
abstract public class LogEvent {
    protected LogEvent() {
    }

    protected void dispatch() {
        Tracker tracker = App.getInstance().getTracker();
        dispatch(tracker);
    }

    /**
     * 이벤트를 서버로 전송 요청
     */
    abstract protected void dispatch(Tracker tracker);
}