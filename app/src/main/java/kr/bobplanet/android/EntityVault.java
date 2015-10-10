package kr.bobplanet.android;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonParser;

import java.io.IOException;
import java.util.Date;

import de.greenrobot.event.EventBus;
import kr.bobplanet.android.event.NetworkExceptionEvent;
import kr.bobplanet.backend.bobplanetApi.BobplanetApi;
import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 * Google AppEngine으로부터 가져오는 데이터를 LruCache를 이용해 한차례 caching하는 객체저장소.
 * ContentProvider를 만들자니 JSON serialization이 귀찮아서 JSON 문자열을 통째로 캐슁하도록 구현함.
 *
 * - 실제 Bobplanet 서버 API를 호출하는 로직은 이쪽으로 집중함
 * - Activity나 Fragment에서 AsyncTask 만들 필요가 없도록 AsyncTask는 이 클래스에 내장
 * - 싱글턴 관리는 MainApplication에 위임
 * - 내부적으로 Android support package의 LruCache를 이용하여 캐슁
 * - LruCache의 key는 '클래스이름:키값'이 되도록 함. (가령, "DailyMenu:2015-10-09")
 * - 캐쉬에서 객체를 찾아보고, 없으면 API를 호출해 가져온 뒤 캐쉬에 저장하는 반복코딩 줄이기 위해 generics 이용
 * - 네트웤에서 데이터를 가져올 경우 시간이 소요되므로 async 방식으로 데이터 조회
 * - 이미 JSON 문자열을 갖고있는 경우에는 <code>getEntity()</code>를 이용해서 unserialize만 해도 됨
 * - callee는 OnEntityLoader를 전달하여, 데이터 로딩이 끝나면 UI업데이트 등을 수행해야 함
 *
 * @author hkjinlee on 2015. 10. 7
 */
public class EntityVault implements AppConstants {
    private static final String TAG = EntityVault.class.getSimpleName();
	
	/**
	 * Google Cloud Endpoint에 의해 만들어지는 서버사이드 API의 wrapper.
	 */
    private BobplanetApi api;

	/**
	 * 캐쉬 최대 사이즈. 현재는 1MB.
	 */
    private static final int MAX_SIZE = 1024 * 1024;

    /**
     *
     */
    private static final int CACHE_EXPIRE_SECONDS = 2 * 60;
	
	/**
	 * LruCache 키값에서 클래스명과 ID를 연결하는 separator
	 */
    private static final char KEY_SEPARATOR = ':';

	/**
	 * JSON 문자열을 저장할 LruCache.
	 */
    private LruCache<String, Pair<Long, String>> jsonCache;
	
	/**
	 * 캐쉬에서 꺼낸 JSON 문자열을 unserialize할 때 사용할 JSON factory.
	 */
    private JsonFactory jsonFactory;
	
	/**
	 * 기본 생성자.
	 */
    protected EntityVault() {
        this.jsonFactory = new AndroidJsonFactory();

        this.api = new BobplanetApi.Builder(
                AndroidHttp.newCompatibleTransport(), jsonFactory, null)
                .setRootUrl(BACKEND_ROOT_URL).build();

        this.jsonCache = new LruCache<>(MAX_SIZE);
    }

	/**
	 * 특정 일자의 메뉴리스트를 로드한다. 로딩이 끝나면 listener의 onEntityLoad() 메소드를 호출.
	 */
    public void loadMenuOfDate(final String date, OnEntityLoadListener<DailyMenu> listener) {
        RemoteApiLoader<DailyMenu> remote = new RemoteApiLoader<DailyMenu>() {
            @Override
            public DailyMenu fromRemoteApi() throws IOException {
                return api.menuOfDate(date).execute();
            }
        };

        EntityLoader<DailyMenu> loader = new EntityLoader<>(remote, listener);
        loader.execute(new Pair<Class<DailyMenu>, Object>(DailyMenu.class, date));
    }

