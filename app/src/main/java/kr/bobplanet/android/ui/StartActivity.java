package kr.bobplanet.android.ui;

import android.content.Intent;
import android.os.Bundle;

import kr.bobplanet.android.App;
import kr.bobplanet.android.DeviceEnvironment;
import kr.bobplanet.android.gcm.GcmServices;

/**
 * 본 애플리케이션의 메인 액티비티.
 * Splashscreen을 띄운다거나, 한번만 실행하면 되는 초기화 로직을 실행하는 등의 용도로 만들었다.
 * <p/>
 * - GCM 등록로직 수행 (등록결과를 받아오기 위해 EventBus를 이용함)
 * - 한번이라도 실행되면 SharedPreferences에 관련 정보 저장
 * - 본 앱의 기본화면인 DayViewActivity를 실행
 *
 * @author heonkyu.jin
 * @version 2015. 9. 30
 */
public class StartActivity extends BaseActivity {
    private static final String TAG = StartActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Google Play Services가 있는지 확인. 없을 경우 종료.
        if (!DeviceEnvironment.checkPlayServices(this)) {
            finish();
        }

        App.getInstance().getUserManager().loadDevice();

        startActivity(new Intent(this, DayActivity.class));
        finish();
    }
}
