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
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import kr.bobplanet.android.App;
import kr.bobplanet.backend.bobplanetApi.model.Secret;
import kr.bobplanet.backend.bobplanetApi.model.User;

/**
 * 페이스북 OAuth 로그인 기능을 제공하는 객체.
 *
 * @author heonkyu.jin
 * @version 15. 10. 31
 */
public class FacebookSignInProvider extends SignInProvider<JSONObject> {
    private static final String TAG = FacebookSignInProvider.class.getSimpleName();

    private static final String[] FACEBOOK_PERMISSIONS = {"public_profile", "email"};
    private CallbackManager callbackManager;

    protected FacebookSignInProvider(Context context) {
        super(context);
    }

    @Override
    public void requestSignIn(Activity activity, Secret secret) {
        FacebookSdk.sdkInitialize(context);
        FacebookSdk.setApplicationId(secret.getFacebookAppId());

        this.callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                (json, response) -> onSignInComplete(json)
                        );
                        Bundle params = new Bundle();
                        params.putString("fields", "id,name,email,link,picture");
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


        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(activity, Arrays.asList(FACEBOOK_PERMISSIONS));
    }

    @Override
    protected void onSignInComplete(JSONObject json) {
        try {
            User user = new User()
                    .setAccountType(SignInManager.ACCOUNT_FACEBOOK)
                    .setAccountId(json.getString("id"))
                    .setNickName(json.getString("name"))
                    .setEmail(json.getString("email"))
                    .setImage(json.getJSONObject("picture").getJSONObject("data").getString("url"));

            App.getUserManager().registerUser(user);
        } catch (JSONException e) {
            Log.w(TAG, "Facebook login error", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
