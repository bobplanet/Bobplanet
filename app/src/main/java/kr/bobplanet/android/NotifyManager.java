package kr.bobplanet.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import hugo.weaving.DebugLog;
import kr.bobplanet.android.ui.DayActivity;
import kr.bobplanet.android.ui.MenuActivity;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 * @author heonkyu.jin
 * @version 2015. 11. 16
 */
public class NotifyManager implements Constants {
    private static final String TAG = NotifyManager.class.getSimpleName();

    private static final Uri DEFAULT_RINGTONE = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private Context context;

    private Bundle bundle;

    public NotifyManager(Context context) {
        this.context = context;
    }

    /**
     * 실제 Notification을 생성/등록한다.
     *
     * @param bundle
     */
    public void requestNextMenuNotification(Bundle bundle) {
        this.bundle = bundle;

        List<String> imageUrls = Lists.newArrayList(bundle.getString("menu1.image"));
        if (!TextUtils.isEmpty(bundle.getString("menu2.image"))) {
            imageUrls.add(bundle.getString("menu2.image"));
        }

        ImageBatchLoader imageBatchLoader = new ImageBatchLoader(imageUrls);
        EventBus.getDefault().register(imageBatchLoader);
        EventBus.getDefault().post(new BatchRequestEvent());
    }

    private void registerNextMenuNotification(int menuSize, ImageBatchLoader imageBatchLoader) {
        Intent intent = new Intent(context, DayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        String title = getDefaultString(bundle, "title", context.getString(R.string.notification_title));
        String text = getDefaultString(bundle, "text", context.getString(R.string.notification_meessage));
        NotificationCompat.Style style = new NotificationCompat.BigPictureStyle()
                .bigPicture(imageBatchLoader.getBitmap())
                .setSummaryText(text);

        PendingIntent pending = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        registerNotification(title, text, style, pending);
    }

    public void registerNotification(String title, String text,
                                     @Nullable NotificationCompat.Style style,
                                     @Nullable PendingIntent pendingIntent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(text)
                .setStyle(style)
                .setAutoCancel(true)
                .setSound(DEFAULT_RINGTONE);

        if (pendingIntent != null) builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, builder.build());
    }

    /**
     * 서버에서 전달받은 메시지 본문을 Bundle에서 꺼내는 유틸리티 함수.
     *
     * @param data
     * @param key
     * @param defaultValue
     * @return
     */
    private String getDefaultString(Bundle data, String key, String defaultValue) {
        String value = data.getString(key);
        return (value == null || value.length() == 0) ?
                defaultValue : value;
    }

    private class ImageBatchLoader implements ImageLoader.ImageListener {
        private List<String> imageUrls;
        private Map<String, Bitmap> imageBitmaps = new HashMap<>();

        @DebugLog
        private ImageBatchLoader(List<String> imageUrls) {
            this.imageUrls = imageUrls;
        }

        @SuppressWarnings("unused")
        public void onEventMainThread(BatchRequestEvent event) {
            for (String imageUrl : imageUrls) {
                App.getImageLoader().get(imageUrl, this);
            }
        }

        protected Bitmap getBitmap() {
            if (imageBitmaps.size() == 1) {
                return imageBitmaps.get(0);
            } else {
                return Util.combineBitmaps(
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
        public void onResponse(com.android.volley.toolbox.ImageLoader.ImageContainer response, boolean isImmediate) {
            Bitmap bitmap = response.getBitmap();
            if (bitmap != null) {
                imageBitmaps.put(response.getRequestUrl(), bitmap);
                if (imageUrls.size() == imageUrls.size()) {
                    registerNextMenuNotification(imageBitmaps.size(), this);
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
    }

    private class BatchRequestEvent {
        private BatchRequestEvent() {}
    }
}
