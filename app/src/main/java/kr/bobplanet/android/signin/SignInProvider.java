package kr.bobplanet.android.signin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import kr.bobplanet.android.App;
import kr.bobplanet.android.Constants;
import kr.bobplanet.backend.bobplanetApi.model.Secret;

/**
 * OAuth 로그인 기능을 제공하는 provider의 뼈대 객체.
 *
 * - 모든 OAuth provider가 Activity를 이용해서 로그인을 처리하므로 onActivityResult override 필요
 * - 본 클래스는 Activity가 아니므로, onActivityResult 자체는 BaseActivity에서 받고, 본 클래스로 delegate
 * - OAuth provider에 따라 API response의 type이 다르므로 Generic 이용.
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

    /**
     * 로그인 요청. SignInManager에서 호출함.
     */
    abstract public void requestSignIn(Activity activity, Secret secret);

    /**
     * 로그인 완료. 마지막에 UserManager.registerUser() 호출해서 회원정보에 저장해주어야 함.
     */
    abstract protected void onSignInComplete(T result);

    /**
     * BaseActivity.onActivityResult()로부터 delegate.
     * 구글/페이스북 로그인에서 이용함.
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
