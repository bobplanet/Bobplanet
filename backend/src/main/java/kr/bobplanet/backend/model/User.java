package kr.bobplanet.backend.model;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.IgnoreLoad;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.OnSave;
import com.googlecode.objectify.annotation.Unindex;

import java.util.Date;

/**
 * 이용자 객체.
 * Google이나 Facebook 로그인을 할 때 생성된다.
 *
 * @author heonkyu.jin
 * @version 2015. 10. 3
 */
@Entity
@Index
public class User {
    /**
     * Bobplanet 자체 사용자번호. UUID. 클라이언트에서 생성됨.
     */
    @Id
    String id;

    /**
     * 서비스계정 종류 (현재는 "Google"만 가능함)
     */
    String accountType;

    /**
     * 해당 서비스 내에서의 회원번호
     */
    String accountId;

    /**
     * 프로파일 이미지
     */
    @Unindex
    String image;

    /**
     * 닉네임
     */
    String nickName;

    /**
     * 최종수정일시
     */
    @IgnoreLoad
    Date updateDate;

    public User() { }

    public User(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @OnSave
    public void onSave() {
        this.updateDate = new Date();
    }

    @Override
    public String toString() {
        return String.format("{ id = %s, accountId = %s }", id, accountId);
    }
}
