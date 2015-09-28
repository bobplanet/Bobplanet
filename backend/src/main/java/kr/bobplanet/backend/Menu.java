package kr.bobplanet.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by hkjinlee on 15. 9. 28..
 */
@Entity
public class Menu {
    @Id
    private String name;
    private String iconURL;

    public String getName() {
        return name;
    }

    public String getIconURL() {
        return iconURL;
    }
}
