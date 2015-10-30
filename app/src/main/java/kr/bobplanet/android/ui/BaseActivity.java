package kr.bobplanet.android.ui;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;

import java.util.Arrays;
import java.util.UUID;

import hugo.weaving.DebugLog;
import kr.bobplanet.android.App;
import kr.bobplanet.android.Constants;
import kr.bobplanet.android.R;
import kr.bobplanet.android.event.ScreenLogEvent;
import kr.bobplanet.backend.bobplanetApi.model.Secret;
import kr.bobplanet.backend.bobplanetApi.model.User;

/**
 * Bobplanet 앱에서 사용하는 모든 Activity들의 엄마클래스.
 * <p>
 * - onResume()에서 이벤트 측정
 * - Google OAuth 기능 사용을 위한 Google Api Client 관리
 * - 공용 옵션메뉴(ActionBar 오른쪽에 나오는) 관리
 *
 * @author heonkyu.jin
 * @version 2015. 10. 3
 */
abstract public class BaseActivity extends AppCompatActivity implements Constants {
    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        super.onActivityResult(requestCode, resultCode, data);

        App.getInstance().getSignInManager().onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 화면이 시작될 때마다 트래킹 정보를 서버로 전송
     */
    @Override
    protected void onResume() {
        super.onResume();
        ScreenLogEvent.activityView(this);
    }

    /**
     * 스낵바 display.
     *
     * @param message
     */
    public void showSnackbar(String message) {
        CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!(this instanceof EmptyOptionsMenu)) {
            getMenuInflater().inflate(R.menu.menu_common, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!(this instanceof EmptyOptionsMenu)) {
            int id = item.getItemId();

            if (id == R.id.action_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }

            return super.onOptionsItemSelected(item);
        } else {
            return false;
        }
    }
}
