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
public class Menu {

    @Id private Long ID;

    @Load private Ref<Item> item;
    private String date;
    private String when;
    private String origin;
    private int calories;

    private List<Submenu> submenu;

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

class Submenu {
    @Load
    private Ref<Item> item;
    private String origin;

    public Item getItem() {
        return item != null ? item.get() : null;
    }
    public String getOrigin() {
        return origin;
    }
}