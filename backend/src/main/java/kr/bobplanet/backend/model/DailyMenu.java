package kr.bobplanet.backend.model;

import java.util.List;

/**
 * Created by hkjinlee on 2015. 10. 3..
 */
public class DailyMenu {
    protected String date;
    protected List<Menu> menu;
    protected String previousDate;
    protected String nextDate;

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

    public String toString() {
        return new StringBuilder()
                .append("[DailyMenu] {")
                .append("date = ").append(date).append(' ')
                .append("previous = ").append(previousDate).append(' ')
                .append("next = ").append(nextDate).append(' ')
                .append("{ ").append(getMenu().get(0).getItem().getID()).append("... }")
                .toString();
    }

}
