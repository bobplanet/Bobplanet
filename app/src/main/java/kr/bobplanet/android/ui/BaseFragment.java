package kr.bobplanet.android.ui;

import android.support.v4.app.Fragment;

import kr.bobplanet.android.Constants;
import kr.bobplanet.android.event.UserLogEvent;

/**
 * 이 앱이 사용하는 모든 fragment의 엄마 클래스.
 * 
 * setUserVisibleHint()에서 이벤트를 기록한다.
 * - Activity가 처음 실행될 때 default로 화면에 보이는 Fragment에서는 setUserVisibleHint() 실행되지 않음
 * - 따라서 swipe 등에 의해 '나중에' 화면으로 나타나는 경우에만 실행되므로 가장 정확한 측정이 가능
 *
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
public class BaseFragment extends Fragment implements Constants {
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && isResumed()) {
            UserLogEvent.fragmentView(this);
        }
    }
}
