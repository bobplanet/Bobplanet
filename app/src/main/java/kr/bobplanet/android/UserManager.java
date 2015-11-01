package kr.bobplanet.android;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.facebook.Profile;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.plus.model.people.Person;

import java.util.UUID;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import kr.bobplanet.android.event.UserSignInEvent;
import kr.bobplanet.android.gcm.GcmEvent;
import kr.bobplanet.android.gcm.GcmServices;
import kr.bobplanet.backend.bobplanetApi.model.User;
import kr.bobplanet.backend.bobplanetApi.model.UserDevice;

/**
 * 기기 및 사용자 identity의 persistence를 관리하는 객체.
 * <p>
 * - 기기정보는 항상 존재하고, 사용자정보는 로그인한 경우에만 존재함
 * - 앱이 실행되면 가장 먼저 기기번호(UUID)를 직접 생성한 뒤 서버에 등록 요청
 *   - 동일한 androidId의 기기가 이미 존재할 경우 서버가 새로운 UUID를 내려줄 수 있음
 * - 기기정보 등록이 완료되면 서버에 GCM토큰 업로드하고 preference에 UUID + GCM토큰 저장
 * - SignInManager에 의해 OAuth 계정이 등록되면 이를 서버/preference에 저장
 *   - 이미 동일한 계정이 존재할 경우 서버는 Device까지 그 밑에 붙여서 내려줄 수 있음
 *   - 저장이 끝나면 UserSignInEvent를 발생시켜 후처리 진행
 *
 * @author heonkyu.jin
 * @version 15. 10. 18
 */
public class UserManager implements ApiProxy.ApiResultListener<UserDevice> {
    private static final String TAG = UserManager.class.getSimpleName();

    private Context context;

    /**
     * 현재 사용자. 로그인하지 않은 사용자는 UseDevice만 있고, User는 없음
     */
    private UserDevice device;

    private Preferences prefs;

    /**
     * @param context
     * @param prefs
     */
    public UserManager(Context context, Preferences prefs) {
        this.context = context;
        this.prefs = prefs;

        EventBus.getDefault().register(this);

        loadDevice();
    }

    /**
     * 사용자정보를 읽어온다. 없으면 서버에 등록요청.
     */
    protected void loadDevice() {
        UserDevice loaded = prefs.loadDevice();
        if (loaded == null) {
            Log.i(TAG, "Device doesn't exists. Creating new device");
            this.device = createUserDevice();

            App.getApiProxy().registerDevice(device, this);
        } else {
            Log.i(TAG, "Device restored from prefs: " + loaded);
            this.device = loaded;

            if (device.getGcmToken() == null) {
                requestGcmToken();
            }
        }
    }

    /**
     * 서버에 등록할 사용자 기기정보를 생성
     */
    private UserDevice createUserDevice() {
        UserDevice device = new UserDevice()
                .setId(UUID.randomUUID().toString())
                .setLunchPushEnabled(true)
                .setDinnerPushEnabled(true)
                .setAndroidId(
                        Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID))
                .setIid(InstanceID.getInstance(context).getId());

        return device;
    }

    /**
     * 사용자 기기정보가 서버에 등록되었을 때 호출되는 callback
     */
    @Override
    public void onApiResult(UserDevice result) {
        if (result != null) {
            Log.d(TAG, "Device registered");

            this.device.setId(result.getId()).setUser(result.getUser());

            requestGcmToken();
        } else {
            Log.w(TAG, "registerDevice's result is NULL");
        }
    }

    private void requestGcmToken() {
        context.startService(new Intent(context, GcmServices.Registration.class));
    }

    /**
     * 사용자번호 조회. 아무나 호출해도 됨.
     *
     * @return
     */
    public String getUserId() {
        return device.getUser().getId();
    }

    /**
     *
     */
    public String getUserName() {
        return device.getUser() != null ? device.getUser().getNickName() : null;
    }

    /**
     *
     */
    public String getUserImage() {
        return device.getUser() != null ? device.getUser().getImage() : null;
    }

    /**
     * 구글이나 페이스북 계정이 등록된 사용자인지 확인. VoteManager가 호출함.
     *
     * @return
     */
    public boolean hasAccount() {
        return device.getUser() != null && device.getUser().getAccountId() != null;
    }

    /**
     * 현재 이 앱을 사용중인 Device의 정보를 알려줌.
     * SettingsActivity에서 기기/사용자정보를 화면에 뿌려주기 위해 호출함.
     */
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
                Log.i(TAG, "gcm register succeeded");

                device.setGcmToken(event.getToken());
                updateDevice();
                break;
            case GcmEvent.REGISTER_FAILURE:
                Log.i(TAG, "gcm register failed");
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
            App.getApiProxy().updateDevice(device);
            prefs.storeDevice(device);
        }
    }

    /**
     * ID 로그인이 끝났을 때 사용자정보를 서버 및 preferences에 저장
     *
     * @param user
     */
    @DebugLog
    public void registerUser(User user) {
        device.setUser(user.setId(UUID.randomUUID().toString()));

        App.getApiProxy().registerUser(device, result -> {
            if (result != null) {
                this.device = result;
                prefs.storeDevice(device);

                EventBus.getDefault().post(new UserSignInEvent(user.getAccountType()));
            }
        });
    }
}