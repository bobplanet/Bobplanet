package kr.bobplanet.android.log;

import android.os.Bundle;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * 화면로딩시간, 네트웤 API latency 등 성능측정 로그를 전송하기 위한 이벤트 객체.
 * LogEvent를 상속받고, target 자리에 metric을 대신 넣는다.
 * (이 때, Metric은 Enum이므로 문자열 변환을 위해 Enum의 toString()을 이용)
 *
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
public class MeasureLog extends Log {
    private static final String TAG = MeasureLog.class.getSimpleName();

    private static final String API_LATENCY = "API_LATENCY";

    private String category;
    private String name;

    /**
     * 측정값
     */
    public long value;

    /**
     * 생성자.
     *
     * @param category 측정종류(대분류)
     * @param name 측정대상(소분류)
     * @param value 측정값
     */
    private MeasureLog(String category, String name, long value) {
        this.category = category;
        this.name = name;
        this.value = value;
    }

    /**
     * 객체를 생성하는 factory method.
     *
     * @param apiName 측정대상(소분류)
     * @param value 측정값
     */
    public static void measureApiLatency(String apiName, long value) {
        new MeasureLog(API_LATENCY, apiName, value).dispatch();
    }

    @Override
    protected void dispatchGA(Tracker tracker) {
        tracker.send(new HitBuilders.TimingBuilder()
                .setCategory(category)
                .setVariable(name)
                .setValue(value)
                .build());
    }

    @Override
    protected void dispatchFirebase(FirebaseAnalytics firebase) {
        Bundle bundle = new Bundle();
        bundle.putString("category", this.category);
        bundle.putString("name", this.name);
        bundle.putLong("value", this.value);

        firebase.logEvent("measure", bundle);
    }
}