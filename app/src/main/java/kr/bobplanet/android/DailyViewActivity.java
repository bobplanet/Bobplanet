package kr.bobplanet.android;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ProgressBar;

import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import com.commonsware.cwac.pager.v4.ArrayPagerAdapter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;
import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;

public class DailyViewActivity extends ActivitySkeleton {
    private static final String TAG = DailyViewActivity.class.getSimpleName();
    private static final String FRAGMENT_TAG_PREFIX = "DailyViewFragment-";

    private ViewPager pager;
    private DailyPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_view);

        EventBus.getDefault().register(this);

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
        adapter = new DailyPagerAdapter(getSupportFragmentManager(), descriptors);

        pager = (ViewPager) findViewById(R.id.daily_view_pager);
        pager.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private PageDescriptor newPageDescriptor(String date) {
        return new SimplePageDescriptor(FRAGMENT_TAG_PREFIX + date, date);
    }

    @SuppressWarnings("unused")
    public void onEvent(DailyViewFragment.DataLoadCompleteEvent e) {
        DailyMenu d = e.getDailyMenu();
        Log.d(TAG, "Data load complete: " + d.toString());

        FragmentManager fm = getSupportFragmentManager();

        if (d.getPreviousDate() != null &&
                fm.findFragmentByTag(FRAGMENT_TAG_PREFIX + d.getPreviousDate()) == null) {
            Log.i(TAG, "Creating fragment of the previous day: " + d.getPreviousDate());
            adapter.insert(newPageDescriptor(d.getPreviousDate()), 0);
        }
        if (d.getNextDate() != null &&
            fm.findFragmentByTag(FRAGMENT_TAG_PREFIX + d.getNextDate()) == null) {
            Log.i(TAG, "Creating fragment of the next day: " + d.getNextDate());
            adapter.add(newPageDescriptor(d.getNextDate()));
        }
    }

    private class DailyPagerAdapter extends ArrayPagerAdapter<DailyViewFragment> {
        public DailyPagerAdapter(FragmentManager fragmentManager, List<PageDescriptor> descriptors) {
            super(fragmentManager, descriptors);
        }

        @Override
        protected DailyViewFragment createFragment(PageDescriptor pageDescriptor) {
            return DailyViewFragment.newInstance(pageDescriptor.getTitle());
        }
    }

}
