package kr.bobplanet.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;

import de.greenrobot.event.EventBus;
import kr.bobplanet.android.gcm.GcmEvent;
import kr.bobplanet.android.gcm.GcmServices;

/**
 * 본 애플리케이션의 메인 액티비티.
 * Splashscreen을 띄운다거나, 한번만 실행하면 되는 초기화 로직을 실행하는 등의 용도로 만들었다.
 *
 * - GCM 등록로직 수행 (등록결과를 받아오기 위해 EventBus를 이용함)
 * - 한번이라도 실행되면 SharedPreferences에 관련 정보 저장
 * - 본 앱의 기본화면인 DayViewActivity를 실행
 *
 * @author heonkyu.jin
 * @version 2015. 9. 30
 */
public class StartActivity extends ActivitySkeleton {
    private static final String TAG = StartActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EventBus.getDefault().register(this);
		
		// GCM 등록
        if (DeviceEnvironment.checkPlayServices(this)) {
            startService(new Intent(this, GcmServices.Registration.class));
        }

		// 최초실행인 경우 초기화로직 수행. 지금은 하는 일 없음.
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean(KEY_LAUNCHED_YN, false)) {
  //          onFirstRun();
            prefs.edit().putBoolean(KEY_LAUNCHED_YN, true).apply();
        }

		// 기본화면인 DayViewActivity를 화면에 띄움.
        startActivity(new Intent(this, DayViewActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * GCM서버 등록이 끝났을때(성공 or 실패) 호출되는 콜백.
     * 
     * @param event GCM서버 등록 결과
     */
    @SuppressWarnings("unused")
    public void onEvent(GcmEvent event) {
        switch (event.getType()) {
            case GcmEvent.REGISTER_SUCCESS:
                Log.d(TAG, "gcm register succeeded");
                break;
            case GcmEvent.REGISTER_FAILURE:
                Log.d(TAG, "gcm register failed");
                break;
        }
    }
}
