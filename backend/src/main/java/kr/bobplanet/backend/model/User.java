package kr.bobplanet.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * 이용자 객체.
 *
 * @author heonkyu.jin
 * @version 2015. 10. 3
 */
@Entity
public class User {
    @Id String id;

    String nickName;

    public User() {
    }

    public User(String id) {
        this.id = id;
    }
}
