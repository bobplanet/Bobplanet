package kr.bobplanet.android;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import kr.bobplanet.backend.bobplanetApi.model.Daily;

/**
 * Created by hkjinlee on 15. 9. 29..
 */
public class DailyListAdapter extends BaseAdapter {
    private Activity activity;
    private List<Daily> dailies;
    private ImageLoader imageLoader = MainApplication.getInstance().getImageLoader();

    public DailyListAdapter(Activity activity) {
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
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_daily_row, null);

        NetworkImageView icon = (NetworkImageView) convertView.findViewById(R.id.icon);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView sub = (TextView) convertView.findViewById(R.id.sub);
        TextView calories = (TextView) convertView.findViewById(R.id.calories);

        Daily daily = dailies.get(position);

        icon.setImageUrl(daily.getMenu().getIconURL(), imageLoader);
        name.setText(daily.getName());
        sub.setText(daily.getDate());
        calories.setText(String.valueOf(daily.getCalories()));

        return convertView;
    }

    public void setDailies(List<Daily> dailyList) {
        this.dailies = dailyList;
    }
}