    public void loadMenu(final long id, OnEntityLoadListener<Menu> listener) {
        RemoteApiLoader<Menu> remote = new RemoteApiLoader<Menu>() {
            @Override
            public Menu fromRemoteApi() throws IOException {
                return api.menu(id).execute();
            }
        };

        EntityLoader<Menu> loader = new EntityLoader<>(remote, listener);
        loader.execute(new Pair<Class<Menu>, Object>(Menu.class, id));
    }

	/**
	 * 이미 JSON 문자열을 갖고있는 경우(다른 클래스로부터 전달받는 등) 사용.
	 * 캐쉬나 네트웤 조회없이 JSON unserialize만 함
	 */
    protected <Entity> Entity getEntity(Class<Entity> type, String json) {
        try {
            JsonParser parser = jsonFactory.createJsonParser(json);
            return parser.parse(type);
        } catch (IOException e) {
            return null;
        }
    }

	/**
	 * 캐쉬 조회 및 네트웤API 호출을 담당하는 AsyncTask.
	 * 
	 * - 생성자에는 서버API호출로직을 담은 RemoteApiLoader와, 데이터로딩 완료 후처리를 담당할 OnEntityLoadListener 전달
	 * - execute()에는 결과값의 Entity class와 서버 API에 전달할 key값을 패러미터로 전달
	 * - doInBackground()에서 캐쉬 조회 및 네트웤API 호출, 캐쉬 업데이트 등을 수행
	 * - onPostExecute()에서 listener의 onEntityLoad() 메소드를 호출하여 데이터로딩 후처리 진행
	 */
    private class EntityLoader<Entity> extends AsyncTask<Pair<Class<Entity>, Object>, Void, Entity> {
        private RemoteApiLoader<Entity> remote;
        private OnEntityLoadListener<Entity> listener;

        /**
         * 생성자.
		 *
         * @param remote BobplanetApi 호출로직
         * @param listener 데이터 로딩 뒤 후처리로직
         */
        EntityLoader(RemoteApiLoader<Entity> remote, @Nullable OnEntityLoadListener<Entity> listener) {
            this.remote = remote;
            this.listener = listener;
        }

		/**
		 * 캐쉬에서 객체 조회하고 없으면 네트웤API를 호출해서 가져옴
		 */
        @Override
        protected Entity doInBackground(Pair<Class<Entity>, Object>... params) {
            try {
                Class<Entity> type = params[0].first;
                Object key = params[0].second;

                String cacheKey = _getKey(type, key);
                Pair<Long, String> cachedObj = jsonCache.get(cacheKey);
                Long now = new Date().getTime();

                if (cachedObj != null) {
                    Long timestamp = cachedObj.first;
                    String json = cachedObj.second;
                    if (now - timestamp < CACHE_EXPIRE_SECONDS * 1000 && json != null) {
                        Log.d(TAG, "Get cached entry for " + key);
						return getEntity(type, json);
                    }
                }

                Log.d(TAG, "No cache or expired for " + key + ". Fetching from network API");
                Entity result = remote.fromRemoteApi();

                jsonCache.put(cacheKey, new Pair<>(now, result.toString()));

                return result;
            } catch (IOException e) {
                Log.d(TAG, "error", e);
                EventBus.getDefault().post(new NetworkExceptionEvent("Daily menu fetch error", e));
                return null;
            }
        }

        @Override
        protected void onPostExecute(Entity entity) {
            if (listener != null) {
                listener.onEntityLoad(entity);
            }
        }
    }

	/**
	 * 내부 LruCache에 객체를 저장할 key값 생성.
	 */
    private static String _getKey(Class type, Object key) {
        return type.getSimpleName().toLowerCase() + KEY_SEPARATOR + key;
    }

	/**
	 * 서버API 호출로직.
	 * BobplanetApi 객체가 갖고있는 적절한 method를 호출.
	 */
    private interface RemoteApiLoader<T> {
        T fromRemoteApi() throws IOException;
    }

	/**
	 * 데이터 로딩이 끝난 뒤의 후처리 로직.
	 */
    public interface OnEntityLoadListener<Entity> {
        void onEntityLoad(Entity result);
    }
}
