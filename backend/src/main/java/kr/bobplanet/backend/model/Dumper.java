package kr.bobplanet.backend.model;

/**
 * Created by hkjinlee on 2015. 10. 3..
 */
public class Dumper {
    public String toString(DailyMenu d) {
        return new StringBuilder()
                .append("[DailyMenu] {")
                .append("date = ").append(d.date).append(' ')
                .append("previous = ").append(d.previousDate).append(' ')
                .append("next = ").append(d.nextDate).append(' ')
                .append("{ ").append(d.getMenu().get(0).getItem().getID()).append("... }")
                .toString();
    }
}
