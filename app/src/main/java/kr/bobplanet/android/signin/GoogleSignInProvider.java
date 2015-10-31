package kr.bobplanet.android.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import kr.bobplanet.android.App;
import kr.bobplanet.backend.bobplanetApi.model.Secret;
import kr.bobplanet.backend.bobplanetApi.model.User;

/**
 * 구글 OAuth 로그인을 제공하는 객체.
 *
 * @author heonkyu.jin
 * @version 15. 10. 31
 */
public class GoogleSignInProvider extends SignInProvider<Void> {
    private static final String TAG = GoogleSignInProvider.class.getSimpleName();

    /**
     *
     */
    private static int REQUEST_GOOGLE_SIGN_IN = 1;

    /**
     * Google Api client
     */
    private GoogleApiClient googleApiClient;

    /**
     *
     */
    private boolean isResolving = false;

    /**
     *
     */
    private boolean shouldResolve = false;

    protected GoogleSignInProvider(Context context) {
        super(context);
    }

    @Override
    public void requestSignIn(Activity activity, Secret secret) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        onSignInComplete(null);
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
                                connectionResult.startResolutionForResult(activity, REQUEST_GOOGLE_SIGN_IN);
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

        googleApiClient.connect();
    }

    @Override
    protected void onSignInComplete(Void v) {
        Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
        String accountName = Plus.AccountApi.getAccountName(googleApiClient);

        User user = new User()
                .setAccountType(SignInManager.ACCOUNT_GOOGLE)
                .setAccountId(person.getId())
                .setNickName(person.getDisplayName())
                //.setEmail(accountName)
                .setImage(person.getImage().getUrl());

        App.getInstance().getUserManager().registerUser(user);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GOOGLE_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != Activity.RESULT_OK) {
                shouldResolve = false;
            }

            isResolving = false;
            googleApiClient.connect();
        }
    }
}
