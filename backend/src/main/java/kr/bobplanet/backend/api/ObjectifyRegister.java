package kr.bobplanet.backend.api;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import kr.bobplanet.backend.model.Item;
import kr.bobplanet.backend.model.Menu;
import kr.bobplanet.backend.model.User;
import kr.bobplanet.backend.model.Vote;

/**
 * Objectify를 통해 관리되어야 하는 객체를 등록해주는 클래스.
 * model 아래에 새로운 객체를 추가할 경우 본 클래스의 static initializer에 반드시 추가해주어야 한다.
 * (단, 별도 저장될 필요가 없는 DailyMenu 등은 해당되지 않는다)
 *
 * - 각 Endpoint에서 ObjectifyRegister.ofy()를 static import할 경우 코드 용량이 살~짝 줄어들 수 있음
 *   (ObjectifyService.ofy()가 아니라 그냥 ofy()라고 코딩하면 된다)
 *
 * @author heonkyu.jin
 * @version 2015. 10. 4
 */
public final class ObjectifyRegister {
    static {
        ObjectifyService.register(Menu.class);
        ObjectifyService.register(Item.class);
        ObjectifyService.register(User.class);
        ObjectifyService.register(Vote.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }
}
