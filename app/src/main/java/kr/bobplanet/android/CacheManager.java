package kr.bobplanet.android;

import android.support.v4.util.LruCache;
import android.support.v4.util.Pair;
import android.util.Log;

import java.util.Date;

/**
 * 서버 API를 호출해서 받아온 결과값 객체를 관리하는 Cache.
 * 원래 ApiProxy의 일부였으나 분리함. (아직 깔끔하게 분리되지는 않아 리팩토링 필요)
 *
 * @author heonkyu.jin
 * @version 2015-11-13
 */
public class CacheManager {
    private static final String TAG = CacheManager.class.getSimpleName();

    /**
     * 캐쉬 최대 사이즈. 현재는 1MB.
     */
    private static final int MAX_SIZE = 1024 * 1024;

    /**
     * 캐쉬 유효기간. default는 2분.
     */
    protected static final int DEFAULT_LIFETIME_SECONDS = 2 * 60 * 1000;

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
    protected CacheManager() {
    }

    protected <T> T getCachedEntity(Class<T> resultType, String key, int cacheLifetime) {
        long now = new Date().getTime();

        Pair<Long, String> cachedObj = jsonCache.get(key);

        if (cachedObj != null) {
            Long timestamp = cachedObj.first;
            String json = cachedObj.second;
            if (now - timestamp < cacheLifetime && json != null) {
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
