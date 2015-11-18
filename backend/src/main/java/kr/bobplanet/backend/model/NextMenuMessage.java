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
     */
    private NextMenuMessage(String messageType, List<Menu> menuList) {
        super(messageType);
        Menu menu1 = menuList.get(0);

        String menuName;
        if (menuList.size() == 1) {
            menuName = menu1.getItem().getName();
        } else {
            Menu menu2 = menuList.get(1);
            menuName = String.format("%s vs %s", menu1.getItem().getName(), menu2.getItem().getName());
            putExtra("menu2.id", String.valueOf(menu2.getID()));
            putExtra("menu2.image", menu2.getItem().getImage());
        }

        setText(menuName);
        putExtra("menu1.id", String.valueOf(menu1.getID()));
        putExtra("menu1.image", menu1.getItem().getImage());
    }

    /**
     *
     */
    public static NextMenuMessage fromMenuList(String messageType, List<Menu> menuList) {
        return new NextMenuMessage(messageType, menuList);
    }
}
