package kr.bobplanet.backend.api;

import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Work;
import com.googlecode.objectify.cmd.LoadType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import kr.bobplanet.backend.model.ItemScore;
import kr.bobplanet.backend.model.ItemStat;
import kr.bobplanet.backend.model.Menu;
import kr.bobplanet.backend.model.Item;
import kr.bobplanet.backend.model.DailyMenu;
import kr.bobplanet.backend.model.User;
import kr.bobplanet.backend.model.Vote;

import static kr.bobplanet.backend.api.ObjectifyRegister.ofy;

/**
 * Google Cloud Endpoint를 이용해 서버사이드 API로 가공되는 클래스.
 *
 * - 모든 데이터는 Google DataStore를 이용하며, 객체접근 위해 Objectify 라이브러리 이용
 * - 데이터 노출을 막기 위해 API는 클라이언트ID 기반 권한관리 (따라서, 신규 클라이언트가 추가되면 여기도 수정해줘야 함)
 *
 * @author heonkyu.jin
 * @version 2015. 9. 27
 */
@ApiClass(
     resource = "menu"
)
public class MenuEndpoint extends BaseEndpoint {
	/**
	 * 특정 일자의 메뉴 리스트를 조회.
	 * DailyViewActivity에서 이용함.
	 *
	 * @param date 메뉴를 조회할 날짜. "2015-10-09" 형태여야 함.
	 * @return 해당 날짜의 일간메뉴 객체.
	 */
    @ApiMethod(
            name = "menuOfDate",
            path = "menu/date/{date}"
    )
    public DailyMenu menuOfDate(@Named("date") String date) {
        logger.info("date = " + date);

        LoadType<Menu> menubase = ofy().load().type(Menu.class);
        List<Menu> menu = menubase.filter("date", date).list();

        List<Menu> previous = menubase.filter("date <", date).order("-date").limit(1).list();
        String previous_date = previous.size() > 0 ? previous.get(0).getDate() : null;

        List<Menu> next = menubase.filter("date >", date).order("date").limit(1).list();
        String next_date = next.size() > 0 ? next.get(0).getDate() : null;

        return new DailyMenu(date, menu, previous_date, next_date);
    }

	/**
	 * 주어진 메뉴ID(번호)로 Menu 객체를 가져온다.
	 * MenuViewActivity에서 이용함.
	 *
	 * @param id 메뉴ID
	 */
    @ApiMethod(
            name = "menu",
            path = "menu/{id}"
    )
    public Menu menu(@Named("id") Long id) {
        logger.info("id = " + id);
        Menu m = ofy().load().type(Menu.class).id(id).now();

        logger.info("result = " + m.toString());
        return m;
    }

	/**
	 * Item에 대해 점수를 매긴다.
	 * 이 때 사용자가 매긴 점수 데이터는 Item의 속성에도 summarise되어 반영되며, 별도의 Vote 객체로도 저장된다.
	 * (그래야 향후 사용자-메뉴아이템간 취향분석 등을 할 수 있으므로)
	 *
	 */
    @ApiMethod(
            name = "vote",
            httpMethod = "POST",
            path = "vote"
    )
    public ItemScore vote(final Vote vote) {
        logger.info(String.format("%s", vote.toString()));

        // 데이터 저장시 transaction 처리
        return ofy().transact(new Work<ItemScore>() {
            final Item item = vote.getItem();

            @Override
            public ItemScore run() {
                Vote oldVote = ofy().load().type(Vote.class)
                        .ancestor(item).filter("user", vote.getUser()).first().now();

                if (oldVote != null) {
                    vote.setId(oldVote.getId());
                }

                ItemScore itemScore = ofy().load().type(ItemScore.class).parent(item).id(item.getName()).now();
                if (itemScore == null) {
                    itemScore = new ItemScore(vote);
                }

                logger.info("oldVote = " + oldVote);
                if (oldVote != null) {
                    logger.info("Vote exists. Updates score");

                    itemScore.applyVote(vote, oldVote);
                } else {
                    logger.info("no oldVote. Just adds score");

                    itemScore.applyVote(vote);
                }

                ofy().save().entities(vote, itemScore).now();

                return itemScore;
            }
        });
    }

    @ApiMethod(
            name = "myVote",
            httpMethod = "GET",
            path = "myVote/{userId}/{menuId}"
    )
    public Vote myVote(@Named("userId") final String userId, @Named("menuId") final Long menuId) {
        logger.info(String.format(
                        "{ userId, menuId } = { %s, %s }",
                        userId, menuId)
        );

        Vote vote = ofy().load().type(Vote.class)
                .filter("user", new User(userId))
                .filter("menu", new Menu(menuId))
                .first().now();

        logger.info("myVote() vote = " + vote);
        return vote;
    }

    @ApiMethod(
            name = "itemScores",
            path = "item/score"
    )
    public Collection<ItemScore> itemScores(@Named("itemNames") List<String> itemNames) {
        logger.info(itemNames.toString());

        List<Key<ItemScore>> keys = new ArrayList<>();
        for (String name : itemNames) {
            keys.add(Key.create(Key.create(Item.class, name), ItemScore.class, name));
        }
        return ofy().load().keys(keys).values();
    }

    @ApiMethod(
            name = "itemStat",
            path = "item/stat/{itemName}"
    )
    public ItemStat itemStat(@Named("itemName") String itemName) {
        return ofy().load().type(ItemStat.class).id(itemName).now();
    }
}