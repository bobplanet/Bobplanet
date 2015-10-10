package kr.bobplanet.android;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import com.commonsware.cwac.pager.v4.ArrayPagerAdapter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;

/**
 * 특정 일자의 아침-점심-저녁 메뉴리스트를 보여주는 Activity.
 *
 * 실행 intent의 extra에 날짜가 지정되어있을 경우(가령, 푸쉬메시지를 통한 실행) 그 날짜를,
 * 없을 경우에는 오늘 날짜를 사용한다.
 * 
 * - Fragment와의 통신을 위해 EventBus 이용 (@see https://github.com/greenrobot/EventBus/)
 * - Fragment의 동적 추가를 위해 ArrayPagerAdapter 이용 (@see https://github.com/commonsguy/cwac-pager)
 * - ViewPager를 이용하여 DayViewFragment를 좌우 swipe로 넘겨볼 수 있음
 * - 체감속도 향상을 위해 DayViewFragment는 좌우 1개씩 미리 생성
 * - Fragment가 데이터 로딩을 끝내면 PagerAdapter에 추가
 */
public class DayViewActivity extends ActivitySkeleton {
    private static final String TAG = DayViewActivity.class.getSimpleName();
    private static final String FRAGMENT_TAG_PREFIX = "DayViewFragment-";

	/**
	 * 좌우 swipe를 위해 사용하는 ViewPager
	 */
    private ViewPager pager;
    private DayPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_view);

		// DailyViewFragment가 보내주는 데이터로딩완료 메시지 수신을 위해 EventBus 등록
        EventBus.getDefault().register(this);

		// intent에 날짜가 있으면 그 날짜, 없으면 오늘 날짜 이용
        Date start_date;
        try {
            String date = getIntent().getStringExtra(AppConstants.DATE_ARGUMENT);
            start_date = DATEFORMAT_YMD.parse(date);
        } catch (Exception e) {
            start_date = new Date();
        }

        List<PageDescriptor> descriptors = Arrays.asList((PageDescriptor)
                newPageDescriptor(DATEFORMAT_YMD.format(start_date))
        );
        adapter = new DayPagerAdapter(getSupportFragmentManager(), descriptors);

        pager = (ViewPager) findViewById(R.id.daily_view_pager);
        pager.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

	/**
	 * Fragment의 tag로는 FRAGMENT_TAG_PREFIX + 날짜를 지정 
	 */
    private PageDescriptor newPageDescriptor(String date) {
        return new SimplePageDescriptor(FRAGMENT_TAG_PREFIX + date, date);
    }

	/**
	 * DailyViewFragment가 데이터 로딩을 끝냈을 때 호출.
	 * 식당이 매일 문열지는 않으므로, 이 때까지는 전날-다음날 메뉴가 있는지 없는지만 알 수 있음.
	 * 메뉴가 있을 경우 PagerAdapter에 추가해서 swipe scroll이 가능하게 함
	 */
    @SuppressWarnings("unused")
    public void onEvent(DayViewFragment.DataLoadCompleteEvent e) {
        DailyMenu d = e.getDailyMenu();
        Log.d(TAG, "Data load complete: " + d.toString());

        Log.d(TAG, "START fragments # = " + adapter.getCount());

        if (d.getPreviousDate() != null && !isExistingPage(d.getPreviousDate())) {
            Log.i(TAG, "Creating fragment of the previous day: " + d.getPreviousDate());
            adapter.insert(newPageDescriptor(d.getPreviousDate()), 0);
        }
        if (d.getNextDate() != null && !isExistingPage(d.getNextDate())) {
            Log.i(TAG, "Creating fragment of the next day: " + d.getNextDate());
            adapter.add(newPageDescriptor(d.getNextDate()));
        }

        Log.d(TAG, "END fragments # = " + adapter.getCount());
    }

    private boolean isExistingPage(String date) {
        int page_count = adapter.getCount();
        for (int i = 0; i < page_count; i++) {
            String title = adapter.getPageTitle(i);
            if (title.equals(date)) {
                return true;
            }
        }
        return false;
    }
	/**
	 * DayViewFragment를 동적으로 추가하기 위해 ArrayPagerAdapter를 이용
	 * 
	 * @see {https://github.com/commonsguy/cwac-pager}
	 */
    private class DayPagerAdapter extends ArrayPagerAdapter<DayViewFragment> {
        public DayPagerAdapter(FragmentManager fragmentManager, List<PageDescriptor> descriptors) {
            super(fragmentManager, descriptors);
        }

        @Override
        protected DayViewFragment createFragment(PageDescriptor pageDescriptor) {
            return DayViewFragment.newInstance(pageDescriptor.getTitle());
        }
    }

}