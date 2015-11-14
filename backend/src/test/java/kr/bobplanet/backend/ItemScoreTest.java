package kr.bobplanet.backend;

import com.google.common.collect.Lists;

import junit.framework.TestCase;

import kr.bobplanet.backend.model.ItemScore;
import kr.bobplanet.backend.model.Vote;

/**
 *
 * @author heonkyu.jin
 * @version 15. 11. 14
 */
public class ItemScoreTest extends TestCase {
    Vote oldVote;
    Vote vote;
    ItemScore itemScore;

    public void setUp() {
        oldVote = new Vote();
        oldVote.setScore(-1);
        oldVote.setComments(Lists.newArrayList("Bad"));

        vote = new Vote();
        vote.setScore(1);
        vote.setComments(Lists.newArrayList("Good"));

        itemScore = new ItemScore();
    }

    public void testInitial() {
        assertEquals(1, vote.getScore());
        assertEquals(-1, oldVote.getScore());
    }

    public void testNewVote() {
        assertEquals(0, itemScore.getNumThumbUps());
        itemScore.applyVote(vote);
        assertEquals(1, itemScore.getNumThumbUps());
    }

    public void testUpdateVote() {
        assertEquals(0, itemScore.getNumThumbUps());

        itemScore.applyVote(oldVote);
        assertEquals(0, itemScore.getNumThumbUps());
        assertEquals(1, itemScore.getNumThumbDowns());
        assertEquals(1, itemScore.getDownComments().getSize());
        assertEquals("Bad", itemScore.getDownComments().getComment(0).getText());
        assertEquals(0, itemScore.getUpComments().getSize());

        itemScore.applyVote(vote, oldVote);
        assertEquals(1, itemScore.getNumThumbUps());
        assertEquals(0, itemScore.getNumThumbDowns());
        assertEquals(0, itemScore.getDownComments().getSize());
        assertEquals("Good", itemScore.getUpComments().getComment(0).getText());
        assertEquals(1, itemScore.getUpComments().getSize());
    }
}
