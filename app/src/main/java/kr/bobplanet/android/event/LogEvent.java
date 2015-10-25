package kr.bobplanet.android.event;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import de.greenrobot.event.EventBus;

/**
 * 각종 로그를 측정하기 위한 이벤트.
 * 본 갹체에 이벤트 내용을 담은 뒤 submit()을 실행하면 Eventbus를 거쳐 App이 GA에 올려준다.
 * 
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
abstract public class LogEvent {
    /**
     * 이벤트의 종류.
     */
    public final String category;

    /**
     * 이벤트의 진원지 소스. (화면명이거나 이벤트 유형)
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
     * 이벤트를 서버로 전송 요청
     */
    protected void submit() {
        EventBus.getDefault().post(this);
    }
}