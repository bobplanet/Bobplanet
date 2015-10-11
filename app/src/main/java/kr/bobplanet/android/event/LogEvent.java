package kr.bobplanet.android.event;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import de.greenrobot.event.EventBus;

/**
 * 각종 로그를 측정하기 위한 이벤트.
 * 본 갹체에 이벤트 내용을 담은 뒤 submit()을 실행할 때 Eventbus로 쏘면 MainApplication이 GA에 올려준다.
 * 
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
abstract public class LogEvent {
    /**
     * 이벤트의 종류.
     */
    final String category;

    /**
     * 이벤트의 진원지 소스.
     */
    public final String source;

    /**
     * 이벤트 레이블.
     */
    public final String label;

    protected LogEvent(String category, String source, @Nullable String label) {
        this.category = category;
        this.source = source;
        this.label = label;
    }

    protected LogEvent(String category, String source) {
        this(category, source, null);
    }

    /**
     * 본 이벤트를 전송.
	 * 지금은 MainApplication에서 받아 GA로 올려주게 된다.
     */
    public void submit() {
        EventBus.getDefault().post(this);
    }
}