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
public class ItemScore {
    @Id
    protected Long ID;

    @Load @Index
    protected Ref<Item> item;

    protected float averageScore;

    protected int[] numVotesPerScore = new int[5];

    public ItemScore() {
    }

    public ItemScore(Item item) {
        this.item = Ref.create(item);
    }

    public void addScore(int score) {
        numVotesPerScore[score - 1]++;
        long totalVotes = 0;
        long totalScore = 0;
        for (int i = 0; i < 5; i++) {
            totalVotes += numVotesPerScore[i];
            totalScore += (i + 1) * numVotesPerScore[i];
        }
        averageScore = totalScore / totalVotes;
    }
}
