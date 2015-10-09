package kr.bobplanet.backend.model;

import java.util.List;

/**
 * 일간 메뉴 객체.
 * 아침-점심-저녁 메뉴 및 다음 영업일, 이전 영업일 데이터로 구성된다.
 * 
 * - Menu 객체의 collection 성격임 (별도로 DataStore에 저장되지 않는다)
 * - 주말 및 공휴일에는 식당이 영업하지 않으므로 '다음영업일', '이전영업일'이 있어야 효율적인 데이터 fetch가 가능
 *
 * @author heonkyu.jin
 * @version 2015. 10. 3
 */
public class DailyMenu {
	/**
	 * 본 객체의 날짜.
	 */
    protected String date;
	
	/**
	 * 아침-점심-저녁 메뉴.
	 * 통상 점심메뉴는 A/B 두가지가 제공되므로, 대개의 경우 List의 크기는 4임.
	 */
    protected List<Menu> menu;
	
	/**
	 * 이전 영업일
	 */
    protected String previousDate;
	
	/**
	 * 다음 영업일
	 */
    protected String nextDate;

    public DailyMenu() {
    }

    public DailyMenu(String date, List<Menu> menu, String previousDate, String nextDate) {
        this.date = date;
        this.menu = menu;
        this.previousDate = previousDate;
        this.nextDate = nextDate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Menu> getMenu() {
        return menu;
    }

    public void setMenu(List<Menu> menu) {
        this.menu = menu;
    }

    public String getPreviousDate() {
        return previousDate;
    }

    public void setPreviousDate(String previousDate) {
        this.previousDate = previousDate;
    }

    public String getNextDate() {
        return nextDate;
    }

    public void setNextDate(String nextDate) {
        this.nextDate = nextDate;
    }
}
