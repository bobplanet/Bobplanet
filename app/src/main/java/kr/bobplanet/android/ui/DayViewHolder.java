package kr.bobplanet.android.ui;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import kr.bobplanet.android.Constants;
import kr.bobplanet.android.R;
import kr.bobplanet.android.Util;
import kr.bobplanet.android.VoteManager;
import kr.bobplanet.backend.bobplanetApi.model.Item;
import kr.bobplanet.backend.bobplanetApi.model.ItemVoteSummary;
import kr.bobplanet.backend.bobplanetApi.model.Menu;
import kr.bobplanet.backend.bobplanetApi.model.Submenu;

/**
 * DayAcvitiy에서 사용하는 ViewHolder. 아침-점심-저녁 메뉴의 listview를 관리함.
 *
 * - 메뉴 클릭할 경우 EventBus를 이용해서 @link{DayActivity}로 Onclick 이벤트 전송
 */
public class DayViewHolder extends BaseListAdapter.BaseViewHolder<Menu> implements View.OnClickListener, Constants {
    private static final String TAG = DayViewHolder.class.getSimpleName();

    private static final int SUBMENU_DISPLAY_COUNT = 3;

    Menu menu;

    @Bind(R.id.when)
    TextView when;
    @Bind(R.id.image)
    NetworkImageView image;
    @Bind(R.id.name)
    TextView name;
    @Bind(R.id.signal)
    ImageView signal;
    @Bind(R.id.submenu)
    TextView submenu;
    @Bind(R.id.button_thumb_up)
    Button buttonThumbUp;
    @Bind(R.id.button_thumb_down)
    Button buttonThumbDown;
    @Bind(R.id.thumb_up_comment)
    TextView thumbUpComment;
    @Bind(R.id.thumb_down_comment)
    TextView thumbDownComment;

    public DayViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(this);
    }

    @Override
    void setItem(Menu menu) {
        if (menu == null) return;
        this.menu = menu;
        setMeta();
        setVote();
    }

    private void setMeta() {
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

        if (item.getImage() != null) {
            image.setImageUrl(item.getImage(), getImageLoader());
        } else {
            image.setDefaultImageResId(R.drawable.no_menu);
        }

        name.setText(item.getName());

        // 서브메뉴는 ','로 concatenate
        List<Submenu> submenus = menu.getSubmenu();
        if (submenus != null) {
            String text = TextUtils.join(", ",
                    fj.data.List.range(0, SUBMENU_DISPLAY_COUNT)
                            .map(i -> submenus.get(i).getItem().getName())
                            .toJavaList());
            submenu.setText(text);
        }
    }

    private void setVote() {
        Item item = menu.getItem();
        ItemVoteSummary voteSummary = item.getVoteSummary();

        int thumbUps = voteSummary.getNumThumbUps();
        int thumbDowns = voteSummary.getNumThumbDowns();

        int drawableResId = R.drawable.signal_wait;
        if (thumbUps + 1> (thumbDowns + 1) * 2) {
            drawableResId = R.drawable.signal_go;
        } else if (thumbDowns + 1> (thumbUps + 1) * 2) {
            drawableResId = R.drawable.signal_stop;
        }
        signal.setImageDrawable(ContextCompat.getDrawable(itemView.getContext(), drawableResId));

        BaseActivity baseActivity = Util.getBaseActivity(itemView);

        buttonThumbUp.setText(String.format("%,d", thumbUps));
        buttonThumbUp.setOnClickListener(v -> new VoteManager(baseActivity, menu).showVoteDialog(VOTE_UP));
        buttonThumbDown.setText(String.format("%,d", thumbDowns));
        buttonThumbDown.setOnClickListener(v -> new VoteManager(baseActivity, menu).showVoteDialog(VOTE_DOWN));
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
