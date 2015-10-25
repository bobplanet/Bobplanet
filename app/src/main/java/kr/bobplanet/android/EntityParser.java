package kr.bobplanet.android;

import android.util.Log;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;

import java.io.IOException;

/**
 *
 *
 * @author heonkyu.jin
 * @version 15. 10. 18
 */
public class EntityParser {
    private static final String TAG = EntityParser.class.getSimpleName();
    /**
     * 캐쉬에서 꺼낸 JSON 문자열에서 객체를 꺼내는 parser를 만들 때 사용할 JSON factory.
     */
    private static final JsonFactory jsonFactory = new AndroidJsonFactory();

    /**
     * 이미 JSON 문자열을 갖고있는 경우(다른 클래스로부터 전달받는 등) 사용.
     * 캐쉬나 네트웤 조회없이 JSON unserialize만 함
     */
    public static <T> T parseEntity(Class<T> type, String json) {
        try {
            return jsonFactory.fromString(json, type);
        } catch (IOException e) {
            return null;
        }
    }

    public static String toString(Object obj) {
        try {
            return jsonFactory.toString(obj);
        } catch (IOException e) {
            return null;
        }
    }
}
