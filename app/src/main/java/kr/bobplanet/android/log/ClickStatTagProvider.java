package kr.bobplanet.android.log;

import android.support.annotation.Keep;

import com.google.android.gms.tagmanager.CustomTagProvider;

import java.util.Map;

/**
 * 클릭통계 tag를 전송하는 Custom Tag Provider.
 *
 */
@Keep
@SuppressWarnings("unused")
public class ClickStatTagProvider implements CustomTagProvider {
    private static final String TAG = ClickStatTagProvider.class.getSimpleName();

    @Override
    public void execute(Map<String, Object> map) {
        synchronized (ClickStatTagProvider.class) {
            android.util.Log.d(TAG, map.toString());
        }
    }
}
