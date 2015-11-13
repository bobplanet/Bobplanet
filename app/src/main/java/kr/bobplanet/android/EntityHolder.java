package kr.bobplanet.android;

import android.support.v4.util.LruCache;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.Date;

/**
 *
 *
 * @author heonkyu.jin
 * @version 2015-11-13
 */
public class EntityHolder {
    private static final String TAG = EntityHolder.class.getSimpleName();

    /**
     * 캐쉬 최대 사이즈. 현재는 1MB.
     */
    private static final int MAX_SIZE = 1024 * 1024;

    /**
     * 캐쉬 유효기간. 현재는 2분.
     */
    private static final int CACHE_EXPIRE_SECONDS = 2 * 60;

    /**
     * LruCache 키값에서 클래스명과 ID를 연결하는 separator
     */
    private static final char KEY_SEPARATOR = ':';

    /**
     * JSON 문자열을 저장할 LruCache.
     */
    private final LruCache<String, Pair<Long, String>> jsonCache = new LruCache<>(MAX_SIZE);

    /**
     *
     */
    protected EntityHolder() {
    }

    protected <T> T getCachedEntity(Class<T> resultType, String key) {
        long now = new Date().getTime();

        Pair<Long, String> cachedObj = jsonCache.get(key);

        if (cachedObj != null) {
            Long timestamp = cachedObj.first;
            String json = cachedObj.second;
            if (now - timestamp < CACHE_EXPIRE_SECONDS * 1000 && json != null) {
                Log.v(TAG, "Get cached entry for " + key);
                return EntityTranslator.parseEntity(resultType, json);
            }
            Log.v(TAG, "Cache expired for " + key);
        }

        return null;
    }

    protected void putCachedEntity(String key, Object entity) {
        long now = new Date().getTime();

        jsonCache.put(key, new Pair<>(now, EntityTranslator.toString(entity)));
    }
}
