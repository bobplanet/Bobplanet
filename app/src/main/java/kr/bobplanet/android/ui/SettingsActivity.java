package kr.bobplanet.android.ui;


import android.annotation.TargetApi;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import hugo.weaving.DebugLog;
import kr.bobplanet.android.App;
import kr.bobplanet.android.R;
import kr.bobplanet.android.UserManager;
import kr.bobplanet.android.event.UserAccountEvent;
import kr.bobplanet.android.signin.SignInManager;
import kr.bobplanet.backend.bobplanetApi.model.UserDevice;

/**
 * 설정화면 Activity.
 * 안드로이드 버전이 ICS(api level 14)일 경우 SwitchPreference를, 아닌 경우 CheckBoxPreference를 사용한다.
 * (이를 위해 xml-v14/와 xml/ 아래에 각각 하나씩의 pref_settings.xml이 존재)
 *
 * - 회원탈퇴 이벤트 수신을 위해 EventBus 이용.
 *
 * @author heonkyu.jin
 * @version 2015. 10. 24.
 */
public class SettingsActivity extends BaseActivity {
    private static final String TAG = SettingsActivity.class.getSimpleName();

    private static final String PREF_MORNING_DISPLAY = "MORNING_DISPLAY";
    private static final String PREF_LUNCH_PUSH = "LUNCH_PUSH";
    private static final String PREF_DINNER_PUSH = "DINNER_PUSH";
    private static final String PREF_APP_VERSION = "APP_VERSION";
    private static final String PREF_DEVICE_ID = "DEVICE_ID";
    private static final String PREF_USER_ID = "USER_ID";
    private static final String PREF_USER_SIGN_INOUT = "USER_SIGN_INOUT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        initToolbar();

        getFragmentManager().beginTransaction()
                .replace(R.id.empty, new SettingsFragment())
                .commit();
    }

    /**
     * 실제 설정화면 역할을 담당하는 Fragment
     */
    public static class SettingsFragment extends PreferenceFragment {
        private static final String TAG = SettingsFragment.class.getSimpleName();
        private TwoStatePreferenceHandler prefHandler;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            prefHandler = TwoStatePreferenceHandler.getInstance(getPreferenceManager());
            addPreferencesFromResource(R.xml.pref_settings);

            EventBus.getDefault().register(this);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            EventBus.getDefault().unregister(this);
        }

        /**
         * 화면 초기화
         */
        @Override
        public void onResume() {
            super.onResume();
            Log.v(TAG, "onResume()");
			
            UserDevice device = App.getUserManager().getDevice();
            prefHandler.setChecked(PREF_MORNING_DISPLAY, device.getMorningMenuEnabled());
            prefHandler.setChecked(PREF_LUNCH_PUSH, device.getLunchPushEnabled());
            prefHandler.setChecked(PREF_DINNER_PUSH, device.getDinnerPushEnabled());

            try {
                PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(
                        getActivity().getPackageName(), 0);
                findPreference(PREF_APP_VERSION).setSummary(
                        String.format("%d (%s)", packageInfo.versionCode, packageInfo.versionName));
            } catch (PackageManager.NameNotFoundException e) {
                Log.w(TAG, "Package name not found", e);
            }
            findPreference(PREF_DEVICE_ID).setSummary(device.getId());

            updateSignInOutPreferences();
        }

        private void updateSignInOutPreferences() {
            UserDevice device = App.getUserManager().getDevice();

            findPreference(PREF_USER_ID).setSummary(device.getUser() != null ?
                    device.getUser().getId() : getString(R.string.settings_app_user_null));

            boolean hasAccount = App.getUserManager().hasAccount();
            findPreference(PREF_USER_SIGN_INOUT).setTitle(hasAccount ?
                    R.string.settings_app_user_sign_out :
                    R.string.settings_app_user_sign_in);
            findPreference(PREF_USER_SIGN_INOUT).setSummary(hasAccount ?
                    R.string.settings_app_user_sign_out_summary :
                    R.string.settings_app_user_sign_in_summary);
        }

        /**
         * 설정항목을 클릭할 때 호출되는 callback.
         * 대부분의 항목이 실제로 Preferences에 저장되지 않으므로 웬만하면 true 반환
         *
         * @param screen
         * @param pref
         * @return true when click event was consumed inside this method
         */
        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen screen, Preference pref) {
            UserManager userManager = App.getUserManager();
            UserDevice device = userManager.getDevice();

            switch (pref.getKey()) {
                case PREF_MORNING_DISPLAY:
                    device.setMorningMenuEnabled(prefHandler.isChecked(pref.getKey()));
                    userManager.updateDevice();
                    return true;
                case PREF_LUNCH_PUSH:
                    device.setLunchPushEnabled(prefHandler.isChecked(pref.getKey()));
                    userManager.updateDevice();
                    return true;
                case PREF_DINNER_PUSH:
                    device.setDinnerPushEnabled(prefHandler.isChecked(pref.getKey()));
                    userManager.updateDevice();
                    return true;
                case PREF_USER_SIGN_INOUT:
                    BaseActivity activity = (BaseActivity) getActivity();
                    SignInManager signInManager = new SignInManager(activity);
                    if (userManager.hasAccount()) {
                        signInManager.showSignOutDialog(activity);
                    } else {
                        signInManager.showSignInDialog(activity);
                    }
                    return true;
            }

            return super.onPreferenceTreeClick(screen, pref);
        }

        @SuppressWarnings("unused")
        @DebugLog
        public void onEvent(UserAccountEvent event) {
            BaseActivity activity = (BaseActivity) getActivity();

            if (event instanceof UserAccountEvent.SignIn) {
                activity.showSnackbar(R.string.sign_in_completed);
            } else if (event instanceof UserAccountEvent.SignOut) {
                activity.showSnackbar(R.string.sign_out_completed);
            }

            updateSignInOutPreferences();
        }
    }

    /**
     * Android 버전에 따라 CheckBoxPreference와 SwitchPreference를 적절히 사용.
     */
    private static class TwoStatePreferenceHandler {
        static TwoStatePreferenceHandler getInstance(PreferenceManager prefsManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                return new TwoStatePreferenceHandler() {
                    @Override
                    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                    boolean isChecked(String prefKey) {
                        return ((SwitchPreference) prefsManager.findPreference(prefKey)).isChecked();
                    }

                    @Override
                    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                    void setChecked(String prefKey, boolean checked) {
                        ((SwitchPreference) prefsManager.findPreference(prefKey)).setChecked(checked);
                    }
                };
            } else {
                return new TwoStatePreferenceHandler() {
                    @Override
                    boolean isChecked(String prefKey) {
                        return ((CheckBoxPreference) prefsManager.findPreference(prefKey)).isChecked();
                    }

                    @Override
                    void setChecked(String prefKey, boolean checked) {
                        ((CheckBoxPreference) prefsManager.findPreference(prefKey)).setChecked(checked);
                    }
                };
            }
        }

        boolean isChecked(String prefKey) { return false; }
        void setChecked(String prefKey, boolean checked) {}
    }
}
