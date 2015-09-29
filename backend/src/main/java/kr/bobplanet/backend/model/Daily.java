package kr.bobplanet.backend.model;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Load;

/**
 * Created by hkjinlee on 15. 9. 28..
 */
@Entity
public class Daily {

    @Id private Long ID;
    private String name;

    @Load private Ref<Menu> menu;
    private String date;
    private String when;
    private int calories;

    public Long getID() { return ID; }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
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
}
