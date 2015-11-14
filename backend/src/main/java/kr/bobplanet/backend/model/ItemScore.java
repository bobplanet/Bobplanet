package kr.bobplanet.backend.model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreLoad;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Parent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 메뉴아이템의 평점 요약 객체.
 * 
 * - '좋아요'는 1점, '싫어요'는 -1점 기준
 * - 태그별 호응자 수
 * 
 * @author heonkyu.jin
 * @version 15. 11. 7
 */
@Entity
public class ItemScore {
	/**
	 * 메뉴명.
	 */
    @Parent
    private Ref<Item> item;

    @Id
    private String name;

    /**
     * '좋아요'한 사람 수
     */
    protected int numThumbUps;

    /**
     * '싫어요'한 사람 수
     */
    protected int numThumbDowns;

    protected Map<String, Integer> commentWithCounts = new HashMap<>();

    /**
     * 최종수정일
     */
    @IgnoreLoad
    protected Date updateDate;

    public ItemScore() { }

    public ItemScore(Item item) {
        this.item = Ref.create(item);
        this.name = item.getName();
    }

    public ItemScore(Vote vote) {
        this(vote.getItem());
    }

    public String getName() {
        return name;
    }

    public Item getItem() {
        return item == null ? null : item.get();
    }

    public int getNumThumbUps() {
        return numThumbUps;
    }

    public int getNumThumbDowns() {
        return numThumbDowns;
    }

    public Map<String, Integer> getCommentWithCounts() {
        return commentWithCounts;
    }

    public void applyVote(Vote vote) {
        numThumbUps += vote.getScore() > 0 ? 1 : 0;
        numThumbDowns += vote.getScore() < 0 ? 1 : 0;

        if (vote.getComments() != null) {
            for (String comment : vote.getComments()) {
                commentWithCounts.put(comment, 1);
            }
        }
    }

    public void applyVote(Vote vote, Vote oldVote) {
        int score = vote.getScore();
        int oldScore = oldVote.getScore();

        numThumbUps += score > 0 ? 1 : 0;
        numThumbDowns += score < 0 ? 1 : 0;
        numThumbUps -= oldScore > 0 ? 1 : 0;
        numThumbDowns -= oldScore < 0 ? 1 : 0;

        if (vote.getComments() != null) {
            for (String c : vote.getComments()) {
                commentWithCounts.put(c, commentWithCounts.containsKey(c) ?
                        commentWithCounts.get(c) + 1 : 1);
            }
        }
        if (oldVote.getComments() != null) {
            for (String c : oldVote.getComments()) {
                commentWithCounts.put(c, commentWithCounts.containsKey(c) ?
                        commentWithCounts.get(c) - 1 : 0);
            }
        }
        for (String c : commentWithCounts.keySet()) {
            if (commentWithCounts.get(c) == 0) {
                commentWithCounts.remove(c);
            }
        }
    }

    @OnSave
    public void onSave() {
        this.updateDate = new Date();
    }
}
