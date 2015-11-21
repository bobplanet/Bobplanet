package kr.bobplanet.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.common.collect.Lists;

import java.util.List;

import kr.bobplanet.android.ui.DayActivity;
import kr.bobplanet.android.util.BitmapLoader;
import kr.bobplanet.android.util.Util;

/**
 * Notification Message를 관리하는 객체.
 *
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
     * 다음 메뉴 알림메시지를 status bar에 등록.
     *
     * @param bundle
     */
    public void requestNextMenuNotification(Bundle bundle) {
        this.bundle = bundle;

        List<String> imageUrls = Lists.newArrayList(bundle.getString("menu1.image"));
        if (!TextUtils.isEmpty(bundle.getString("menu2.image"))) {
            imageUrls.add(bundle.getString("menu2.image"));
        }

        BitmapLoader bitmapLoader = new BitmapLoader(imageUrls, instance -> {
            Intent intent = new Intent(context, DayActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            String title = Util.getDefaultString(bundle, "title", context.getString(R.string.noti_default_title));
            String text = Util.getDefaultString(bundle, "text", context.getString(R.string.noti_default_text));
            NotificationCompat.Style style = new NotificationCompat.BigPictureStyle()
                    .bigPicture(instance.getBitmap())
                    .setSummaryText(text);

            PendingIntent pending = PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            registerNotification(title, text, style, pending);
        });
        bitmapLoader.execute();
    }

    /**
     * 알림메시지를 등록하는 일반 method.
     *
     * @param title
     * @param text
     * @param style
     * @param pendingIntent
     */
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
}
