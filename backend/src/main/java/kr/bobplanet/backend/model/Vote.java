package kr.bobplanet.backend.model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.Parent;

/**
 * Created by hkjinlee on 2015. 10. 3..
 */
@Entity
public class Vote {
    @Id
    private Long ID;

    @Load @Index
    protected Ref<Item> item;
    @Load @Index
    protected Ref<User> user;
    @Load
    protected Ref<Menu> menu;

    protected int score;

    public Vote(User user, Item item, Menu menu) {
        this.user = Ref.create(user);
        this.item = Ref.create(item);
        this.menu = Ref.create(menu);
    }

    public void setScore(int score) {
        this.score = score;
    }
}
