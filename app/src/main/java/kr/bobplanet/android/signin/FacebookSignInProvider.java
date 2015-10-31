package kr.bobplanet.android.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Arrays;

import kr.bobplanet.android.App;
import kr.bobplanet.backend.bobplanetApi.model.Secret;
import kr.bobplanet.backend.bobplanetApi.model.User;

/**
 *
 *
 * @author heonkyu.jin
 * @version 15. 10. 31
 */
public class FacebookSignInProvider extends SignInProvider<Profile> {
    private static final String TAG = FacebookSignInProvider.class.getSimpleName();

    private static final String[] FACEBOOK_PERMISSIONS = {"public_profile", "email"};
    private CallbackManager facebookCallbackManager;
    private ProfileTracker facebookProfileTracker;

    protected FacebookSignInProvider(Context context) {
        super(context);
    }

    @Override
    public void requestSignIn(Activity activity, Secret secret) {
        FacebookSdk.sdkInitialize(context);
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
                onSignInComplete(currentProfile);
            }
        };

        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(activity, Arrays.asList(FACEBOOK_PERMISSIONS));
    }

    @Override
    protected void onSignInComplete(Profile profile) {
        Log.d(TAG, "facebook = " + profile.toString());

        User user = new User()
                .setAccountType(SignInManager.ACCOUNT_FACEBOOK)
                .setAccountId(profile.getId())
                .setNickName(profile.getName())
                .setImage(profile.getProfilePictureUri(200, 200).toString());

        App.getInstance().getUserManager().registerUser(user);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        if (facebookProfileTracker != null) {
            facebookProfileTracker.stopTracking();
        }
    }
}
