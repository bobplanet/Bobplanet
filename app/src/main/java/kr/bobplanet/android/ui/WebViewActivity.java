package kr.bobplanet.android.ui;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.animation.AccelerateInterpolator;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.ButterKnife;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;
import kr.bobplanet.android.App;
import kr.bobplanet.android.R;
import kr.bobplanet.android.UserManager;

/**
 * 앱 안에서 WebView를 띄울 때 기본으로 사용되어야 하는 Activity.
 *
 * - 앱 -> 웹으로 동선이 넘어가는 경우에도 GA상에서 동일한 사용자로 인식되도록 함.
 *   - bobp_userId를 웹페이지에 request parameter로 전달
 *   - 웹페이지에서는 해당 parameter가 있을 경우 이를 쿠키에 저장
 *   - 다른 페이지로 넘어가더라도 쿠키에서 userId를 복원
 *
 * @author heonkyu.jin
 * @version 15. 10. 27
 */
public class WebViewActivity extends BaseActivity {
    private static final String TAG = WebViewActivity.class.getSimpleName();

    /**
     * 네트웤에서 데이터를 가져올 때 동작하는 ProgressBar
     */
    ProgressBar progressBar;

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressBar = ButterKnife.findById(this, R.id.progress_bar);
        Drawable d = new SmoothProgressDrawable.Builder(this)
                .interpolator(new AccelerateInterpolator()).build();
        d.setColorFilter(ContextCompat.getColor(this, R.color.progress),
                android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setIndeterminateDrawable(d);

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
        UserManager userManager = App.getUserManager();

        StringBuilder sb = new StringBuilder()
                .append(uri.getScheme()).append("://")
                .append(uri.getHost())
                .append(uri.getPort() != 80 && uri.getPort() > 0 ? ":" + uri.getPort() : "")
                .append(uri.getPath())
                .append("?type=webview&")
                .append(TextUtils.isEmpty(uri.getEncodedQuery()) ? "" : uri.getEncodedQuery())
                .append(userManager.hasAccount() ?
                        String.format("&%s=%s", WEBVIEW_USERID, userManager.getUserId()) : "")
                .append(TextUtils.isEmpty(uri.getEncodedFragment()) ? "" : uri.getEncodedFragment());

        String url = sb.toString();
        Log.i(TAG, "url = " + url);
        return url;
    }

    private class DefaultWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setIndeterminate(false);
        }
    }
}
