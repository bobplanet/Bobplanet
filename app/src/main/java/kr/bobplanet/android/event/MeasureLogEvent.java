package kr.bobplanet.android.event;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * 화면로딩시간, 네트웤 API latency 등 성능측정 로그를 전송하기 위한 이벤트 객체.
 * LogEvent를 상속받고, target 자리에 metric을 대신 넣는다.
 * (이 때, Metric은 Enum이므로 문자열 변환을 위해 Enum의 toString()을 이용)
 *
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
public class MeasureLogEvent extends LogEvent {
    private static final String TAG = MeasureLogEvent.class.getSimpleName();

    private static final String API_LATENCY = "API_LATENCY";

    private String category;
    private String label;

    /**
     * 측정값
     */
    public long value;

    /**
     * 생성자.
     *
     * @param category 측정종류(대분류)
     * @param label 측정대상(소분류)
     * @param value 측정값
     */
    private MeasureLogEvent(String category, String label, long value) {
        this.category = category;
        this.label = label;
        this.value = value;
    }

    /**
     * 객체를 생성하는 factory method.
     *
     * @param label 측정대상(소분류)
     * @param value 측정값
     */
    public static void measureApiLatency(String label, long value) {
        new MeasureLogEvent(API_LATENCY, label, value).dispatch();
    }

    protected void dispatch(Tracker tracker) {
        tracker.send(new HitBuilders.TimingBuilder()
                .setCategory(category)
                .setLabel(label)
                .setValue(value)
                .build());
    }
}