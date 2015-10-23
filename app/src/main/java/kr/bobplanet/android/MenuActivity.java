package kr.bobplanet.android;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RatingBar;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import kr.bobplanet.android.event.GoogleSigninEvent;
import kr.bobplanet.android.event.UserLogEvent;
import kr.bobplanet.backend.bobplanetApi.model.Item;
import kr.bobplanet.backend.bobplanetApi.model.Menu;
import kr.bobplanet.backend.bobplanetApi.model.Vote;

/**
 * 메뉴 상세화면을 담당하는 activity.
 * <p>
 * - 메뉴 개요 탭(MenuFragment)과 세부평가 탭(MenuScoreFragment)의 2개 탭으로 구성.
 * - 메뉴정보는 intent에 통째로 넣어서 받는다. (푸쉬메시지 수신한 경우, extra에 넣어서 본 화면 호출해야 함)
 * - 메뉴에 점수를 매긴 경우 점수데이터를 다시 불러와야 하므로 ApiProxy.ApiResultListener 구현 필요
 * <p>
 * 로그인 여부에 따른 flow
 * - 로그인유저: 투표 dialog -> uploadVote()
 * - 비로그인유저: 투표 dialog -> 로그인 dialog -> requestGoogleSignin() -> onEvent() -> uploadVote()
 * <p>
 * TODO 화면상단의 up arrow를 눌렀을 때 DayViewActivity의 마지막 fragment로 돌아가야 함
 *
 * @author heonkyu.jin
 * @version 2015. 10. 10
 */
public class MenuActivity extends BaseActivity implements Constants {
    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG = MenuActivity.class.getSimpleName();

    private final ImageLoader imageLoader = App.getInstance().getImageLoader();

    private Menu menu;

    /**
     * 유저가 매긴 점수
     */
    private int myScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        menu = EntityParser.parseEntity(Menu.class, getIntent().getStringExtra(KEY_MENU));

        EventBus.getDefault().register(this);

        initLayout();

        requestMyScore();
    }

    /**
     * 화면 초기화.
     */
    private void initLayout() {
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        toolbar.setTitle(menu.getItem().getId());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CollapsingToolbarLayout toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbar_layout.setTitleEnabled(false);

        NetworkImageView iconView = ButterKnife.findById(this, R.id.image);
        iconView.setImageUrl(menu.getItem().getImage(), imageLoader);

        ViewPager viewPager = ButterKnife.findById(this, R.id.view_pager);
        PagerAdapter adapter = setupViewPagerAdapter();
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = ButterKnife.findById(this, R.id.fab);
        fab.setOnClickListener((View v) -> showRatingDialog());
    }

    /**
     * 유저가 과거에 매긴 점수 가져옴.
     */
    private void requestMyScore() {
        UserManager um = App.getInstance().getUserManager();
        if (um.hasAccount()) {
            App.getInstance().getApiProxy().myVote(um.getUserId(), menu,
                    (Vote result) -> {
                        if (result != null) {
                            myScore = result.getScore();
                        }
                    }
            );
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 점수주기 dialog 표시
     */
    private void showRatingDialog() {
        View ratingBarHolder = getLayoutInflater().inflate(R.layout.menu_rating_dialog, null);
        final RatingBar ratingBar = ButterKnife.findById(ratingBarHolder, R.id.rating);
        ratingBar.setRating(myScore);

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_rating_label)
                .setView(ratingBarHolder)
                .setPositiveButton(R.string.button_ok,
                        (DialogInterface dialog, int which) -> {
                            myScore = (int) ratingBar.getRating();

                            if (App.getInstance().getUserManager().hasAccount()) {
                                uploadVote();
                            } else {
                                showSigninDialog();
                            }
                        }
                )
                .setNegativeButton(R.string.button_cancel, null).show();

        UserLogEvent.dialogView(getString(R.string.dialog_rating_label));
    }

    /**
     * 평가결과를 서버로 전송.
     */
    @DebugLog
    private void uploadVote() {
        if (myScore > 0) {
            ApiProxy proxy = App.getInstance().getApiProxy();
            proxy.vote(App.getInstance().getUserManager().getUserId(), menu, myScore,
                    (Item result) -> MenuActivity.this.menu.setItem(result));

            Resources r = getResources();
            String[] levels = r.getStringArray(R.array.rating_level);
            String message = String.format(r.getString(R.string.rating_notice_fmt),
                    menu.getItem().getId(),
                    Util.endsWithConsonant(menu.getItem().getId()) ? "을" : "를",
                    levels[myScore - 1],
                    Util.endsWithConsonant(levels[myScore - 1]) ? "이" : ""
            );
            CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
            Snackbar.make(layout, message, Snackbar.LENGTH_LONG).show();
        }
    }

    /**
     * 구글/페이스북 계정 등록 요청 dialog 표시
     */
    @DebugLog
    private void showSigninDialog() {
        View view = getLayoutInflater().inflate(R.layout.menu_login_dialog, null);

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_login_label)
                .setView(view)
                .setPositiveButton(R.string.button_ok, (dialog, which) -> requestGoogleSignin())
                .setNegativeButton(R.string.button_cancel, null).show();

        UserLogEvent.dialogView(getString(R.string.dialog_login_label));
    }

    /**
     * 구글 로그인이 완료되었을 때 호출되는 callback.
     *
     * @param event
     */
    @DebugLog
    public void onEvent(GoogleSigninEvent event) {
        uploadVote();
    }

    /**
     * 탭 구현용 activity를 관리할 ViewPagerAdapter 생성.
     *
     * @return
     */
    private PagerAdapter setupViewPagerAdapter() {
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager());

        // fragment 두개 더해줌
        adapter.addFragment(new MenuFragment(), getString(R.string.tab_menu_label));
        adapter.addFragment(new MenuScoreFragment(), getString(R.string.tab_menu_score_label));

        return adapter;
    }

    /**
     * 탭 아래쪽의 화면을 표현하는 fragment를 관리하는 PagerAdapter.
     */
    public class TabFragmentPagerAdapter extends FragmentPagerAdapter {
        private final List<Pair<Fragment, String>> fragmentList = new ArrayList<>();

        public TabFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position).first;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentList.get(position).second;
        }

        private void addFragment(Fragment fragment, String title) {
            fragmentList.add(new Pair(fragment, title));
        }
    }
}
