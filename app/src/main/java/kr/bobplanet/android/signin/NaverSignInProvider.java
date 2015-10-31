package kr.bobplanet.android.signin;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.toolbox.StringRequest;
import com.nhn.android.naverlogin.OAuthLogin;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import kr.bobplanet.android.App;
import kr.bobplanet.backend.bobplanetApi.model.Secret;
import kr.bobplanet.backend.bobplanetApi.model.User;

/**
 *
 *
 * @author heonkyu.jin
 * @version 15. 10. 31
 */
class NaverSignInProvider extends SignInProvider<String> {
    private static final String TAG = NaverSignInProvider.class.getSimpleName();

    private static final String NAVER_PROFILE_API_URL = "https://openapi.naver.com/v1/nid/getUserProfile.xml";

    private OAuthLogin oAuthLogin;

    protected NaverSignInProvider(Context context) {
        super(context);
    }

    @Override
    public void requestSignIn(Activity activity, Secret secret) {
        oAuthLogin = OAuthLogin.getInstance();
        oAuthLogin.init(context,
                secret.getNaverClientId(),
                secret.getNaverClientSecret(),
                "Bobplanet");
        oAuthLogin.startOauthLoginActivity(activity, new OAuthLoginHandler(this, activity));
    }

    @Override
    protected void onSignInComplete(String response) {
        User user = new User().setAccountType(SignInManager.ACCOUNT_NAVER);

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
                                //user.setEmail(text);
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
     */
    private static class OAuthLoginHandler extends com.nhn.android.naverlogin.OAuthLoginHandler {
        WeakReference<NaverSignInProvider> providerRef;
        WeakReference<Activity> activityRef;

        private OAuthLoginHandler(NaverSignInProvider provider, Activity activity) {
            this.providerRef = new WeakReference<NaverSignInProvider>(provider);
            this.activityRef = new WeakReference<Activity>(activity);
        }

        @Override
        public void run(boolean success) {
            NaverSignInProvider provider = providerRef.get();
            OAuthLogin naverLogin = provider.oAuthLogin;
            Activity activity = activityRef.get();

            if (success) {
                String accessToken = naverLogin.getAccessToken(activity);
                String refreshToken = naverLogin.getRefreshToken(activity);

                StringRequest profileRequest = new StringRequest(NAVER_PROFILE_API_URL,
                        result -> provider.onSignInComplete(result),
                        error -> Log.w(TAG, "Naver login error")) {
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
}
