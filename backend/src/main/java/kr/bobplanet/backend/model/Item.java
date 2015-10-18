package kr.bobplanet.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * 식당에서 제공되는 개별 메뉴항목 객체(가령, '갈비탕', '잡채밥').
 * 메뉴의 메타데이터 및 평점정보로 구성된다.
 * 
 * - 메뉴 이미지는 외부검색에서 긁어옴 (처음에 네이버API 쓰다가 다음API로 전향)
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
     * 메뉴 큰 이미지.
     */
    private String image;

	/**
	 * 메뉴 아이콘.
	 */
    private String thumbnail;

	/**
	 * 평균점수
	 */
    protected float averageScore;

	/**
	 * 점수대별 평점자수.
	 */
    protected final int[] numVotesPerScore = new int[5];

    public Item() {
    }

    public Item(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public String getImage() {
        return image;
    }

    public String getThumbnail() {
        return thumbnail;
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
        recalculateScore();
    }

	/**
	 * 기존 평가자가 점수를 수정하는 경우, 기존점수는 제거하고 평균평점에 반영
	 */
    public void editScore(int score, int oldScore) {
        numVotesPerScore[score - 1]++;
        numVotesPerScore[oldScore - 1]--;
        recalculateScore();
    }

    /**
     * 평점대별 평점자수에 기반하여 정확한 평점 계산
     */
    private void recalculateScore() {
        long totalVotes = 0;
        long totalScore = 0;
        for (int i = 0; i < 5; i++) {
            totalVotes += numVotesPerScore[i];
            totalScore += (i + 1) * numVotesPerScore[i];
        }
        averageScore = totalScore / totalVotes;
    }
}
