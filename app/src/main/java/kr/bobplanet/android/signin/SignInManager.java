package kr.bobplanet.android.signin;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import kr.bobplanet.android.App;
import kr.bobplanet.android.Constants;
import kr.bobplanet.android.R;
import kr.bobplanet.android.event.InitCompleteEvent;
import kr.bobplanet.android.ui.BaseActivity;
import kr.bobplanet.android.ui.BaseDialogBuilder;
import kr.bobplanet.backend.bobplanetApi.model.Secret;

/**
 * 구글/페이스북/네이버 등 OAuth 로그인 기능을 제공하는 객체.
 *
 * @author heonkyu.jin
 * @version 15. 10. 30
 */
public class SignInManager implements Constants {
    private static final String TAG = SignInManager.class.getSimpleName();
    private Context context;

    private static final String DIALOG_LOGIN = "DIALOG_LOGIN";

    /**
     *
     */
    private Secret secret;

    /**
     *
     */
    public SignInManager(Context context) {
        this.context = context;
    }

    public void loadSecret() {
        App.getInstance().getApiProxy().getSecret((result) -> {
            if (result != null) {
                this.secret = result;

                EventBus.getDefault().post(new InitCompleteEvent(SignInManager.this.getClass()));
            }
        });
    }

    /**
     *
     * @param accountType
     * @return
     */
    public SignInProvider getSignInProvider(String accountType) {
        switch (accountType) {
            case ACCOUNT_GOOGLE:
                return new GoogleSignInProvider(context);
            case ACCOUNT_FACEBOOK:
                return new FacebookSignInProvider(context);
            case ACCOUNT_NAVER:
                return new NaverSignInProvider(context);
        }

        throw new RuntimeException("Unknown provider: " + accountType);
    }

    /**
     * 구글/페이스북 계정 등록 요청 dialog 표시
     */
    @DebugLog
    public void showSignInDialog(BaseActivity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.signin_dialog, null);

        Dialog signInDialog = new BaseDialogBuilder(activity, DIALOG_LOGIN)
                .setTitle(R.string.dialog_login_label)
                .setView(view)
                .setNegativeButton(R.string.button_cancel, null)
                .create();

        List<String> order = Arrays.asList(ACCOUNT_GOOGLE, ACCOUNT_FACEBOOK, ACCOUNT_NAVER);
        Collections.shuffle(order);

        Map<String, Integer> buttonDrawables = new HashMap<>();
        buttonDrawables.put(ACCOUNT_GOOGLE, R.drawable.google_signin);
        buttonDrawables.put(ACCOUNT_FACEBOOK, R.drawable.facebook_signin);
        buttonDrawables.put(ACCOUNT_NAVER, R.drawable.naver_signin);

        int[] buttonIds = { R.id.signin_button_1, R.id.signin_button_2, R.id.signin_button_3 };

        for (int i = 0; i < buttonIds.length; i++) {
            ImageButton button = (ImageButton) view.findViewById(buttonIds[i]);
            String accountType = order.get(i);
            button.setImageDrawable(context.getResources().getDrawable(buttonDrawables.get(accountType)));

            button.setOnClickListener(v -> {
                SignInProvider p = getSignInProvider(accountType);
                activity.setSignInProvider(p);
                p.requestSignIn(activity, secret);
                signInDialog.dismiss();
            });
        }

        signInDialog.show();
    }
}
