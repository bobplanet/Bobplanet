package kr.bobplanet.android.ui;

import android.view.View;
import android.widget.TextView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.provider.TextCardProvider;

import java.util.Arrays;

import butterknife.Bind;
import butterknife.ButterKnife;
import kr.bobplanet.android.R;
import kr.bobplanet.backend.bobplanetApi.model.ItemStat;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 *
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
    public void render(View view, Card card) {
        super.render(view, card);
        ButterKnife.bind(this, view);

        originValue.setText(menu.getOrigin());
        caloriesValue.setText(menu.getCalories() > 0 ? menu.getCalories() + " KCal" : "-");

        String fmt = view.getContext().getString(R.string.card_summary_counts_fmt);
        String counts = itemStat != null ?
            String.format(fmt, itemStat.getCountWithinDays()) :
            String.format(fmt, 0, 0, 0);
        countsValue.setText(counts);
    }
}
