package kr.bobplanet.android.event;

/**
 *
 *
 * @author heonkyu.jin
 * @version 15. 10. 30..
 */
public class InitCompleteEvent {
    public final Class component;

    public InitCompleteEvent(Class component) {
        this.component = component;
    }
}
