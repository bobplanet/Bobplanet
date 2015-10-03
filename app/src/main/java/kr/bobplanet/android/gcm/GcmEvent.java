package kr.bobplanet.android.gcm;

/**
 * Created by hkjinlee on 2015. 10. 3..
 */
public class GcmEvent {
    public static final String REGISTER = "REGISTER";
    public static final String REGISTER_SUCCESS = "REGISTER_SUCCESS";
    public static final String REGISTER_FAILURE = "REGISTER_FAILURE";

    private String type;

    public GcmEvent(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
