package kr.bobplanet.android;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.bobplanet.backend.bobplanetApi.model.Menu;
import kr.bobplanet.backend.bobplanetApi.model.Item;
import kr.bobplanet.backend.bobplanetApi.model.Submenu;

/**
 * Created by hkjinlee on 15. 9. 29..
 */
public class DailyViewAdapter extends BaseAdapter {
    private static final String TAG = DailyViewAdapter.class.getSimpleName();

    private Activity activity;
    private List<Menu> menuList;
    private ImageLoader imageLoader = MainApplication.getInstance().getImageLoader();

    public DailyViewAdapter(Activity activity) {
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return menuList == null ? 0 : menuList.size();
    }

    @Override
    public Object getItem(int position) {
        return menuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();

        Menu menu = menuList.get(position);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_daily_row, null);

        TextView when = (TextView) convertView.findViewById(R.id.when);
        int rid = 0;
        int color = 0;
        switch (menu.getWhen()) {
            case "08:00":
                rid = R.string.when_breakfast;
                color = R.color.when_breakfast_bg;
                break;
            case "12:00":
                rid = R.string.when_lunch;
                color = R.color.when_lunch_bg;
                break;
            case "18:00":
                rid = R.string.when_dinner;
                color = R.color.when_dinner_bg;
                break;
        }
        when.setText(new StringBuilder().append(activity.getResources().getString(rid)).append(
                menu.getType() == null ? "" : menu.getType()));
        when.setBackgroundColor(activity.getResources().getColor(color, activity.getTheme()));

        NetworkImageView iconimage = (NetworkImageView) convertView.findViewById(R.id.icon_image);
        iconimage.setImageUrl(menu.getItem().getIconURL(), imageLoader);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(menu.getItem().getId());

        RatingBar rating = (RatingBar) convertView.findViewById(R.id.rating);
        rating.setRating(menu.getItem().getAverageScore());
        LayerDrawable progress = (LayerDrawable) rating.getProgressDrawable();
        DrawableCompat.setTint(progress.getDrawable(2), Color.MAGENTA);

        TextView submenu = (TextView) convertView.findViewById(R.id.submenu);
        List<Submenu> submenus = menu.getSubmenu();
        if (submenus != null) {
            List<String> subs = new ArrayList<String>();
            for (Submenu sub : submenus) {
                subs.add(sub.getItem().getId());
            }
            submenu.setText(TextUtils.join(", ", subs));
        }

        TextView calories = (TextView) convertView.findViewById(R.id.calories);
        int cal = menu.getCalories();
        calories.setText(menu.getCalories() == 0 ? "" : new StringBuilder().append(cal).append(" KCal"));

        return convertView;
    }

    public void setMenuList(List<Menu> menuList) {
        this.menuList = menuList;
    }
}
