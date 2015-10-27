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

/**
 * 사용자의 기기 객체.
 * 기기 레벨에서의 환경설정 정보도 이 객체를 이용해 저장.
 * 
 * @author heonkyu.jin
 * @version 15. 10. 24
 */
@Entity
public class UserDevice {
    /**
     * 기기번호. UUID. 클라이언트에서 생성.
     */
    @Id
    String id;

    /**
     * 이 기기를 소유한 유저. 로그인하기 전까지는 null.
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
     * 점심메뉴 알림메시지 수신여부
     */
    @Index
    boolean lunchPushEnabled = true;

    /**
     * 저녁메뉴 알림메시지 수신여부
     */
    @Index
    boolean dinnerPushEnabled = true;

    /**
     * 최종수정일시
     */
    @IgnoreLoad
    Date updateDate;

    public UserDevice() { }

    public UserDevice(String id) {
        this.id = id;
    }

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
        this.user = user != null ? Ref.create(user) : null;
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
        return String.format("{ id = %s, user = %s, gcmToken = %s, androidId = %s",
                id, user == null ? "null" : user.toString(), gcmToken, androidId);
    }
}
