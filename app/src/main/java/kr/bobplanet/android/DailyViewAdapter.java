package kr.bobplanet.android;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import kr.bobplanet.backend.bobplanetApi.model.Daily;
import kr.bobplanet.backend.bobplanetApi.model.DailySub;

/**
 * Created by hkjinlee on 15. 9. 29..
 */
public class DailyViewAdapter extends BaseAdapter {
    private Activity activity;
    private List<Daily> dailies;
    private ImageLoader imageLoader = MainApplication.getInstance().getImageLoader();

    public DailyViewAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return dailies == null ? 0 : dailies.size();
    }

    @Override
    public Object getItem(int position) {
        return dailies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        Daily daily = dailies.get(position);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_daily_row, null);

        NetworkImageView icon = (NetworkImageView) convertView.findViewById(R.id.icon);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView submenu = (TextView) convertView.findViewById(R.id.submenu);
        TextView calories = (TextView) convertView.findViewById(R.id.calories);

        icon.setImageUrl(daily.getMenu().getIconURL(), imageLoader);
        title.setText(daily.getMenu().getId());
        List<String> subs = new ArrayList<String>();
        for (DailySub sub : daily.getSubList()) {
            subs.add(sub.getMenu().getId());
        }
        submenu.setText(TextUtils.join(", ", subs));
        calories.setText(new StringBuilder().append(daily.getCalories()).append("KCal"));

        return convertView;
    }

    public void setDailies(List<Daily> dailyList) {
        this.dailies = dailyList;
    }
}
