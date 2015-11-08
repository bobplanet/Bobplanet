package kr.bobplanet.backend.model;

import com.googlecode.objectify.annotation.Subclass;

import java.util.ArrayList;
import java.util.List;

/**
 *
 *
 * @author heonkyu.jin
 * @version 15. 10. 22
 */
@Subclass
public class NextMenuMessage extends BaseMessage {
    /**
     *
     * @param menuIdList
     */
    private NextMenuMessage(String messageType, List<Long> menuIdList) {
        super(messageType);
        putExtra("menuId", menuIdList.get(0).toString());
    }

    /**
     *
     */
    public static NextMenuMessage fromMenuList(String messageType, List<Menu> menuList) {
        List<Long> menuIdList = new ArrayList<>();
        for (Menu menu : menuList) {
            menuIdList.add(menu.getID());
        }

        return new NextMenuMessage(messageType, menuIdList);
    }
}
