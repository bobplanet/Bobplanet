package kr.bobplanet.android.event;

import android.support.annotation.Nullable;

/**
 * 화면로딩시간, 네트웤 API latency 등 성능측정 로그를 전송하기 위한 이벤트 객체.
 * LogEvent를 상속받고, source 자리에 metric을 대신 넣는다.
 * (이 때, Metric은 Enum이므로 문자열 변환을 위해 Enum의 toString()을 이용)
 *
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
public class MeasureLogEvent extends LogEvent {
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
    protected MeasureLogEvent(Metric category, @Nullable String label, long value) {
        super(LogEvent.Category.MEASURE, category.toString(), label);
        this.value = value;
    }

    /**
     * 객체를 생성하는 factory method.
     *
     * @param category 측정종류(대분류)
     * @param label 측정대상(소분류)
     * @param metric 측정값
     * @return
     */
    public static MeasureLogEvent measure(Metric category, String label, long metric) {
        return new MeasureLogEvent(category, label, metric);
    }

    /**
     * 측정종류
     */
    public enum Metric {
        API_LATENCY("API_LATENCY");

        private String expression;

        Metric(String expression) {
            this.expression = expression;
        }

        @Override
        public String toString() {
            return expression;
        }
    }
}