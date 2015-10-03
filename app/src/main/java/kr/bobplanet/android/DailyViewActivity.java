package kr.bobplanet.android;

import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.commonsware.cwac.pager.PageDescriptor;
import com.commonsware.cwac.pager.SimplePageDescriptor;
import com.commonsware.cwac.pager.v4.ArrayPagerAdapter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;

public class DailyViewActivity extends AppCompatActivity implements AppConstants {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_daily_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
