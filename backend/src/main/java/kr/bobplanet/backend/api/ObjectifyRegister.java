package kr.bobplanet.backend.api;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

import kr.bobplanet.backend.model.Item;
import kr.bobplanet.backend.model.Menu;
import kr.bobplanet.backend.model.User;
import kr.bobplanet.backend.model.Vote;

/**
 * Created by hkjinlee on 2015. 10. 4..
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
