package kr.bobplanet.android.ui;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import hugo.weaving.DebugLog;
import kr.bobplanet.android.App;
import kr.bobplanet.android.util.DeviceEnvironment;
import kr.bobplanet.android.R;
import kr.bobplanet.android.event.InitCompleteEvent;
import kr.bobplanet.android.signin.SignInManager;

/**
 * 본 애플리케이션의 메인 액티비티.
 * Splashscreen을 띄운다거나, 한번만 실행하면 되는 초기화 로직을 실행하는 등의 용도로 만들었다.
 * <p/>
 * - GCM 등록로직 수행 (등록결과를 받아오기 위해 EventBus를 이용함)
 * -
 * - 본 앱의 기본화면인 DayViewActivity를 실행
 *
 * @author heonkyu.jin
 * @version 2015. 9. 30
 */
public class StartActivity extends BaseActivity {
    private static final String TAG = StartActivity.class.getSimpleName();

    private static final Class[] INIT_COMPONENTS = { SignInManager.class };

    /**
     *
     */
    private List<Class> initComponents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity);

        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setBackgroundResource(R.drawable.pacman_animation);

        AnimationDrawable frameAnimation = (AnimationDrawable) imageView.getBackground();
        frameAnimation.start();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Google Play Services가 있는지 확인. 없을 경우 종료.
        if (!DeviceEnvironment.checkPlayServices(this)) {
            Toast.makeText(this, R.string.need_google_play_service, Toast.LENGTH_LONG).show();
            finish();
        }

        initComponents.addAll(Arrays.asList(INIT_COMPONENTS));

        App.getUserManager().loadDevice();
        App.getSignInManager().loadSecret();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     *
     * @param event
     */
    @Subscribe
    @DebugLog
    @SuppressWarnings("unused")
    public void onEvent(InitCompleteEvent event) {
        if (initComponents.contains(event.component)) {
            initComponents.remove(event.component);
        }
        Log.d(TAG, "size = " + initComponents.size());

        if (initComponents.size() == 0) {
            startActivity(new Intent(this, DayActivity.class));
            finish();
        }
    }
}
