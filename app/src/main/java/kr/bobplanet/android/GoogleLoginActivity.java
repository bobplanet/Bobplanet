package kr.bobplanet.android;

import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;

import hugo.weaving.DebugLog;
import kr.bobplanet.android.event.UserLogEvent;

/**
 * Bobplanet 앱에서 사용하는 모든 Activity들의 엄마클래스.
 * 
 * - onResume()에서 이벤트 측정
 * - Google OAuth 기능 사용을 위한 Google Api Client 관리
 * - 공용 옵션메뉴(ActionBar 오른쪽에 나오는) 관리
 *
 * @author heonkyu.jin
 * @version 2015. 10. 18
 */
public class GoogleLoginActivity extends AppCompatActivity implements Constants/*,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener*/ {
    private static final String TAG = GoogleLoginActivity.class.getSimpleName();
/*
    protected static final int REQUEST_SIGN_IN = 1;

    private GoogleApiClient googleApiClient;
    private boolean isResolving = false;

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

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    @DebugLog
    public void onConnected(Bundle bundle) {
        Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);

        App.getInstance().updateUserGoogleAccount(person);

        setResult(RESULT_OK);
        finish();
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
    }*/
}
