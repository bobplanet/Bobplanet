package kr.bobplanet.android.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Toast;

import kr.bobplanet.android.R;

/**
 * 일간메뉴화면에서 가장 마지막 날짜에서 오른쪽 swipe를 시도하면 toast 메시지를 보여주는 ViewPager.
 * onInterceptTouchEvent()가 여러번 호출되는 경우가 있어 eventConsumed를 통해 상태관리.
 *
 * @author heonkyu.jin
 * @version 15. 10. 22
 */
public class DayViewPager extends ViewPager {
    /**
     * 해당 이벤트가 처리되었는가?
     */
    private boolean eventConsumed;

    /**
     * 드래깅 이벤트가 시작된 위치의 X좌표
     */
    private float startDragX;

    public DayViewPager(Context context) {
        super(context);
    }

    public DayViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                eventConsumed = false;
                startDragX = x;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!eventConsumed && startDragX > x && getCurrentItem() == getAdapter().getCount() - 1) {
                    Toast.makeText(this.getContext(), R.string.last_page_notice, Toast.LENGTH_SHORT).show();
                    eventConsumed = true;
                }
                break;
        }
        return super.onInterceptTouchEvent(e);
    }
}
