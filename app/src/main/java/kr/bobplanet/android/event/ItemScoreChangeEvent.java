package kr.bobplanet.android.event;

import kr.bobplanet.backend.bobplanetApi.model.Item;
import kr.bobplanet.backend.bobplanetApi.model.ItemScore;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 * 사용자가 메뉴를 평가해서 메뉴의 전체 점수가 변경되었을 때 발생하는 이벤트 객체.
 * 최신 데이터에 맞게 화면을 다시 그려주는 용도로 사용함.
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
