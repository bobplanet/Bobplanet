package kr.bobplanet.android;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;

import kr.bobplanet.android.ui.BaseActivity;

/**
 *
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
}
