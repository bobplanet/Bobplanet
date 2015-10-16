package kr.bobplanet.android;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kr.bobplanet.backend.bobplanetApi.model.Menu;
import kr.bobplanet.backend.bobplanetApi.model.Submenu;

/**
 * {@link DayFragment}에서 사용되는 ListAdapter.
 *
 * - setMenuList()를 통해 fragment로부터 메뉴 데이터를 전달받음
 * - 메뉴 썸네일은 Volley에서 제공하는 <code>NetworkImageView</code>를 이용하여 async로 가져옴
 *
 * TODO 별을 하트로 바꾸고 색상 조정
 *
 * @author heonkyu.jin
 * @version 15. 9. 29
 */
public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
    @SuppressWarnings("UnusedDeclaration")
    private static final String TAG = DayAdapter.class.getSimpleName();

    private Context context;
    private List<Menu> menuList = new ArrayList<>();
    private final ImageLoader imageLoader = MainApplication.getInstance().getImageLoader();

    private static final String[] WHEN_ARRAY = { "08:00", "12:00", "18:00" };

    public DayAdapter(Context context, List<Menu> menuList) {
        this.context = context;
        this.menuList = menuList;
    }

    @Override
    public int getItemCount() {
        return menuList == null ? 0 : menuList.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.day_item, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Menu menu = menuList.get(position);
        if (menu == null) return;

        holder.setMenu(menu);

        Resources resources = context.getResources();

        int index = Arrays.binarySearch(WHEN_ARRAY, menu.getWhen());
        holder.when.setText(new StringBuilder()
                        .append(resources.getStringArray(R.array.when_titles)[index])
                        .append(menu.getType() == null ? "" : menu.getType())
        );
        holder.when.setBackgroundColor(
                resources.obtainTypedArray(R.array.when_background_color).getColor(index, Color.BLACK));

        if (menu.getItem().getIconURL() != null) {
            holder.icon.setImageUrl(menu.getItem().getIconURL(), imageLoader);
        } else {
            holder.icon.setDefaultImageResId(R.drawable.no_menu);
        }

        holder.title.setText(menu.getItem().getId());

        holder.rating.setRating(menu.getItem().getAverageScore());

        // 서브메뉴는 ','로 concatenate
        List<Submenu> submenus = menu.getSubmenu();
        if (submenus != null) {
            List<String> subs = new ArrayList<>();
            for (Submenu sub : submenus) {
                subs.add(sub.getItem().getId());
            }
            holder.submenu.setText(TextUtils.join(", ", subs));
        }

        // 아침 메뉴는 칼로리 데이터가 없으므로 비워서 보여줌
        int cal = menu.getCalories();
        holder.calories.setText(menu.getCalories() == 0 ? "" : new StringBuilder().append(cal).append(" KCal"));
    }

	/**
	 * 메뉴 객체의 ViewHolder.
	 * EventBus를 이용해서 @link{DayActivity}로 Onclick 이벤트 전송
	 */
    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Menu menu;

        @Bind(R.id.when) TextView when;
        @Bind(R.id.icon) NetworkImageView icon;
        @Bind(R.id.title) TextView title;
        @Bind(R.id.rating) RatingBar rating;
        @Bind(R.id.submenu) TextView submenu;
        @Bind(R.id.calories) TextView calories;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            LayerDrawable progress = (LayerDrawable) rating.getProgressDrawable();
            DrawableCompat.setTint(progress.getDrawable(2), Color.MAGENTA);

            itemView.setOnClickListener(this);
        }

        void setMenu(Menu menu) {
            this.menu = menu;
        }

        @Override
        public void onClick(View v) {
            EventBus.getDefault().post(new ViewClickEvent(this));
        }
    }

	/**
	 * 메뉴 클릭 이벤트.
	 * @link MenuViewHolder
	 */
    static class ViewClickEvent {
        ViewHolder viewHolder;

        ViewClickEvent(ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }
    }
}
