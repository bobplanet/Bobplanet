package kr.bobplanet.backend.model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.AlsoLoad;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;

/**
 * Created by hkjinlee on 15. 9. 28..
 */
@Entity
public class Item {
    @Id
    private String ID;

    private String iconURL;

    protected float averageScore;

    protected int[] numVotesPerScore = new int[5];

    public Item() {
    }

    public Item(String ID) {
        this.ID = ID;
    }
    public String getID() {
        return ID;
    }
    public String getIconURL() {
        return iconURL;
    }

    public float getAverageScore() {
        return averageScore;
    }

    public int[] getNumVotesPerScore() {
        return numVotesPerScore;
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
