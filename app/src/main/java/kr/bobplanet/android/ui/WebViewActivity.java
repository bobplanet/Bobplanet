package kr.bobplanet.android.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import kr.bobplanet.android.App;
import kr.bobplanet.android.R;
import kr.bobplanet.android.UserManager;

/**
 * 앱 안에서 WebView를 띄울 때 기본으로 사용되어야 하는 Activity.
 *
 * - 앱 -> 웹으로 동선이 넘어가는 경우에도 GA상에서 동일한 사용자로 인식되도록 함.
 *
 * @author heonkyu.jin
 * @version 15. 10. 27
 */
public class WebViewActivity extends BaseActivity {
    private static final String TAG = WebViewActivity.class.getSimpleName();
    private WebView webView;
    private UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);

        userManager = App.getUserManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new DefaultWebViewClient());
        webView.loadUrl(modifyUrl(getIntent().getData()));
    }

    /**
     * WebView에 띄울 URI에 사용자인식 패러미터를 더해준다.
     *
     * @param uri
     * @return
     */
    private String modifyUrl(Uri uri) {
        String userId = userManager.getUserId();
        if (!TextUtils.isEmpty(userId)) {
            StringBuilder sb = new StringBuilder()
                    .append(uri.getScheme()).append("://")
                    .append(uri.getHost())
                    .append(uri.getPort() != 80 && uri.getPort() > 0 ? ":" + uri.getPort() : "")
                    .append(uri.getPath())
                    .append(TextUtils.isEmpty(uri.getEncodedQuery()) ? "?" :
                            uri.getEncodedQuery() + "&")
                    .append(WEBVIEW_USERID + '=').append(userManager.getUserId())
                    .append(TextUtils.isEmpty(uri.getEncodedFragment()) ? "" : uri.getEncodedFragment());

            String url = sb.toString();
            Log.i(TAG, "url = " + url);
            return url;
        } else {
            return uri.toString();
        }
    }

    static class DefaultWebViewClient extends WebViewClient {

    }
}
