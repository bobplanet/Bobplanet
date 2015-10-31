package kr.bobplanet.android.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import kr.bobplanet.android.App;
import kr.bobplanet.android.Constants;
import kr.bobplanet.backend.bobplanetApi.model.Secret;

/**
 *
 *
 * @author heonkyu.jin
 * @version 15. 10. 31.
 * @param <T>
 */
abstract public class SignInProvider<T> implements Constants {
    protected Context context;

    protected SignInProvider(Context context) {
        this.context = context;
    }

    abstract public void requestSignIn(Activity activity, Secret secret);
    abstract protected void onSignInComplete(T result);

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    protected void onDestroy() {
    }
}
