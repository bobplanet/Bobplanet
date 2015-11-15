package kr.bobplanet.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import kr.bobplanet.android.ui.MenuActivity;
import kr.bobplanet.backend.bobplanetApi.model.Menu;

/**
 * @author heonkyu.jin
 * @version 2015. 11. 16
 */
public class NotifyManager implements Constants, ImageLoader.ImageListener {
    private static final String TAG = NotifyManager.class.getSimpleName();

    private static final Uri DEFAULT_RINGTONE = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    private Context context;

    private Menu menu;
    private Bundle bundle;

    public NotifyManager(Context context) {
        this.context = context;
    }

    /**
     * 실제 Notification을 생성/등록한다.
     *
     * @param menuId
     * @param bundle
     */
    public void requestNextMenuNotification(long menuId, Bundle bundle) {
        this.bundle = bundle;

        App.getApiProxy().loadMenu(menuId, result -> {
            this.menu = result;
            App.getImageLoader().get(result.getItem().getThumbnail(), NotifyManager.this);
        });
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
            registerNextMenuNotification(menu, bitmap);
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
        registerNextMenuNotification(menu, null);
    }

    private void registerNextMenuNotification(Menu menu, Bitmap bitmap) {
        Intent intent = new Intent(context, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(KEY_MENU, menu.toString());

        String title = getDefaultString(bundle, "title", menu.getDate() + ": " + menu.getItem().getName());
        String message = getDefaultString(bundle, "message", "Get it while you can");
        String detail = bundle.getString("detail");

        PendingIntent pending = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // 이미지가 있으면 BigPicture 스타일, 없으면 BigText 스타일.
        NotificationCompat.Style style = bitmap != null ?
                new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap).setBigContentTitle(title)
                        .setSummaryText(message) :
                new NotificationCompat.BigTextStyle()
                        .setBigContentTitle(title).setSummaryText(detail);

        registerNotification(title, message, pending, style);
    }

    public void registerNotification(String title, String message,
                                     @Nullable PendingIntent pendingIntent,
                                     @Nullable NotificationCompat.Style style) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(DEFAULT_RINGTONE);

        if (pendingIntent != null) builder.setContentIntent(pendingIntent);
        if (style != null) builder.setStyle(style);

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
}
