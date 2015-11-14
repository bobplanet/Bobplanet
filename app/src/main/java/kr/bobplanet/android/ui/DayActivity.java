package kr.bobplanet.android.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import com.commonsware.cwac.pager.v4.ArrayPagerAdapter;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import kr.bobplanet.android.App;
import kr.bobplanet.android.Constants;
import kr.bobplanet.android.Preferences;
import kr.bobplanet.android.R;
import kr.bobplanet.android.event.MorningMenuToggleEvent;
import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;
import kr.bobplanet.backend.bobplanetApi.model.Menu;
import kr.bobplanet.backend.bobplanetApi.model.UserDevice;

/**
 * 특정 일자의 아침-점심-저녁 메뉴리스트를 보여주는 Activity.
 * <p>
 * 실행 intent의 extra에 날짜가 지정되어있을 경우(가령, 푸쉬메시지를 통한 실행) 그 날짜를,
 * 없을 경우에는 오늘 날짜를 사용한다.
 * <p>
 * - Fragment와의 통신을 위해 EventBus 이용 (@see https://github.com/greenrobot/EventBus/)
 * - Fragment의 동적 추가를 위해 ArrayPagerAdapter 이용 (@see https://github.com/commonsguy/cwac-pager)
 * - ViewPager를 이용하여 DayViewFragment를 좌우 swipe로 넘겨볼 수 있음
 * - 체감속도 향상을 위해 DayViewFragment는 좌우 1개씩 미리 생성
 * - Fragment가 데이터 로딩을 끝내면 PagerAdapter에 추가
 */
public class DayActivity extends BaseActivity {
    private static final String TAG = DayActivity.class.getSimpleName();

    /**
     * 주간보기 모드인가?
     */
    private boolean isWeekViewMode = false;

    /**
     * 화면 왼쪽에서 나오는 Drawer의 레이아웃
     */
    private DrawerLayout drawerLayout;

    /**
     * Drawer를 켰다꺼는 토글버튼
     */
    private ActionBarDrawerToggle drawerToggle;

    /**
     * Fragment 관리용 Adapter
     */
    private DayPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.day_activity);

        // Toolbar 표시 & 버튼 노출
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Drawer 표시
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.app_name, R.string.app_name);
        drawerLayout.setDrawerListener(drawerToggle);

        // Drawer 설정
        NetworkImageView profileView = (NetworkImageView) findViewById(R.id.profile_image);
        if (App.getUserManager().hasAccount()) {
            profileView.setImageUrl(App.getUserManager().getUserImage(), App.getImageLoader());
        }

        // DayFragment가 보내주는 데이터로딩완료 메시지 수신을 위해 EventBus 등록
        EventBus.getDefault().register(this);

        // intent에 날짜가 있으면 그 날짜, 없으면 오늘 날짜 이용
        Date start_date;
        try {
            String date = getIntent().getStringExtra(Constants.KEY_DATE);
            start_date = DATEFORMAT_YMD.parse(date);
        } catch (Exception e) {
            start_date = new Date();
        }

        List<PageDescriptor> descriptors = Collections.singletonList(
                newPageDescriptor(DATEFORMAT_YMD.format(start_date))
        );
        adapter = new DayPagerAdapter(getSupportFragmentManager(), descriptors);

        ViewPager pager = (ViewPager) findViewById(R.id.view_pager);
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                String title = adapter.getPageTitle(position);
                getSupportActionBar().setTitle(title);
            }
        });
        pager.setAdapter(adapter);

        // 처음 사용하는 사람들을 위해 좌우스와이프 안내메시지 노출
        showSwipeNotice();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_day, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_morning_toggle:
                UserDevice device = App.getUserManager().getDevice();

                boolean morningEnabled = device.getMorningMenuEnabled();
                device.setMorningMenuEnabled(!morningEnabled);
                App.getUserManager().updateDevice();

                int messageId = !morningEnabled ? R.string.morning_menu_active : R.string.morning_menu_inactive;
                Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show();

                EventBus.getDefault().post(new MorningMenuToggleEvent(!morningEnabled));
                return true;

