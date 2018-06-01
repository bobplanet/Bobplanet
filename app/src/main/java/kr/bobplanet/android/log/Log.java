package kr.bobplanet.android.log;

//import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

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
/*
        dispatchGA(App.getTracker());
        if (this instanceof Rollupable) {
            dispatchGA(App.getRollupTracker());
        }
*/
        dispatchFirebase(App.getFirebase());
    }

    /**
     * 이벤트를 GA로 전송 요청
     */
//    abstract protected void dispatchGA(Tracker tracker);

    /**
     * 이벤트를 Firebase로 전송 요청
     */
    abstract protected void dispatchFirebase(FirebaseAnalytics firebase);
}