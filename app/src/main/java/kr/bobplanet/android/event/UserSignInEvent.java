package kr.bobplanet.android.event;

/**
 * 구글/페이스북 로그인이 완료되었을 때 UserManager가 발생시키는 이벤트.
 * 로그인 완료시 화면전환 등을 처리하고자 할 때 이 이벤트를 listen하면 됨.
 *
 * @author heonkyu.jin
 * @version 15. 10. 18
 */
public class UserSignInEvent {
    public String accountType;

    public UserSignInEvent(String accountType) {
        this.accountType = accountType;
    }
}
