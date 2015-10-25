package kr.bobplanet.android.ui;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import kr.bobplanet.android.App;
import kr.bobplanet.android.Constants;
import kr.bobplanet.android.R;
import kr.bobplanet.android.event.GoogleSigninEvent;
import kr.bobplanet.android.event.UserLogEvent;

/**
 * Bobplanet 앱에서 사용하는 모든 Activity들의 엄마클래스.
 * <p/>
 * - onResume()에서 이벤트 측정
 * - Google OAuth 기능 사용을 위한 Google Api Client 관리
 * - 공용 옵션메뉴(ActionBar 오른쪽에 나오는) 관리
 *
 * @author heonkyu.jin
 * @version 2015. 10. 3
 */
abstract public class BaseActivity extends AppCompatActivity implements Constants,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private static final int REQUEST_SIGN_IN = 1;

    private GoogleApiClient googleApiClient;
    private boolean isResolving = false;
    private boolean shouldResolve = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
    }

    protected void showSnackbar(String message) {
        CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * 구글 로그인이 필요할 때 자식 activity가 호출.
     *
     */
    protected void requestGoogleSignin() {
        googleApiClient.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == REQUEST_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                shouldResolve = false;
            }

            isResolving = false;
            googleApiClient.connect();
        }
    }

    /**
     * 구글 로그인이 완료되면 GoogleApiClient에 의해 호출되는 callback.
     * 로그인을 요청한 Activity에게 알리기 위해 Eventbus 이벤트를 쏴준다.
	 *
     * @param bundle
     */
    @Override
    @DebugLog
    public void onConnected(Bundle bundle) {
        Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);

        App.getInstance().getUserManager().updateUserGoogleAccount(person);

        EventBus.getDefault().post(new GoogleSigninEvent());
    }

    /**
     * 화면이 시작될 때마다 트래킹 정보를 서버로 전송
     */
    @Override
    protected void onResume() {
        super.onResume();
        UserLogEvent.activityView(this).submit();
    }

    @Override
    @DebugLog
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended: " + i);
    }

    @Override
    @DebugLog
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: " + connectionResult);

        if (!isResolving) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, REQUEST_SIGN_IN);
                    isResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "error", e);
                    isResolving = false;
                    googleApiClient.connect();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
