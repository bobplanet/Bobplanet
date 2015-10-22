package kr.bobplanet.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.OnSave;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 푸시메시지 객체.
 *
 * @author heonkyu.jin
 * @version 15. 10. 22
 */
@Entity(name="Message")
abstract public class BaseMessage {
    public static final String TYPE_NEXT_MENU = "TYPE_NEXT_MENU";
    public static final String TYPE_NEED_UPGRADE = "TYPE_NEED_UPGRADE";
    public static final String TYPE_SURVEY = "TYPE_SURVEY";

    /**
     *
     */
    @Id
    Long id;

    /**
     * 메시지 종류.
     */
    private String type;

    /**
     * 메시지 제목. Notification 상단에 노출되는 텍스트.
     */
    private String title;

    /**
     *
     */
    private Map<String, String> extras = new HashMap<>();

    /**
     *
     */
    private int numRecipients;

    /**
     *
     */
    private int numErrors;

    /**
     *
     */
    private Date sentDate;

    protected BaseMessage(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExtra(String name) {
        return extras.get(name);
    }

    public void putExtra(String name, String value) {
        extras.put(name, value);
    }

    public void setNumRecipients(int numRecipients) {
        this.numRecipients = numRecipients;
    }

    public void setNumErrors(int numErrors) {
        this.numErrors = numErrors;
    }

    @OnSave
    public void onSave() {
        this.sentDate = new Date();
    }
}
