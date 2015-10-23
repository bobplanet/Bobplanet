package kr.bobplanet.android;

import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.Date;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import kr.bobplanet.android.event.MeasureLogEvent;
import kr.bobplanet.android.event.NetworkExceptionEvent;
import kr.bobplanet.backend.bobplanetApi.BobplanetApi;
import kr.bobplanet.backend.bobplanetApi.model.DailyMenu;
import kr.bobplanet.backend.bobplanetApi.model.Item;
import kr.bobplanet.backend.bobplanetApi.model.Menu;
import kr.bobplanet.backend.bobplanetApi.model.User;
import kr.bobplanet.backend.bobplanetApi.model.Vote;

/**
 * Google AppEngine으로부터 가져오는 데이터를 LruCache를 이용해 한차례 caching하는 객체저장소.
 * ContentProvider를 만들자니 JSON serialization이 귀찮아서 JSON 문자열을 통째로 캐슁하도록 구현함.
 * <p/>
 * - 실제 Bobplanet 서버 API를 호출하는 로직은 이쪽으로 집중함
 * - Activity나 Fragment에서 AsyncTask 만들 필요가 없도록 AsyncTask는 이 클래스에 내장
 * - 싱글턴 관리는 MainApplication에 위임
 * - 내부적으로 Android support package의 LruCache를 이용하여 캐슁
 * - LruCache의 key는 '클래스이름:키값'이 되도록 함. (가령, "DailyMenu:2015-10-09")
 * - 캐쉬에서 객체를 찾아보고, 없으면 API를 호출해 가져온 뒤 캐쉬에 저장하는 반복코딩 줄이기 위해 generics 이용
 * - 네트웤에서 데이터를 가져올 경우 시간이 소요되므로 async 방식으로 데이터 조회
 * - 이미 JSON 문자열을 갖고있는 경우에는 <code>parseEntity()</code>를 이용해서 unserialize만 해도 됨
 * - callee는 OnEntityLoader를 전달하여, 데이터 로딩이 끝나면 UI업데이트 등을 수행해야 함
 *
 * @author hkjinlee
 * @version 2015. 10. 7
 */
public class ApiProxy implements Constants {
    private static final String TAG = ApiProxy.class.getSimpleName();

    /**
     * Google Cloud Endpoint에 의해 만들어지는 서버사이드 API의 wrapper.
     */
    private BobplanetApi api;

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
    private final LruCache<String, Pair<Long, String>> jsonCache;

