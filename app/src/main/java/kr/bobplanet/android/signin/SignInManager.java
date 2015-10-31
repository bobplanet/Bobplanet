package kr.bobplanet.android.signin;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
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
import kr.bobplanet.android.event.UserLogEvent;
import kr.bobplanet.android.ui.BaseActivity;
import kr.bobplanet.android.ui.BaseDialogBuilder;
import kr.bobplanet.backend.bobplanetApi.model.Secret;

/**
 * 구글/페이스북/네이버 등 OAuth 로그인 기능을 제공하는 객체.
 *
 * - 구글은 패키지 이름과 인증서 fingerprint로 클라이언트를 식별하지만 페이스북과 네이버의 경우는 별도 인증정보 필요
 * - 인증정보를 소스 내에 둘 수는 없으니, Bobplanet 서버로부터 받아옴 (이게 Secret)
 * - Secret 로딩이 끝나면 Eventbus를 통해 InitCompleteEvent 전파: StartActivity에서 받아 처리함
 * - 계정 노출순서는 random으로 지정하여 노출순서가 계정 선택에 영향을 주지 않도록 함
 * - 선택한 계정 종류는 View에 tag으로 저장
 * - onActivityResult() 처리를 위해 BaseActivity.setSignInProvider() 호출해서 delegate 전달.
 *
 * @author heonkyu.jin
 * @version 15. 10. 30
 */
public class SignInManager implements Constants {
    private static final String TAG = SignInManager.class.getSimpleName();
    private Context context;

    private static final String DIALOG_LOGIN = "DIALOG_LOGIN";

    /**
     * 페이스북, 네이버의 OAuth 인증을 위해 필요한 ID, secret 등이 저장된 객체
     */
    private Secret secret;

    private Button positiveButton;

    /**
     *
     */
    public SignInManager(Context context) {
        this.context = context;
    }

    /**
     * 서버로부터 Secret을 받아온 뒤 StartActivity에 노티.
     */
    public void loadSecret() {
        App.getApiProxy().getSecret((result) -> {
            if (result != null) {
                this.secret = result;

                EventBus.getDefault().post(new InitCompleteEvent(SignInManager.this.getClass()));
            }
        });
    }

    /**
     * 약식 팩토리.
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
        View view = createDialogView(activity);
        Dialog signInDialog = createDialog(activity, view);
        signInDialog.show();
    }

    /**
     * Dialog의 본체를 구성할 View 생성.
     *
     * @param context
     * @return
     */
    private View createDialogView(Context context) {
        final View view = LayoutInflater.from(context).inflate(R.layout.signin_dialog, null);

        final List<String> accountTypes = Arrays.asList(ACCOUNT_GOOGLE, ACCOUNT_FACEBOOK, ACCOUNT_NAVER);
        Collections.shuffle(accountTypes);

        Map<String, Integer> buttonDrawables = new HashMap<>();
        buttonDrawables.put(ACCOUNT_GOOGLE, R.drawable.google_signin);
        buttonDrawables.put(ACCOUNT_FACEBOOK, R.drawable.facebook_signin);
        buttonDrawables.put(ACCOUNT_NAVER, R.drawable.naver_signin);

        int[] buttonIds = { R.id.signin_button_1, R.id.signin_button_2, R.id.signin_button_3 };
        ImageButton[] buttons = new ImageButton[buttonIds.length];

        for (int i = 0; i < buttonIds.length; i++) {
            buttons[i] = (ImageButton) view.findViewById(buttonIds[i]);
            String accountType = accountTypes.get(i);
            ImageButton button = (ImageButton) view.findViewById(buttonIds[i]);

            button.setTag(new Tag(accountType, i + 1));
            button.setImageDrawable(context.getResources().getDrawable(buttonDrawables.get(accountType)));

            button.setOnClickListener(v -> {
                positiveButton.setEnabled(true);

                for (int j = 0; j < buttonIds.length; j++) {
                    buttons[j].setSelected(false);
                }
                button.setSelected(true);

                view.setTag(button.getTag());
            });
        }

        return view;
    }

    /**
     * Dialog 생성
     *
     * @param activity
     * @param view
     * @return
     */
    private Dialog createDialog(BaseActivity activity, View view) {
        AlertDialog signInDialog = new BaseDialogBuilder(activity, DIALOG_LOGIN)
                .setTitle(R.string.dialog_login_label)
                .setView(view)
                .setPositiveButton(R.string.button_ok, (dialog, which) -> {
                    Tag tag = (Tag) view.getTag();
                    UserLogEvent.accountSelect(tag.accountType, tag.displayOrder);

                    SignInProvider provider = getSignInProvider(tag.accountType);
                    activity.setSignInProvider(provider);
                    provider.requestSignIn(activity, secret);
                })
                .setNegativeButton(R.string.button_cancel, null)
                .create();

        // positiveButton은 실제 화면에 노출된 이후에 생성되므로 OnShowListener에서 처리.
        signInDialog.setOnShowListener(dialog -> {
            this.positiveButton = signInDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setEnabled(false);
        });

        return signInDialog;
    }

    /**
     * 계정 선택 버튼의 위치와 계정 종류를 저장하는 tag 객체
     */
    private class Tag {
        String accountType;
        int displayOrder;

        private Tag(String accountType, int displayOrder) {
            this.accountType = accountType;
            this.displayOrder = displayOrder;
        }
    }
}
