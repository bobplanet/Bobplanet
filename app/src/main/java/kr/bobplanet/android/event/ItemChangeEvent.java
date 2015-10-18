package kr.bobplanet.android.event;

import kr.bobplanet.backend.bobplanetApi.model.Item;

/**
 *
 *
 * @author heonkyu.jin
 * @version 15. 10. 18
 */
public class ItemChangeEvent {
    private Item item;

    public ItemChangeEvent(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }
}
