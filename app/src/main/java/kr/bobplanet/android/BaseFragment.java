package kr.bobplanet.android;

import android.support.v4.app.Fragment;

import kr.bobplanet.android.event.UserLogEvent;

/**
 * 이 앱이 사용하는 모든 fragment의 엄마 클래스. 
 *
 * - onResume()에서 이벤트 측정
 *
 * @author heonkyu.jin
 * @version 2015. 10. 11
 */
public class BaseFragment extends Fragment implements AppConstants {
    @Override
    public void onResume() {
        super.onResume();
        UserLogEvent.fragmentView(this).submit();
    }
}
