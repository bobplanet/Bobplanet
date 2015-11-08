package kr.bobplanet.android.event;

/**
 * 아침메뉴 표시여부가 변경될 때 생성되는 이벤트 객체.
 *
 * @author heonkyu.jin
 * @version 15. 11. 8
 */
public class MorningMenuToggleEvent {
    public boolean isActive;

    public MorningMenuToggleEvent(boolean isActive) {
        this.isActive = isActive;
    }
}
