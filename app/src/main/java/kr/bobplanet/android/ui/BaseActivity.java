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

import java.util.Arrays;
import java.util.UUID;

import hugo.weaving.DebugLog;
import kr.bobplanet.android.App;
import kr.bobplanet.android.Constants;
import kr.bobplanet.android.R;
import kr.bobplanet.android.event.ScreenLogEvent;
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

    private static final int REQUEST_GOOGLE_SIGN_IN = 1;

    private GoogleApiClient googleApiClient;
    private boolean isResolving = false;
    private boolean shouldResolve = false;

    private static final String[] FACEBOOK_PERMISSIONS = { "public_profile", "email" };
    private CallbackManager facebookCallbackManager;
    private ProfileTracker facebookProfileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initGoogleSignInEnv();
        initFacebookSignInEnv();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        facebookProfileTracker.stopTracking();
    }

    /**
     * 구글 로그인을 위한 환경 초기화
     */
    private void initGoogleSignInEnv() {
        this.googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        onGoogleSignInComplete();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.w(TAG, "onConnectionSuspended: " + i);
                    }
                })
                .addOnConnectionFailedListener(connectionResult -> {
                    if (!isResolving) {
                        if (connectionResult.hasResolution()) {
                            try {
                                connectionResult.startResolutionForResult(this, REQUEST_GOOGLE_SIGN_IN);
                                isResolving = true;
                            } catch (IntentSender.SendIntentException e) {
                                Log.e(TAG, "error", e);
                                isResolving = false;
                                googleApiClient.connect();
                            }
                        }
                    }
                })
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
    }

    /**
     * 구글 로그인이 필요할 때 자식 activity가 호출.
     */
    protected void requestGoogleSignIn() {
        googleApiClient.connect();
    }

    /**
     * 구글 로그인이 완료되면 GoogleApiClient에 의해 호출되는 callback.
     * 로그인을 요청한 Activity에게 알리기 위해 Eventbus 이벤트를 쏴준다.
     *
     */
    @DebugLog
    public void onGoogleSignInComplete() {
        Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
        User user = new User()
                .setAccountType("Google")
                .setAccountId(person.getId())
                .setNickName(person.getDisplayName())
                .setImage(person.getImage().getUrl());

        App.getInstance().getUserManager().registerUser(ACCOUNT_GOOGLE, user);
    }

    /**
     * Facebook 로그인 환경 초기화.
     */
    private void initFacebookSignInEnv() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        this.facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                (object, response) -> { Log.d(TAG, response.toString()); });
                        Bundle params = new Bundle();
                        params.putString("fields", "id,name,email");
                        request.setParameters(params);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Log.i(TAG, "Facebook login cancelled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.i(TAG, "Facebook login error: " + exception);
                    }
                });

        this.facebookProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                onFacebookSignInComplete(currentProfile);
            }
        };
    }

    /**
     * Facebook 로그인 요청
     */
    protected void requestFacebookSignin() {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(this, Arrays.asList(FACEBOOK_PERMISSIONS));
    }

    /**
     * Facebook 로그인 완료처리
     *
     */
    private void onFacebookSignInComplete(Profile profile) {
        User user = new User()
                .setAccountType("Facebook")
                .setAccountId(profile.getId())
                .setNickName(profile.getName())
                .setImage(profile.getProfilePictureUri(200, 200).toString());

        App.getInstance().getUserManager().registerUser(ACCOUNT_FACEBOOK, user);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == REQUEST_GOOGLE_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                shouldResolve = false;
            }

            isResolving = false;
            googleApiClient.connect();
        } else {
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
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
    protected void showSnackbar(String message) {
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
