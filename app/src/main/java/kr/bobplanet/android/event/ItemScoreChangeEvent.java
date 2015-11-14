package kr.bobplanet.android.event;

import kr.bobplanet.backend.bobplanetApi.model.Item;
import kr.bobplanet.backend.bobplanetApi.model.ItemScore;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 *
 *
 * @author heonkyu.jin
 * @version 15. 10. 18
 */
public class ItemScoreChangeEvent {
    private ItemScore itemScore;

    public ItemScoreChangeEvent(ItemScore itemScore) {
        this.itemScore = itemScore;
    }

    public boolean isFor(Menu menu) {
        return menu.getItem().getName().equals(itemScore.getName());
    }

    public void apply(Menu menu) {
        apply(menu.getItem());
    }

    public void apply(Item item) {
        item.setScore(itemScore);
    }
}
