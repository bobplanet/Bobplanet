package kr.bobplanet.backend.model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.AlsoLoad;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnLoad;

/**
 * Created by hkjinlee on 15. 9. 28..
 */
@Entity
public class Item {
    @Id
    private String ID;

    private String iconURL;

    @Load
    Ref<ItemScore> score;

    public Item(String ID) {
        this.ID = ID;
    }
    public String getID() {
        return ID;
    }

    public String getIconURL() {
        return iconURL;
    }
  }
