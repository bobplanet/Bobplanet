package kr.bobplanet.android.util;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.JsonFactory;

import java.io.IOException;

/**
 * JSON과 POJO 클래스를 상호변환하는 객체.
 * Google AppEngine과 주고받는 object를 캐싱하거나 어디엔가 문자열로 저장하고자 할 때 사용함.
 *
 * @author heonkyu.jin
 * @version 15. 10. 18
 */
public class EntityTranslator {
    private static final String TAG = EntityTranslator.class.getSimpleName();
    /**
     * 상호변환시 JSON factory.
     */
    private static final JsonFactory jsonFactory = new AndroidJsonFactory();

    /**
     * JSON을 객체로 변환.
     */
    public static <T> T parseEntity(Class<T> type, String json) {
        try {
            return jsonFactory.fromString(json, type);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 객체를 JSON으로 변환.
     * @param obj
     * @return
     */
    public static String toString(Object obj) {
        try {
            return jsonFactory.toString(obj);
        } catch (IOException e) {
            return null;
        }
    }
}
