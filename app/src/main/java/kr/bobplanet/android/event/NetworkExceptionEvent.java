package kr.bobplanet.android.event;

/**
 * Created by hkjinlee on 2015. 10. 4..
 */
public class NetworkExceptionEvent {
    private String message;
    private Exception exception;

    public NetworkExceptionEvent(String message, Exception exception) {
        this.message = message;
        this.exception = exception;
    }

    public String getMessage() {
        return message;
    }

    public Exception getException() {
        return exception;
    }
}
