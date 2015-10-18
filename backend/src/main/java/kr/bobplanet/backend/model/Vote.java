package kr.bobplanet.backend.model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Parent;

import java.util.Date;

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
    private Long ID;

    /**
     * 투표자
     */
    @Index
    protected Ref<User> user;

    /**
     * 투표대상 메뉴항목
     */
    @Parent
    @Index
    protected Ref<Item> item;

    /**
     * 투표대상 메뉴번호
     */
    @Load
    protected Ref<Menu> menu;

    /**
     * 점수. 5점 만점.
     */
    protected int score;

    /**
     * 코멘트.
     */
    protected String comment;

    /**
     *
     */
    protected Date updateDate;

    public Vote() {
    }

    public Vote(User user, Item item, Menu menu) {
        this.user = Ref.create(user);
        this.item = Ref.create(item);
        this.menu = Ref.create(menu);
    }

    public long getId() {
        return ID;
    }

    public void setId(long ID) {
        this.ID = ID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @OnSave
    public void onSave() {
        updateDate = new Date();
    }
}
