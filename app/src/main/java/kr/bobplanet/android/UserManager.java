package kr.bobplanet.android;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.plus.model.people.Person;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import kr.bobplanet.android.gcm.GcmEvent;
import kr.bobplanet.backend.bobplanetApi.model.User;
import kr.bobplanet.backend.bobplanetApi.model.UserDevice;

/**
 * 사용자 identity를 관리하는 객체.
 *
 * - 앱이 실행되면 가장 먼저 Bobplanet 서버에 신규사용자ID(long)를 요청
 * - 거의 동시에 StartActivity는 Gcm 토큰 등록요청
 * - 위 두 작업이 모두 끝나면 preference와 서버에 사용자정보를 저장
 *
 * @author heonkyu.jin
 * @version 15. 10. 18
 */
public class UserManager {
    private static final String TAG = UserManager.class.getSimpleName();

    /**
     * 현재 사용자. 로그인하지 않은 사용자는 Google Account 없음
     */
    private UserDevice device;

    private Preferences prefs;

    /**
     *
     * @param context
     * @param prefs
     */
    public UserManager(Context context, Preferences prefs) {
        this.prefs = prefs;

        EventBus.getDefault().register(this);

        loadDeviceInfo(context);
    }

    /**
     * 사용자정보를 읽어온다. 없으면 서버에 등록요청.
     */
    private void loadDeviceInfo(Context context) {
        UserDevice d = prefs.loadDevice();
        if (d == null) {
            Log.i(TAG, "Device doesn't exists. Creating new device");
            device = new UserDevice();
            device.setLunchPushEnabled(true);
            device.setDinnerPushEnabled(true);
            device.setAndroidId(
                    Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
            device.setIid(InstanceID.getInstance(context).getId());

            App.getInstance().getApiProxy().registerDevice(device,
                    (result) -> {
                        Log.d(TAG, "Device registered");
                        device.setId(result.getId());
                        device.setUser(result.getUser());
                        updateDevice();
                    });
        } else {
            Log.i(TAG, "Device restored from prefs: " + d);
            this.device = d;
            Log.i(TAG, "user = " + getUserId());
        }
    }

    /**
     * 사용자번호 조회. 아무나 호출해도 됨.
     * @return
     */
    public String getUserId() {
        return device.getUser().getId();
    }

    /**
     * GCM 토큰 등록이 완료되었는지 알려준다. StartActivity에서 확인용으로 호출함.
     *
     * @return
     */
    public boolean isGcmRegistered() {
        Log.d(TAG, "GCM token = " + device.getGcmToken());
        return device.getGcmToken() != null;
    }

    /**
     * 구글이나 페이스북 계정이 등록된 사용자인지 확인. MenuActivity가 호출함.
     *
     * @return
     */
    public boolean hasAccount() {
        return device.getUser().getAccountType() != null;
    }

    public UserDevice getDevice() {
        return device;
    }

    /**
     * GCM서버 등록이 끝났을때(성공 or 실패) 호출되는 콜백.
     *
     * @param event GCM서버 등록 결과
     */
    @SuppressWarnings("unused")
    @DebugLog
    public void onEvent(GcmEvent event) {
        switch (event.getType()) {
            case GcmEvent.REGISTER_SUCCESS:
                Log.d(TAG, "gcm register succeeded");

                device.setGcmToken(event.getToken());
                updateDevice();
                break;
            case GcmEvent.REGISTER_FAILURE:
                Log.d(TAG, "gcm register failed");
                break;
        }
    }

    /**
     * 사용자정보에 변화가 생겼을 때 prefs와 서버에 동시반영.
     * 사용자번호와 GCM토큰 둘 다 있어야 함.
     */
    @DebugLog
    public void updateDevice() {
        if (device.getId() != null && device.getGcmToken() != null) {
            App.getInstance().getApiProxy().updateDevice(device);
            prefs.storeDevice(device);
        }
    }

    /**
     * 구글ID 로그인이 끝났을 때 호출해줘야 하는 함수.
     * @param person
     */
    @DebugLog
    public void updateUserGoogleAccount(Person person) {
        User user = device.getUser();

        user.setAccountType("Google");
        user.setAccountId(person.getId());
        user.setNickName(person.getDisplayName());
        user.setImage(person.getImage().getUrl());

        App.getInstance().getApiProxy().setUserAccount(user, (result) -> {
            this.device = result;
            updateDevice();
        });
    }
}