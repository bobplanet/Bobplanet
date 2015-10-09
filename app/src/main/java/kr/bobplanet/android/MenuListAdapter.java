package kr.bobplanet.android;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.bobplanet.backend.bobplanetApi.model.Menu;
import kr.bobplanet.backend.bobplanetApi.model.Submenu;

/**
 * {@link DayViewFragment}에서 사용되는 ListAdapter.
 *
 * - setMenuList()를 통해 fragment로부터 메뉴 데이터를 전달받음
 * - 메뉴 썸네일은 Volley에서 제공하는 <code>NetworkImageView</code>를 이용하여 async로 가져옴
 *
 * @author hkjinlee on 15. 9. 29
 */
public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MenuViewHolder> {
    private static final String TAG = MenuListAdapter.class.getSimpleName();

    private Context context;
    private List<Menu> menuList = new ArrayList<Menu>();
    private ImageLoader imageLoader = MainApplication.getInstance().getImageLoader();

    private static final String[] WHEN_ARRAY = { "08:00", "12:00", "18:00" };

    public MenuListAdapter(Context context, List<Menu> menuList) {
        this.context = context;
        this.menuList = menuList;
    }

    @Override
    public int getItemCount() {
        return menuList == null ? 0 : menuList.size();
    }

    @Override
    public MenuViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_day_row, viewGroup, false);
        return new MenuViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MenuViewHolder viewHolder, int position) {
        Menu menu = menuList.get(position);
        if (menu == null) return;

        Resources resources = context.getResources();

        int index = Arrays.binarySearch(WHEN_ARRAY, menu.getWhen());
        viewHolder.when.setText(new StringBuilder()
                .append(resources.getStringArray(R.array.when_titles)[index])
                .append(menu.getType() == null ? "" : menu.getType())
        );
        viewHolder.when.setBackgroundColor(
                resources.obtainTypedArray(R.array.when_background_color).getColor(index, Color.BLACK));

        viewHolder.icon.setImageUrl(menu.getItem().getIconURL(), imageLoader);

        viewHolder.title.setText(menu.getItem().getId());

        viewHolder.rating.setRating(menu.getItem().getAverageScore());

        // 서브메뉴는 ','로 concatenate
        List<Submenu> submenus = menu.getSubmenu();
        if (submenus != null) {
            List<String> subs = new ArrayList<String>();
            for (Submenu sub : submenus) {
                subs.add(sub.getItem().getId());
            }
            viewHolder.submenu.setText(TextUtils.join(", ", subs));
        }

        // 아침 메뉴는 칼로리 데이터가 없으므로 비워서 보여줌
        int cal = menu.getCalories();
        viewHolder.calories.setText(menu.getCalories() == 0 ? "" : new StringBuilder().append(cal).append(" KCal"));
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView when;
        NetworkImageView icon;
        TextView title;
        RatingBar rating;
        TextView submenu;
        TextView calories;

        public MenuViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            when = (TextView) itemLayoutView.findViewById(R.id.when);
            icon = (NetworkImageView) itemLayoutView.findViewById(R.id.icon);
            title = (TextView) itemLayoutView.findViewById(R.id.title);
            rating = (RatingBar) itemLayoutView.findViewById(R.id.rating);
            LayerDrawable progress = (LayerDrawable) rating.getProgressDrawable();
            DrawableCompat.setTint(progress.getDrawable(2), Color.MAGENTA);
            submenu = (TextView) itemLayoutView.findViewById(R.id.submenu);
            calories = (TextView) itemLayoutView.findViewById(R.id.calories);
        }
    }
}
