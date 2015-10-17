package kr.bobplanet.backend.model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;

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
    @Id private Long ID;

	/**
	 * 투표자
	 */
    @Load @Index protected Ref<User> user;
	
	/**
	 * 투표대상 메뉴항목
	 */
    @Load @Index protected Ref<Item> item;
    
	/**
	 * 투표대상 메뉴번호
	 */
	@Load protected Ref<Menu> menu;

	/**
	 * 점수. 5점 만점.
	 */
    protected int score;

	/**
	 * 코멘트.
	 */
    protected String comment;

    public Vote() {
    }

    public Vote(User user, Item item, Menu menu) {
        this.user = Ref.create(user);
        this.item = Ref.create(item);
        this.menu = Ref.create(menu);
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
}
