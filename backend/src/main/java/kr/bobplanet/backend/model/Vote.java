package kr.bobplanet.backend.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreLoad;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Parent;

import java.util.Date;
import java.util.List;

/**
 * 사용자가 평점을 매길 때마다 하나씩 생성되는 투표 객체.
 *
 * @author heonkyu.jin
 * @version 2015. 10. 3
 */
@Entity
public class Vote {
    /**
     * 투표번호. 별도로 지정하지 않으며 서버에서 자동으로 채번되는 인조key.
     */
    @Id
    private Long id;

    /**
     * 투표대상 메뉴항목
     */
    @Parent
    @Index
    protected Ref<Item> item;

    /**
     * 투표자
     */
    @Index
    protected Ref<User> user;

    /**
     * 투표대상 메뉴번호
     */
    @Load
    @Index
    protected Ref<Menu> menu;

    /**
     * 점수. +1 or -1.
     */
    protected int score;

    /**
     * 코멘트.
     */
    @Load
    protected List<String> comments;

    /**
     *
     */
    @IgnoreLoad
    protected Date updateDate;

    public Vote() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user == null ? null : user.get();
    }

    public void setUserId(String userId) {
        this.user = Ref.create(Key.create(User.class, userId));
    }

    public Item getItem() {
        return item.get();
    }

    public void setItemName(String itemName) {
        this.item = Ref.create(Key.create(Item.class, itemName));
    }

    public void setMenuId(Long menuId) {
        this.menu = Ref.create(Key.create(Menu.class, menuId));
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    @OnSave
    public void onSave() {
        updateDate = new Date();
    }

    @Override
    public String toString() {
        return String.format("Vote { id = %s, user = %s, itemId = %s, score = %d, comments = %s }",
                id, user, item, score, comments);
    }
}
