package kr.bobplanet.android;

import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kr.bobplanet.backend.bobplanetApi.model.Menu;
import kr.bobplanet.backend.bobplanetApi.model.Submenu;

/**
 * 메뉴 객체의 ViewHolder.
 * EventBus를 이용해서 @link{DayActivity}로 Onclick 이벤트 전송
 */
class MenuViewHolder extends BaseListAdapter.BaseViewHolder<Menu> implements View.OnClickListener {
    Menu menu;

    @Bind(R.id.when)
    TextView when;
    @Bind(R.id.thumbnail)
    NetworkImageView thumbnail;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.rating)
    RatingBar rating;
    @Bind(R.id.submenu)
    TextView submenu;
    @Bind(R.id.calories)
    TextView calories;

    public MenuViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        LayerDrawable progress = (LayerDrawable) rating.getProgressDrawable();
        DrawableCompat.setTint(progress.getDrawable(2), Color.MAGENTA);

        itemView.setOnClickListener(this);
    }

    @Override
    void setItem(Menu menu) {
        if (menu == null) return;

        this.menu = menu;

        int background = R.color.breakfast_bg;
        switch (menu.getWhen()) {
            case "아침":
                background = R.color.breakfast_bg;
                break;
            case "점심":
                background = R.color.lunch_bg;
                break;
            case "저녁":
                background = R.color.dinner_bg;
        }

        when.setText(menu.getWhen() + (menu.getType() == null ? "" : menu.getType()));
        when.setBackgroundColor(ContextCompat.getColor(itemView.getContext(), background));

        if (menu.getItem().getThumbnail() != null) {
            thumbnail.setImageUrl(menu.getItem().getThumbnail(), getImageLoader());
        } else {
            thumbnail.setDefaultImageResId(R.drawable.no_menu);
        }

        title.setText(menu.getItem().getId());

        rating.setRating(menu.getItem().getAverageScore());

        // 서브메뉴는 ','로 concatenate
        List<Submenu> submenus = menu.getSubmenu();
        if (submenus != null) {
            List<String> subs = new ArrayList<>();
            for (Submenu sub : submenus) {
                subs.add(sub.getItem().getId());
            }
            submenu.setText(TextUtils.join(", ", subs));
        }

        // 아침 메뉴는 칼로리 데이터가 없으므로 비워서 보여줌
        int cal = menu.getCalories();
        calories.setText(menu.getCalories() == 0 ? "" : new StringBuilder().append(cal).append(" KCal"));
    }

    @Override
    public void onClick(View v) {
        EventBus.getDefault().post(new ViewClickEvent(this));
    }

    /**
	 * 메뉴 클릭 이벤트.
	 * @link MenuViewHolder
	 */
    static class ViewClickEvent {
        MenuViewHolder viewHolder;

        ViewClickEvent(MenuViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }
    }
}
