package kr.bobplanet.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreLoad;
import com.googlecode.objectify.annotation.OnSave;

import java.util.Date;

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
    @Id
    private String name;

    /**
     * 메뉴 큰 이미지.
     */
    private String image;

	/**
	 * 메뉴 아이콘.
	 */
    private String thumbnail;

    private ItemScore score;

    /**
     * 최종수정일
     */
    @IgnoreLoad
    protected Date updateDate;

    public Item() {
    }

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public ItemScore getScore() {
        return score;
    }

    public void setScore(ItemScore score) {
        this.score = score;
    }

    @OnSave
    public void onSave() {
        this.updateDate = new Date();
    }
}
