package kr.bobplanet.backend.model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

import java.util.List;

/**
 * 특정 날짜/시간에 제공되는 "메뉴" 객체.
 * 아이템('갈비탕')과 날짜, 시간(아침-점심-저녁), 원산지, 칼로리 등의 데이터로 구성된다.
 *
 * - Datastore의 "Menu" kind로 매핑
 * - P넷의 식단 페이지에 들어있는 메뉴별 고유ID(가령, '2184')를 그대로 key로 사용함
 * - '갈비탕' 등 메뉴 항목은 Item 객체로 별도 관리함
 * - '보리밥' 등 서브메뉴 항목도 Item 객체로 별도 관리하며, 서브메뉴는 여러개이므로 list에 저장. 
 *
 * @author heonkyu.jin@gmail.com
 * @version 15. 9. 28
 */
@Entity
public class Menu {
	/**
	 * 메뉴번호. 통상 4자리 숫자.
	 */
    @Id private Long ID;

	/**
	 * 식사항목. 가령 '갈비탕'
	 */
    @Load private Ref<Item> item;
	
	/**
	 * 제공일자. "2015-10-09" 형식.
	 */
    private String date;
	
	/**
	 * 아침-점심-저녁 구분. 식사제공이 시작되는 시간을 따른다.
	 * 아침은 "07:00", 점심은 "12:00", 저녁은 "18:00"
	 */
    private String when;
	
	/**
	 * 원산지
	 */
    private String origin;
	
	/**
	 * 유형(A/B). 점심메뉴에만 있음.
	 */
    private String type;
	
	/**
	 * 칼로리. 아침메뉴의 경우는 항상 0임.
	 */
    private int calories;

	/**
	 * 서브메뉴 리스트.
	 */
    private List<Submenu> submenu;

    public Menu() {
    }

    public Menu(Long ID) {
        this.ID = ID;
    }

    public Long getID() {
        return ID;
    }

    public String getDate() {
        return date;
    }

    public String getWhen() {
        return when;
    }

    public String getType() {
        return type;
    }

    public int getCalories() {
        return calories;
    }

    public String getOrigin() {
        return origin;
    }

    public Item getItem() {
        return item != null ? item.get() : null;
    }

    public List<Submenu> getSubmenu() {
        return submenu;
    }
}

/**
 * 서브메뉴 객체.
 * Menu 클래스와 마찬가지로 실제 음식종류는 Item 클래스로 관리함.
 */
class Submenu {
	/**
	 * 식사항목. 가령 '보리밥'
	 */
    @Load private Ref<Item> item;
	
	/**
	 * 원산지.
	 */
    private String origin;

    public Submenu() {
    }

    public Item getItem() {
        return item != null ? item.get() : null;
    }
    public String getOrigin() {
        return origin;
    }
}