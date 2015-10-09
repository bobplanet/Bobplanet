package kr.bobplanet.android;

import android.content.Intent;
import android.content.IntentSender;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

/**
 * Bobplanet 앱에서 사용하는 모든 Activity들의 엄마클래스.
 * 
 * - Google OAuth 기능 사용을 위한 Google Api Client 관리
 * - 공용 옵션메뉴(ActionBar 오른쪽에 나오는) 관리
 *
 * @author heonkyu.jin
 * @version 2015. 10. 3
 */
public class ActivitySkeleton extends AppCompatActivity implements AppConstants,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = ActivitySkeleton.class.getSimpleName();

    private static final int REQUEST_SIGN_IN = 1;

    private GoogleApiClient googleApiClient;
    private boolean isResolving = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

        }

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
        //googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected: " + bundle);
        Person user = Plus.PeopleApi.getCurrentPerson(googleApiClient);
        MainApplication.getInstance().setCurrentUser(user);
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended: " + i);
    }

    @Override
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
            } else {

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
