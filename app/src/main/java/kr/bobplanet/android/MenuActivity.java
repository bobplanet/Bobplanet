package kr.bobplanet.android;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 * 메뉴 상세화면을 담당하는 activity.
 *
 * - 메뉴 개요 탭(MenuFragment)과 세부평가 탭(MenuScoreFragment)의 2개 탭으로 구성.
 * - 메뉴정보는 intent에 통째로 넣어서 받는다. (푸쉬메시지 수신한 경우, extra에 넣어서 본 화면 호출해야 함)
 *
 * TODO 화면상단의 up arrow를 눌렀을 때 DayViewActivity의 마지막 fragment로 돌아가야 함
 *
 * @author heonkyu.jin
 * @version 2015. 10. 10
 */
public class MenuActivity extends BaseActivity implements AppConstants {
    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG = MenuActivity.class.getSimpleName();

    private final ImageLoader imageLoader = MainApplication.getInstance().getImageLoader();

    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        EntityVault entityVault = MainApplication.getInstance().getEntityVault();
        menu = entityVault.parseEntity(Menu.class, getIntent().getStringExtra(KEY_MENU));

        Toolbar toolbar = ButterKnife.findById(this, R.id.toolbar);
        toolbar.setTitle(menu.getItem().getId());
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        CollapsingToolbarLayout toolbar_layout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbar_layout.setTitleEnabled(false);

        NetworkImageView iconView = ButterKnife.findById(this, R.id.image);
        Log.d(TAG, "image = " + menu.getItem().getImage());
        iconView.setImageUrl(menu.getItem().getImage(), imageLoader);

        ViewPager viewPager = ButterKnife.findById(this, R.id.view_pager);
        PagerAdapter adapter = setupViewPagerAdapter();
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
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
