package kr.bobplanet.android;

import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import kr.bobplanet.android.event.NetworkExceptionEvent;
import kr.bobplanet.backend.bobplanetApi.BobplanetApi;
import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;

/**
 * Created by hkjinlee on 2015. 10. 7..
 */
public class EntityVault implements AppConstants {
    private static final String TAG = EntityVault.class.getSimpleName();
    private static EntityVault instance;
    private static final int MAX_SIZE = 1 * 1024 * 1024;
    private static final String INVALID_KEY = "INVALID_KEY";
    private static final char KEY_SEPARATOR = '_';

    private LruCache<String, String> jsonCache;
    private JsonFactory jsonFactory;
    private BobplanetApi api;

    private EntityVault() {
        this.jsonFactory = new AndroidJsonFactory();

        this.api = new BobplanetApi.Builder(
                AndroidHttp.newCompatibleTransport(), jsonFactory, null)
                .setRootUrl(BACKEND_ROOT_URL).build();

        this.jsonCache = new LruCache<String, String>(MAX_SIZE);
    }

    public static EntityVault getInstance() {
        if (instance == null) {
            instance = new EntityVault();
        }
        return instance;
    }

    public void loadMenuOfDate(final String date, OnEntityLoadListener listener) {
        RemoteApiLoader<DailyMenu> remote = new RemoteApiLoader<DailyMenu>() {
            @Override
            public DailyMenu fromRemoteApi() throws IOException {
                return api.menuOfDate(date).execute();
            }
        };

        EntityLoader<DailyMenu> loader = new EntityLoader<DailyMenu>(remote, listener);
        loader.execute(new Pair<Class, Object>(DailyMenu.class, date));
    }

    private class EntityLoader<Entity> extends AsyncTask<Pair<Class, Object>, Void, Entity> {
        private RemoteApiLoader<Entity> remote;
        private OnEntityLoadListener<Entity> listener;

        EntityLoader(RemoteApiLoader<Entity> remote, OnEntityLoadListener<Entity> listener) {
            this.remote = remote;
            this.listener = listener;
        }

        @Override
        protected Entity doInBackground(Pair<Class, Object>... params) {
            try {
                Class type = params[0].first;
                Object key = params[0].second;

                String cacheKey = _getKey(type, key);
                String cachedJson = jsonCache.get(cacheKey);

                if (cachedJson != null) {
                    Log.d(TAG, "Get cached entry for " + key);
                    JsonParser parser = jsonFactory.createJsonParser(cachedJson);
                    return (Entity) parser.parse(type);
                } else {
                    Log.d(TAG, "No cache found for " + key + ". Fetching from network API");
                    Entity result = remote.fromRemoteApi();

                    jsonCache.put(cacheKey, result.toString());

                    return result;
                }
            } catch (IOException e) {
                Log.d(TAG, "error", e);
                EventBus.getDefault().post(new NetworkExceptionEvent("Daily menu fetch error", e));
                return null;
            }
        }

        @Override
        protected void onPostExecute(Entity entity) {
            listener.onEntityLoad(entity);
        }
    }

    private static String _getKey(Class type, Object key) {
        return new StringBuilder().append(type.getSimpleName().toLowerCase()).append(KEY_SEPARATOR).append(key).toString();
    }

    private interface RemoteApiLoader<T> {
        T fromRemoteApi() throws IOException;
    }

    public static interface OnEntityLoadListener<Entity> {
        public void onEntityLoad(Entity result);
    }
}
