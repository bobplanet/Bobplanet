package kr.bobplanet.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by hkjinlee on 15. 9. 28..
 */
@Entity
public class Daily {

    @Id
    private Long ID;

    private String name;
    private String date;
    private String when;
    private int calories;

    public Long getID() {
        return ID;
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
}