/*
            case R.id.action_dayweek_toggle:
                isWeekViewMode = !isWeekViewMode;
                item.setIcon(isWeekViewMode ? R.drawable.ic_week_view : R.drawable.ic_day_view);
                return true;
*/
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Override하지 않으면 Drawer가 열린 상태에서 back 눌렀을 때 Activity가 종료됨
     */
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Fragment의 tag와 타이틀 지정
     * - tag: "2015-11-07" 형태
     * - title: "11월 7일(금)" 형태
     */
    private PageDescriptor newPageDescriptor(String date) {
        String pageTitle = date;
        try {
            pageTitle = DATEFORMAT_MDE.format(DATEFORMAT_YMD.parse(date));
        } catch (Exception e) {
        }

        return new SimplePageDescriptor(date, pageTitle);
    }

    /**
     * DayFragment가 데이터 로딩을 끝냈을 때 호출.
     * 식당이 매일 문열지는 않으므로, 이 때까지는 전날-다음날 메뉴가 있는지 없는지만 알 수 있음.
     * 메뉴가 있을 경우 PagerAdapter에 추가해서 swipe scroll이 가능하게 함
     */
    @SuppressWarnings("unused")
    public void onEvent(DayFragment.DataLoadCompleteEvent e) {
        DailyMenu d = e.getDailyMenu();
        Log.v(TAG, "Data load complete: " + d.toString());

        // 주의사항: FragmentManager를 이용해서 fragment 존재여부 체크하면 안됨.
        // 이 단계에서는 adapter에만 추가되어있어, FM에서는 해당 fragment를 모르는 상태임.
        if (d.getPreviousDate() != null && !isExistingPage(d.getPreviousDate())) {
            Log.i(TAG, "Creating fragment of the previous day: " + d.getPreviousDate());
            adapter.insert(newPageDescriptor(d.getPreviousDate()), 0);
        }
        if (d.getNextDate() != null && !isExistingPage(d.getNextDate())) {
            Log.i(TAG, "Creating fragment of the next day: " + d.getNextDate());
            adapter.add(newPageDescriptor(d.getNextDate()));
        }
    }

    /**
     * 이미 해당날짜의 Fragment가 있는지 확인한다.
     * 10월 5일 화면을 띄우는 순간 10월 4일과 6일 화면을 미리 만들어두는데,
     * 10월 4일 화면으로 이동하면 10월 3일(이건 괜찮음)과 5일(이건 안됨) 화면을 또 만들려고 하기 때문.
     */
    private boolean isExistingPage(String date) {
        int page_count = adapter.getCount();
        for (int i = 0; i < page_count; i++) {
            DayFragment fragment = adapter.getExistingFragment(i);
            if (fragment != null && fragment.getTag().equals(date)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 식단메뉴 상세페이지로 이동.
     * 식단메뉴가 클릭되었을 때 EventBus에 의해 호출됨.
     * <p>
     * - 관련 View가 ViewPager 밑의 fragment 밑의 RecyclerView 밑에 있어서 그냥 EventBus로 간단히 구현함.
     * - startActivity()는 메인스레드(=UI스레드)에서 실행되어야 하는 것으로 보임.
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(DayViewHolder.ViewClickEvent e) {
        startMenuViewActivity(e.viewHolder);
    }

    /**
     * TODO Activity 전환 transition 효과 복구.
     */
    @DebugLog
    private void startMenuViewActivity(DayViewHolder viewHolder) {
        Menu menu = viewHolder.menu;

        Intent intent = new Intent(this, MenuActivity.class);
        intent.putExtra(KEY_MENU, menu.toString());

        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                new Pair<>(viewHolder.image, viewHolder.image.getTransitionName())/*,
                new Pair<View, String>(viewHolder.name, EXTRA_MENU_TITLE)*/);

        ActivityCompat.startActivity(this, intent, options.toBundle());
    }

    /**
     * 좌우로 swipe하면 다른 날짜 메뉴도 볼 수 있음을 Snackbar로 알려줌
     */
    private void showSwipeNotice() {
        final Preferences prefs = App.getPreferences();

        if (prefs.hasDismissedSwipeNotice()) {
            return;
        }

        CoordinatorLayout layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        Snackbar.make(layout, R.string.swipe_notice, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.swipe_notice_goaway, (v) -> prefs.setDismissedSwipeNotice())
                .show();
    }

    /**
     * DayViewFragment를 동적으로 추가하기 위해 ArrayPagerAdapter를 이용
     *
     * @see {https://github.com/commonsguy/cwac-pager}
     */
    private class DayPagerAdapter extends ArrayPagerAdapter<DayFragment> {
        public DayPagerAdapter(FragmentManager fragmentManager, List<PageDescriptor> descriptors) {
            super(fragmentManager, descriptors);
        }

        @Override
        protected DayFragment createFragment(PageDescriptor pageDescriptor) {
            return DayFragment.newInstance(pageDescriptor.getFragmentTag());
        }
    }

}
