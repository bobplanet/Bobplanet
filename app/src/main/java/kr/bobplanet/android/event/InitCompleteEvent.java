package kr.bobplanet.android.event;

/**
 * 특정 모듈의 초기화가 완료되었을 때 발생하는 이벤트 객체.
 * <code>StartActivity</code>에서 스플래시화면 종료여부 판단할 때 사용함.
 *
 * @author heonkyu.jin
 * @version 15. 10. 30..
 */
public class InitCompleteEvent {
    public final Class component;

    public InitCompleteEvent(Class component) {
        this.component = component;
    }
}
