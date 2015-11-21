package kr.bobplanet.android.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import kr.bobplanet.android.App;

/**
 * Volley의 ImageLoader를 이용해 여러 장의 이미지를 로딩하고 합쳐주는 유틸리티 클래스.
 * MainThread에서 이미지 요청을 하기 위해 내부적으로 EventBus를 이용함. (좋은 디자인은 아닌 듯)
 *
 * @author heonkyu.jin
 * @version 15. 11. 21
 */
public class BitmapLoader implements ImageLoader.ImageListener {
    private static final String TAG = BitmapLoader.class.getSimpleName();

    /**
     * 로드할 이미지의 URL을 담고 있는 List
     */
    private List<String> imageUrls;

    /**
     * 로드된 이미지를 담을 Map. key는 URL임
     */
    private Map<String, Bitmap> imageBitmaps = new HashMap<>();

    /**
     * 이미지 로딩이 완료될 때 호출될 Listener
     */
    private OnLoadListener listener;

    public BitmapLoader(List<String> imageUrls, OnLoadListener listener) {
        this.imageUrls = imageUrls;
        this.listener = listener;

        EventBus.getDefault().register(this);
    }

    /**
     * 이미지 로딩을 요청
     */
    public void execute() {
        EventBus.getDefault().post(new RequestEvent());
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(RequestEvent event) {
        for (String imageUrl : imageUrls) {
            App.getImageLoader().get(imageUrl, this);
        }
    }

    /**
     * 로딩된 bitmap을 돌려준다. 2장인 경우에는 1장으로 합쳐줌.
     *
     * @return
     */
    public Bitmap getBitmap() {
        if (imageBitmaps.size() == 1) {
            return imageBitmaps.get(imageUrls.get(0));
        } else {
            return combineBitmaps(
                    imageBitmaps.get(imageUrls.get(0)),
                    imageBitmaps.get(imageUrls.get(1)));
        }
    }

    /**
     * 이미지가 로딩되었을 때 Volley에서 호출해주는 callback.
     * 정확하게는 모르겠으나 두 번 호출되는 듯함. (첫번째 호출때는 image가 null이다.)
     *
     * @param response
     * @param isImmediate
     */
    @Override
    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
        Bitmap bitmap = response.getBitmap();
        if (bitmap != null) {
            imageBitmaps.put(response.getRequestUrl(), bitmap);
            if (imageUrls.size() == imageUrls.size()) {
                listener.onImageLoaded(this);
            }
        }
    }

    /**
     * Volley에서 이미지를 로딩하지 못했을 때 호출하는 callback.
     * 지금까지는 한번도 호출된 적이 없음.
     *
     * @param error
     */
    @Override
    public void onErrorResponse(VolleyError error) {
        Log.w(TAG, "Image fetch error", error.getCause());
    }

    /**
     * 두 장의 Bitmap을 하나로 합침.
     *
     * @param bitmap1
     * @param bitmap2
     * @return
     */
    protected static Bitmap combineBitmaps(Bitmap bitmap1, Bitmap bitmap2) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        Bitmap out = Bitmap.createBitmap(width, height / 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(out);
        canvas.drawBitmap(bitmap1, null,
                new Rect(0, 0, width / 2, height / 2), null);
        canvas.drawBitmap(bitmap2, null,
                new Rect(width / 2, 0, width, height / 2), null);
        return out;
    }

    /**
     * 이미지 로딩이 완료되었을 때 호출될 Listener
     */
    public interface OnLoadListener {
        void onImageLoaded(BitmapLoader instance);
    }

    /**
     * 이미지 로딩을 요청하는 이벤트.
     */
    private class RequestEvent {
        private RequestEvent() {}
    }
}
