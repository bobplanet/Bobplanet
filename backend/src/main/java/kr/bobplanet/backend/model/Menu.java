package kr.bobplanet.backend.model;

import com.googlecode.objectify.annotation.AlsoLoad;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.OnLoad;

/**
 * Created by hkjinlee on 15. 9. 28..
 */
@Entity
public class Menu {
    @Id
    private String ID;

    private String iconURL;

    public String getID() {
        return ID;
    }

    public String getIconURL() {
        return iconURL;
    }

    @Override
    public String toString() {
        return ID;
    }
  }
