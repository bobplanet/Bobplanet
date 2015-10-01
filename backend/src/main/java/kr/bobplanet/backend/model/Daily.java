package kr.bobplanet.backend.model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;

import java.util.List;

/**
 * Created by hkjinlee on 15. 9. 28..
 */
@Entity
public class Daily {

    @Id private Long ID;

    @Load private Ref<Menu> menu;
    private String date;
    private String when;
    private int calories;

    private List<DailySub> submenus;

    public Long getID() {
        return ID;
    }

    public String getDate() {
        return date;
    }

    public String getWhen() {
        return when;
    }

    public int getCalories() {
        return calories;
    }

    public Menu getMenu() {
        return menu != null ? menu.get() : null;
    }

    public List<DailySub> getSubList() {
        return submenus;
    }
}

class DailySub {
    @Load
    private Ref<Menu> menu;
    private String from;

    public Menu getMenu() {
        return menu != null ? menu.get() : null;
    }
    public String getFrom() {
        return from;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(getMenu().getID())
                .append('(')
                .append(from)
                .append(')')
                .toString();
    }
}