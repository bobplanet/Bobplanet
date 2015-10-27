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
 *
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

        userManager = App.getInstance().getUserManager();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setWebViewClient(new DefaultWebViewClient());
        webView.loadUrl(modifyUrl(getIntent().getData()));
    }

    private String modifyUrl(Uri uri) {
        StringBuilder sb = new StringBuilder()
                .append(uri.getScheme()).append("://")
                .append(uri.getHost())
                .append(uri.getPort() != 80 && uri.getPort() > 0 ? ":" + uri.getPort() : "")
                .append(uri.getPath())
                .append(TextUtils.isEmpty(uri.getEncodedQuery()) ? "?" :
                        uri.getEncodedQuery() + "&")
                .append("userId=").append(userManager.getUserId())
                .append(TextUtils.isEmpty(uri.getEncodedFragment()) ? "" : uri.getEncodedFragment());

        String url = sb.toString();
        Log.i(TAG, "url = " + url);

        return url;
    }

    static class DefaultWebViewClient extends WebViewClient {

    }
}
