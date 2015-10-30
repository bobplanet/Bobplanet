package kr.bobplanet.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import hugo.weaving.DebugLog;
import kr.bobplanet.backend.bobplanetApi.model.Secret;
import kr.bobplanet.backend.bobplanetApi.model.User;

/**
 * @author heonkyu.jin
 * @version 15. 10. 30
 */
public class SignInManager implements Constants {
    private static final String TAG = SignInManager.class.getSimpleName();

    private Context applicationContext;

    private GoogleApiClient googleApiClient;
    private boolean isResolving = false;
    private boolean shouldResolve = false;

    private static final String[] FACEBOOK_PERMISSIONS = {"public_profile", "email"};
    private CallbackManager facebookCallbackManager;
    private ProfileTracker facebookProfileTracker;

    private static final String NAVER_PROFILE_API_URL = "https://openapi.naver.com/v1/nid/getUserProfile.xml";
    private OAuthLogin naverLogin;

    protected SignInManager(Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     *
     */
    protected void initAccountSignInEnv(Secret secret) {
        initGoogleSignInEnv();
        initFacebookSignInEnv(secret);
        initNaverSignInEnv(secret);
    }

    protected void onDestroy() {
        if (facebookProfileTracker != null) {
            facebookProfileTracker.stopTracking();
        }
    }

    /**
     * 구글 로그인을 위한 환경 초기화
     */
    private void initGoogleSignInEnv() {
        this.googleApiClient = new GoogleApiClient.Builder(applicationContext)
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
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();
    }

    /**
     * 구글 로그인이 필요할 때 자식 activity가 호출.
     */
    public void requestGoogleSignIn(Activity activity) {
        googleApiClient.registerConnectionFailedListener(connectionResult -> {
            if (!isResolving) {
                if (connectionResult.hasResolution()) {
                    try {
                        connectionResult.startResolutionForResult(activity, REQUEST_GOOGLE_SIGN_IN);
                        isResolving = true;
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "error", e);
                        isResolving = false;
                        googleApiClient.connect();
                    }
                }
            }
        });

        googleApiClient.connect();
    }

    /**
     * 구글 로그인이 완료되면 GoogleApiClient에 의해 호출되는 callback.
     */
    @DebugLog
    public void onGoogleSignInComplete() {
        Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
        String accountName = Plus.AccountApi.getAccountName(googleApiClient);

        User user = new User()
                .setAccountType(ACCOUNT_GOOGLE)
                .setAccountId(person.getId())
                .setNickName(person.getDisplayName())
                .setEmail(accountName)
                .setImage(person.getImage().getUrl());

        App.getInstance().getUserManager().registerUser(user);
    }

    /**
     * Facebook 로그인 환경 초기화.
     */
    private void initFacebookSignInEnv(Secret secret) {
        FacebookSdk.sdkInitialize(applicationContext);
        FacebookSdk.setApplicationId(secret.getFacebookAppId());

        this.facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                (object, response) -> Log.d(TAG, response.toString())
                        );
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
    public void requestFacebookSignIn(Activity activity) {
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(activity, Arrays.asList(FACEBOOK_PERMISSIONS));
    }

    /**
     * Facebook 로그인 완료처리
     */
    private void onFacebookSignInComplete(Profile profile) {
        Log.d(TAG, "facebook = " + profile.toString());

        User user = new User()
                .setAccountType(ACCOUNT_FACEBOOK)
                .setAccountId(profile.getId())
                .setNickName(profile.getName())
                .setImage(profile.getProfilePictureUri(200, 200).toString());

        App.getInstance().getUserManager().registerUser(user);
    }

    /**
     *
     */
    private void initNaverSignInEnv(Secret secret) {
        naverLogin = OAuthLogin.getInstance();
        naverLogin.init(applicationContext,
                secret.getNaverClientId(),
                secret.getNaverClientSecret(),
                "Bobplanet");
        Log.d(TAG, "naverLogin = " + naverLogin);
    }

    /**
     *
     */
    public void requestNaverSignIn(final Activity activity) {
        Log.d(TAG, "naverLogin = " + naverLogin);

        naverLogin.startOauthLoginActivity(activity, new NaverOAuthLoginHandler(this, activity));
    }

    /**
     *
     */
    private static class NaverOAuthLoginHandler extends OAuthLoginHandler {
        WeakReference<SignInManager> signInManagerRef;
        WeakReference<Activity> activityRef;

        private NaverOAuthLoginHandler(SignInManager signInManager, Activity activity) {
            this.signInManagerRef = new WeakReference<SignInManager>(signInManager);
            this.activityRef = new WeakReference<Activity>(activity);
        }

        @Override
        public void run(boolean success) {
            SignInManager signInManager = signInManagerRef.get();
            OAuthLogin naverLogin = signInManager.naverLogin;
            Activity activity = activityRef.get();

            if (success) {
                String accessToken = naverLogin.getAccessToken(activity);
                String refreshToken = naverLogin.getRefreshToken(activity);

                StringRequest profileRequest = new StringRequest(NAVER_PROFILE_API_URL,
                        (String result) -> signInManager.onNaverSignInComplete(result),
                        (error) -> Log.w(TAG, "Naver login error")) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + accessToken);
                        return headers;
                    }
                };
                App.getInstance().addToRequestQueue(profileRequest);
            } else {
                Log.w(TAG, "Naver SignIn failed:" + naverLogin.getLastErrorDesc(activity));
            }
        }
    }

    /**
     *
     * @param response
     */
    private void onNaverSignInComplete(String response) {
        Log.d(TAG, "response = " + response);

        User user = new User().setAccountType(ACCOUNT_NAVER);

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(response));

            int eventType = parser.getEventType();
            String currentTag = "";
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String tag = parser.getName();
                        switch (tag) {
                            case "enc_id":
                            case "email":
                            case "nickname":
                            case "profile_image":
                                currentTag = tag;
                                break;
                            default:
                                currentTag = "";
                        }
                        break;
                    case XmlPullParser.TEXT:
                        String text = parser.getText();
                        switch (currentTag) {
                            case "enc_id":
                                user.setAccountId(text);
                                break;
                            case "nickname":
                                user.setNickName(text);
                                break;
                            case "email":
                                user.setEmail(text);
                                break;
                            case "profile_image":
                                user.setImage(text);
                                break;
                        }
                }
                eventType = parser.next();
            }

            App.getInstance().getUserManager().registerUser(user);
        } catch (Exception e) {
            Log.w(TAG, "Naver login XML parsing error: ", e);
        }
    }

    /**
     *
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);

        if (requestCode == REQUEST_GOOGLE_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != Activity.RESULT_OK) {
                shouldResolve = false;
            }

            isResolving = false;
            googleApiClient.connect();
        } else {
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}
