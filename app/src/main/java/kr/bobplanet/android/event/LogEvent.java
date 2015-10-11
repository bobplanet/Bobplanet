package kr.bobplanet.android.event;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import de.greenrobot.event.EventBus;

/**
 * 사용자의 behavior 로그를 측정하기 위한 이벤트.
 * 본 갹체에 이벤트 내용을 담은 뒤 submit()을 실행할 때 Eventbus로 쏴준다.
 * 지금은 MainApplication에서 이 이벤트를 받아 GA에 올려줌.
 *
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
public class LogEvent {
    /**
     * 이벤트의 종류.
     */
    final Category category;

    /**
     * 이벤트의 진원지 소스.
     */
    public final String source;

    /**
     * 이벤트 레이블.
     */
    public final String label;

    protected LogEvent(Category category, String source, @Nullable String label) {
        this.category = category;
        this.source = source;
        this.label = label;
    }

    protected LogEvent(Category category, String source) {
        this(category, source, null);
    }

    /**
     * Activity가 실행될 때 onResume() 안에서 호출.
     *
     */
    public static LogEvent activityView(Activity src) {
        return new LogEvent(Category.ACTIVITY_VIEW, src.getClass().getName());
    }

    /**
     * Fragment가 실행될 때 onResume() 안에서 호출.
     *
     */
    public static LogEvent fragmentView(Fragment src) {
        return new LogEvent(Category.FRAGMENT_VIEW, src.getClass().getName());
    }

    /**
     * 본 이벤트를 전송.
	 * 지금은 MainApplication에서 받아 GA로 올려주게 된다.
     */
    public void submit() {
        EventBus.getDefault().post(this);
    }

    /**
     * 스크린뷰인가?
     *
     */
    public boolean isScreenView() {
        switch (category) {
            case ACTIVITY_VIEW:
            case FRAGMENT_VIEW:
                return true;
            default:
                return false;
        }
    }

    /**
     * 이벤트의 종류.
     */
    protected enum Category {
        ACTIVITY_VIEW,
        FRAGMENT_VIEW,
        MEASURE
    }
}