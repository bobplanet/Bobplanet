package kr.bobplanet.backend.model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.AlsoLoad;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;

/**
 * 식당에서 제공되는 개별 메뉴항목 객체(가령, '갈비탕', '잡채밥').
 * 메뉴의 메타데이터 및 평점정보로 구성된다.
 * 
 * - Datastore의 "Item" kind로 매핑.
 * - 메뉴이름(string)을 그대로 key로 사용함.
 * - 평점은 5점 만점 기준이며, 각 점수를 준 사람 수를 기준으로 평균평점을 계산함.
 * 
 * @author heonkyu.jin
 * @version 15. 9. 28
 */
@Entity
public class Item {
	/**
	 * 메뉴명.
	 */
    @Id private String ID;

	/**
	 * 메뉴 아이콘.
	 * P넷은 아이콘을 제공하지 않으므로, scrape할 때 네이버API를 이용해 썸네일 URL을 미리 밀어넣어둔다.
	 */
    private String iconURL;

	/**
	 * 평균점수
	 */
    protected float averageScore;

	/**
	 * 점수대별 평점자수.
	 */
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

	/**
	 * 새로운 평가점수를 합산하여 평균평점에 반영
	 */
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
