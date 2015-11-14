package kr.bobplanet.android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import kr.bobplanet.android.Constants;
import kr.bobplanet.android.R;
import kr.bobplanet.android.log.ScreenLogEvent;
import kr.bobplanet.android.signin.SignInProvider;

/**
 * Bobplanet 앱에서 사용하는 모든 Activity들의 엄마클래스.
 * <p>
 * - 로그인 처리를 위해 SignInProvider를 이용 (사실은 얘가 delegate임)
 * - onResume()에서 Activity 구동이벤트 측정
 * - 공용 옵션메뉴(ActionBar 오른쪽에 나오는) 관리
 * - 스낵바에 메시지를 출력하는 간단한 helper method 제공
 *
 * @author heonkyu.jin
 * @version 2015. 10. 3
 */
abstract public class BaseActivity extends AppCompatActivity implements Constants {
    private static final String TAG = BaseActivity.class.getSimpleName();

    /**
     * OAuth 로그인 처리를 담당하는 provider.
     */
    private SignInProvider signInProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * OAuth delegate 지정. SignInManager가 호출해줌.
     *
     * @param signInProvider
     */
    public void setSignInProvider(SignInProvider signInProvider) {
        this.signInProvider = signInProvider;
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        super.onActivityResult(requestCode, resultCode, data);

        signInProvider.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 화면이 시작될 때마다 트래킹 정보를 서버로 전송
     */
    @Override
    protected void onResume() {
        super.onResume();
        ScreenLogEvent.activityView(this);
    }

    /**
     * 스낵바 display.
     *
     * @param message
     */
    public void showSnackbar(String message) {
        CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show();
    }

    public void showSnackbar(@StringRes int messageId) {
        showSnackbar(getString(messageId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!(this instanceof EmptyOptionsMenu)) {
            getMenuInflater().inflate(R.menu.menu_common, menu);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!(this instanceof EmptyOptionsMenu)) {
            int id = item.getItemId();

            if (id == R.id.action_settings) {
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            }

            return super.onOptionsItemSelected(item);
        } else {
            return false;
        }
    }
}
