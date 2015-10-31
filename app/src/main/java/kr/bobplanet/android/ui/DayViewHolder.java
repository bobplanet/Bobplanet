package kr.bobplanet.android.ui;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kr.bobplanet.android.R;
import kr.bobplanet.backend.bobplanetApi.model.Item;
import kr.bobplanet.backend.bobplanetApi.model.Menu;
import kr.bobplanet.backend.bobplanetApi.model.Submenu;

/**
 * DayAcvitiy에서 사용하는 ViewHolder. 아침-점심-저녁 메뉴의 listview를 관리함.
 *
 * - 메뉴 클릭할 경우 EventBus를 이용해서 @link{DayActivity}로 Onclick 이벤트 전송
 */
public class DayViewHolder extends BaseListAdapter.BaseViewHolder<Menu> implements View.OnClickListener {
    private static final String TAG = DayViewHolder.class.getSimpleName();

    Menu menu;

    @Bind(R.id.when)
    TextView when;
    @Bind(R.id.thumbnail)
    NetworkImageView thumbnail;
    @Bind(R.id.title)
    TextView title;
    @Bind(R.id.text_thumb_ups)
    TextView thumbUps;
    @Bind(R.id.text_thumb_downs)
    TextView thumbDowns;
    @Bind(R.id.vote_bar_up)
    View voteBarUp;
    @Bind(R.id.submenu)
    TextView submenu;
    @Bind(R.id.calories)
    TextView calories;

    public DayViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

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

        Item item = menu.getItem();

        if (item.getThumbnail() != null) {
            thumbnail.setImageUrl(item.getThumbnail(), getImageLoader());
        } else {
            thumbnail.setDefaultImageResId(R.drawable.no_menu);
        }

        title.setText(item.getName());

        thumbUps.setText(String.format("%,d", item.getNumThumbUps()));
        thumbDowns.setText(String.format("%,d", item.getNumThumbDowns()));

        int totalVotes = item.getNumThumbUps() + item.getNumThumbDowns();
        float barWeight = totalVotes == 0 ? 0.5f : (1f * item.getNumThumbDowns() / totalVotes);
        voteBarUp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT, barWeight));

        // 서브메뉴는 ','로 concatenate
        List<Submenu> submenus = menu.getSubmenu();
        if (submenus != null) {
            List<String> subs = new ArrayList<>();
            for (Submenu sub : submenus) {
                subs.add(sub.getItem().getName());
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
	 * @link DayViewHolder
	 */
    static class ViewClickEvent {
        DayViewHolder viewHolder;

        ViewClickEvent(DayViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }
    }
}
