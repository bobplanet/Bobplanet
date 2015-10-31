package kr.bobplanet.android.ui;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import kr.bobplanet.android.App;
import kr.bobplanet.android.Constants;
import kr.bobplanet.android.EntityTranslator;
import kr.bobplanet.android.R;
import kr.bobplanet.android.VoteManager;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 * 메뉴 상세화면을 담당하는 activity.
 * <p>
 * - 메뉴 개요 탭(MenuFragment)과 세부평가 탭(MenuScoreFragment)의 2개 탭으로 구성.
 * - 메뉴정보는 intent에 통째로 넣어서 받는다. (푸쉬메시지 수신한 경우, extra에 넣어서 본 화면 호출해야 함)
 * - 메뉴에 점수를 매긴 경우 점수데이터를 다시 불러와야 하므로 ApiProxy.ApiResultListener 구현 필요
 * <p>
 * 로그인 여부에 따른 flow
 * - 로그인유저: 투표 dialog -> uploadVote()
 * - 비로그인유저: 투표 dialog -> 로그인 dialog -> requestGoogleSignIn() -> onEvent() -> uploadVote()
 * <p>
 * TODO 화면상단의 up arrow를 눌렀을 때 DayViewActivity의 마지막 fragment로 돌아가야 함
 *
 * @author heonkyu.jin
 * @version 2015. 10. 10
 */
public class MenuActivity extends BaseActivity implements Constants {
    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG = MenuActivity.class.getSimpleName();

    private final ImageLoader imageLoader = App.getImageLoader();

    private Menu menu;

    /**
     * 유저가 매긴 점수
     */
    private int myScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        menu = EntityTranslator.parseEntity(Menu.class, getIntent().getStringExtra(KEY_MENU));

        initLayout();
    }

    /**
     * 화면 초기화.
     */
    private void initLayout() {
        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        toolbar.setTitle(menu.getItem().getName());
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
        fab.setOnClickListener((View v) -> showVoteDialog());
    }

    /**
     * 점수주기 dialog 표시
     */
    private void showVoteDialog() {
        VoteManager voteManager = new VoteManager(this, menu);
        voteManager.showVoteDialog();
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
