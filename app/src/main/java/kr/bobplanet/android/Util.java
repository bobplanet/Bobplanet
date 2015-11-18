package kr.bobplanet.android;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.Size;
import android.view.View;

import java.util.List;

import kr.bobplanet.android.ui.BaseActivity;

/**
 * 공용 유틸리티 객체.
 *
 * @author heonkyu.jin
 * @version 15. 10. 18
 */
public class Util {

    public static String appendParticle(String str, String consonantCase, String vowelCase) {
        return str + (endsWithConsonant(str) ? consonantCase : vowelCase);
    }

    public static boolean endsWithConsonant(String str) {
        char c = str.charAt(str.length() - 1);
        int f = (c - 0xAC00) % 28;
        return f != 0;
    }

    public static String getQuotedString(String text) {
        return TextUtils.isEmpty(text) ? "" :
                new StringBuilder(text.length() + 2).append('"').append(text).append('"').toString();
    }

    public static Drawable getTintedDrawable(Context context, @DrawableRes int drawableResId,
                                         @ColorRes int colorResId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableResId);
        Drawable wrap = DrawableCompat.wrap(drawable);

        int color = ContextCompat.getColor(context, colorResId);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        return drawable;
    }

    public static BaseActivity getBaseActivity(View view) {
        Context context = view.getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (BaseActivity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public static Bitmap combineBitmaps(Bitmap bitmap1, Bitmap bitmap2) {
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
}
