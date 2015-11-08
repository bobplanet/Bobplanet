package kr.bobplanet.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.IgnoreLoad;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Parent;

import java.util.Date;

/**
 * 사용자들이 메뉴에 매긴 comment
 * 
 * 
 * @author heonkyu.jin
 * @version 15. 11. 7.
 */
@Entity
public class Comment {
	/**
	 * 메뉴명.
	 */
    private String name;

    /**
     * 최종수정일
     */
    @IgnoreLoad
    protected Date updateDate;

    public Comment() {
    }

    public Comment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @OnSave
    public void onSave() {
        updateDate = new Date();
    }
}
