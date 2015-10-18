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
    /**
     * Bobplanet 자체 사용자번호
     */
    @Id
    Long id;

    /**
     * 서비스계정 종류 (현재는 "Google"만 가능함)
     */
    String accountType;

    /**
     * 해당 서비스 내에서의 회원번호
     */
    String accountId;

    /**
     * Android device 내에서 생성되는 InstanceID
     */
    String iid;

    /**
     * GCM 토큰
     */
    String gcmToken;

    /**
     * 프로파일 이미지
     */
    String image;

    /**
     * 이메일주소
     */
    String email;

    /**
     * 닉네임
     */
    String nickName;

    public User() {
    }

    public User(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIid() {
        return iid;
    }

    public void setIid(String iid) {
        this.iid = iid;
    }

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