    /**
     * 기본 생성자.
     */
    protected ApiProxy() {
        this.api = new BobplanetApi.Builder(
                AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                .setRootUrl(BACKEND_ROOT_URL).build();

        this.jsonCache = new LruCache<>(MAX_SIZE);
    }

    /**
     * 특정 일자의 메뉴리스트를 로드한다.
     * DayViewFragment에서 주로 사용.
     *
     * @param date     대상 날짜
     * @param listener 데이터로드 후처리를 담당할 listener
     */
    public void loadMenuOfDate(final String date, ApiResultListener<DailyMenu> listener) {
        new Builder<>(DailyMenu.class, () -> api.menuOfDate(date).execute(), "menuOfDate")
                .setResultListener(listener)
                .setCacheKey(date)
                .execute();
    }

    /**
     * 주어진 메뉴번호로 메뉴를 가져온다.
     * MenuDetailViewActivity와 GcmServices.MessageListener에서 주로 사용.
     *
     * @param id
     * @param listener 데이터로드 후처리를 담당할 listener
     */
    public void loadMenu(final long id, ApiResultListener<Menu> listener) {
        new Builder<>(Menu.class, () -> api.menu(id).execute(), "menu")
                .setResultListener(listener)
                .setCacheKey(id)
                .execute();
    }

    /**
     * 사용자 등록.
     *
     * @param user
     * @param listener
     */
    @DebugLog
    public void registerUser(final User user, ApiResultListener<User> listener) {
        new Builder<>(User.class, () -> api.registerUser(user).execute(), "registerUser")
                .setResultListener(listener)
                .execute();
    }

    /**
     * 사용자정보 업데이트.
     *
     * @param user
     */
    @DebugLog
    public void updateUser(final User user) {
        new Builder<>(Void.class, () -> api.updateUser(user).execute(), "updateUser")
                .execute();
    }

    /**
     * 메뉴 평가.
     *
     * @param userId
     * @param menu
     * @param score
     * @param listener
     */
    @DebugLog
    public void vote(final Long userId, final Menu menu, final int score, ApiResultListener<Item> listener) {
        Log.v(TAG, "score = " + score);

        new Builder<>(Item.class, () -> api.vote(userId, menu.getItem().getId(), menu.getId(), score).execute(), "vote")
                .setResultListener(listener)
                .setCacheKey(menu.getId())
                .setCacheWritable(true)
                .execute();
    }

    /**
     * 내 평가점수 가져오기.
     *
     * @param userId
     * @param menu
     * @param listener
     */
    @DebugLog
    public void myVote(final Long userId, final Menu menu, ApiResultListener<Vote> listener) {
        new Builder<>(Vote.class, () -> api.myVote(userId, menu.getItem().getId()).execute(), "myVote")
                .setResultListener(listener)
                .execute();
    }

    /**
     * 캐쉬 조회 및 네트웤API 호출을 담당하는 AsyncTask.
     * <p/>
     * - 생성자에는 서버API호출로직을 담은 RemoteApiLoader와, 데이터로딩 완료 후처리를 담당할 ApiResultListener 전달
     * - execute()에는 결과값의 Entity class와 서버 API에 전달할 key값을 패러미터로 전달
     * - doInBackground()에서 캐쉬 조회 및 네트웤API 호출, 캐쉬 업데이트 등을 수행
     * - onPostExecute()에서 listener의 onApiResult() 메소드를 호출하여 데이터로딩 후처리 진행
     */
    private class Builder<T> extends AsyncTask<Void, Void, T>{
        private Class<T> resultType;
        ApiExecutor<T> apiExecutor;
        ApiResultListener resultListener;
        String measureApiName;
        String cacheKey;
        boolean cacheReadable = false;
        boolean cacheWritable = false;

        Builder(Class<T> resultType, ApiExecutor<T> apiExecutor, String measureApiName) {
            this.resultType = resultType;
            this.apiExecutor = apiExecutor;
            this.measureApiName = measureApiName;
        }

        Builder<T> setResultListener(ApiResultListener<T> resultListener) {
            this.resultListener = resultListener;
            return this;
        }

        Builder<T> setCacheKey(Object key) {
            this.cacheKey = resultType.getSimpleName() + KEY_SEPARATOR + key;
            this.cacheReadable = true;
            this.cacheWritable = true;
            return this;
        }

        Builder<T> setCacheReadable(boolean cacheReadable) {
            this.cacheReadable = cacheReadable;
            return this;
        }

        Builder<T> setCacheWritable(boolean cacheWritable) {
            this.cacheWritable = cacheWritable;
            return this;
        }

        /**
         * 캐쉬에서 객체 조회하고 없거나 이미 expire되었으면 네트웤API를 호출해서 가져옴
         */
        @Override
        @DebugLog
        protected T doInBackground(Void... params) {
            try {
                long now = new Date().getTime();

                if (cacheReadable) {
                    Pair<Long, String> cachedObj = jsonCache.get(cacheKey);

                    if (cachedObj != null) {
                        Long timestamp = cachedObj.first;
                        String json = cachedObj.second;
                        if (now - timestamp < CACHE_EXPIRE_SECONDS * 1000 && json != null) {
                            Log.v(TAG, "Get cached entry for " + cacheKey);
                            return EntityParser.parseEntity(resultType, json);
                        }
                        Log.v(TAG, "Cache expired for " + cacheKey);
                    }
                }

                Log.v(TAG, "Fetching from network API");
                T result = apiExecutor.fromRemoteApi();
                Log.v(TAG, "result = " + result);

                MeasureLogEvent.measure(MeasureLogEvent.Metric.API_LATENCY,
                        measureApiName, new Date().getTime() - now).submit();

                if (cacheWritable) {
                    jsonCache.put(cacheKey, new Pair<>(now, EntityParser.toString(result)));
                }

                return result;
            } catch (IOException e) {
                Log.d(TAG, "error", e);
                EventBus.getDefault().post(new NetworkExceptionEvent("Network error", e));
                return null;
            }
        }

        @Override
        protected void onPostExecute(T entity) {
            if (resultListener != null) {
                resultListener.onApiResult(entity);
            }
        }
    }

    /**
     * 서버API 호출로직.
     * BobplanetApi 객체가 갖고있는 적절한 method를 호출.
     */
    private interface ApiExecutor<T> {
        T fromRemoteApi() throws IOException;
    }

    /**
     * 데이터 로딩이 끝난 뒤의 후처리 로직.
     */
    public interface ApiResultListener<T> {
        void onApiResult(T result);
    }
}