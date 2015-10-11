package kr.bobplanet.android.gcm;

/**
 * Google Cloud Messaging 처리 과정에서 필요한 이벤트 객체.
 * 등록성공 메시지와 등록실패 메시지를 나르는 용도로 사용함.
 *
 * @author heonkyu.jin
 * @version 2015. 10. 3
 */
public class GcmEvent {
    public static final String REGISTER_SUCCESS = "REGISTER_SUCCESS";
    public static final String REGISTER_FAILURE = "REGISTER_FAILURE";

    private final String type;

    public GcmEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
