package kr.bobplanet.android.ui;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.provider.TextCardProvider;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import kr.bobplanet.android.R;
import kr.bobplanet.backend.bobplanetApi.model.ItemStat;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 * 메뉴 요약정보 카드.
 * 원산지, 칼로리 등을 보여주기 위해 Menu 객체, 최근 n개월간 제공횟수를 보여주기 위해 ItemStat 객체를 참조.
 *
 * @author heonkyu.jin
 * @version 15. 10. 17
 */
public class MenuSummaryCardProvider extends TextCardProvider<MenuSummaryCardProvider> {
    private Menu menu;
    private ItemStat itemStat;

    @Bind(R.id.originValue)
    TextView originValue;

    @Bind(R.id.caloriesValue)
    TextView caloriesValue;

    @Bind(R.id.countsValue)
    TextView countsValue;

    public MenuSummaryCardProvider setMenu(Menu menu) {
        this.menu = menu;
        return this;
    }

    public MenuSummaryCardProvider setItemStat(ItemStat itemStat) {
        this.itemStat = itemStat;
        notifyDataSetChanged();
        return this;
    }

    @Override
    public int getLayout() {
        return R.layout.menu_summary_card;
    }

    @Override
    public void render(@NonNull View view, @NonNull Card card) {
        super.render(view, card);
        ButterKnife.bind(this, view);

        if (menu != null) {
            originValue.setText(menu.getOrigin());
            caloriesValue.setText(menu.getCalories() > 0 ? menu.getCalories() + " KCal" : "-");
        }

        if (itemStat != null) {
            List<Integer> counts = itemStat.getCountWithinDays();
            String fmt = view.getContext().getString(R.string.card_summary_counts_fmt);
            String text = itemStat != null ?
                    String.format(fmt, counts.get(0), counts.get(1), counts.get(2)) :
                    String.format(fmt, 0, 0, 0);
            countsValue.setText(text);
        }
    }
}
