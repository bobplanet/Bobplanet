package kr.bobplanet.backend.model;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreLoad;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Load;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Parent;

import java.util.Date;
import java.util.UUID;

/**
 * 사용자의 기기 객체.
 * 기기 레벨에서의 환경설정 정보도 이 객체를 이용해 저장.
 * 
 * @author heonkyu.jin
 * @version 15. 10. 24
 */
@Entity
public class UserDevice {
    @Id
    String id;

    /**
     * 이 기기를 소유한 유저.
     */
    @Parent
    @Load
    Ref<User> user;

    /**
     * Android ID. 기기를 공장초기화하기 전까지는 unique한 값임.
	 * 이 값을 기준으로 이 기기가 이미 등록된 기기인지 체크하므로 index 필요.
     */
    @Index
    String androidId;

    /**
     * Android device 내에서 생성되는 InstanceID
     */
    String iid;

    /**
     * GCM 토큰.
	 * 푸쉬메시지 발송시 이 값을 주소지로 하므로 index 필요
     */
    @Index
    String gcmToken;

    /**
     * 
     */
    @Index
    boolean lunchPushEnabled = true;

    /**
     *
     */
    @Index
    boolean dinnerPushEnabled = true;

    /**
     * 최종수정일시
     */
    @IgnoreLoad
    Date updateDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user != null ? user.get() : null;
    }

    public void setUser(User user) {
        this.user = Ref.create(user);
    }

    public String getAndroidId() {
        return androidId;
    }

    public String getGcmToken() {
        return gcmToken;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public boolean isLunchPushEnabled() {
        return lunchPushEnabled;
    }

    public void setLunchPushEnabled(boolean lunchPushEnabled) {
        this.lunchPushEnabled = lunchPushEnabled;
    }

    public boolean isDinnerPushEnabled() {
        return dinnerPushEnabled;
    }

    public void setDinnerPushEnabled(boolean dinnerPushEnabled) {
        this.dinnerPushEnabled = dinnerPushEnabled;
    }

    @OnSave
    public void onSave() {
        this.updateDate = new Date();
    }

    @Override
    public String toString() {
        User user = getUser();
        return String.format("{ id = %s, user = %s, androidId = %s",
                id, user == null ? "null" : user.toString(), androidId);
    }
}
