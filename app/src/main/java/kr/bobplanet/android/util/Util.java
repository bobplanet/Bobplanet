package kr.bobplanet.android.util;

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
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.TextUtils;
import android.util.Pair;
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

    /**
     * 주어진 문자열이 자음으로 끝나는지에 따라 '이/가', '은/는' 등의 조사를 붙여준다
     *
     * @param str
     * @param consonantCase
     * @param vowelCase
     * @return
     */
    public static String appendParticle(String str, String consonantCase, String vowelCase) {
        return str + (endsWithConsonant(str) ? consonantCase : vowelCase);
    }

    /**
     * 주어진 문자열이 자음으로 끝나는지 알려줌.
     *
     * @param str
     * @return
     */
    public static boolean endsWithConsonant(String str) {
        char c = str.charAt(str.length() - 1);
        int f = (c - 0xAC00) % 28;
        return f != 0;
    }

    /**
     * 문자열 앞뒤에 "나 ' 등을 넣어 인용처리.
     *
     * @param text
     * @return
     */
    public static String getQuotedString(String text) {
        return TextUtils.isEmpty(text) ? "" :
                new StringBuilder(text.length() + 2).append('"').append(text).append('"').toString();
    }

    /**
     * Bundle에서 문자열을 꺼내되, 해당 key가 없을 경우 default 문자열을 리턴.
     *
     * @param data
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getDefaultString(Bundle data, String key, String defaultValue) {
        String value = data.getString(key);
        return (value == null || value.length() == 0) ?
                defaultValue : value;
    }

    /**
     * 주어진 View의 BaseActivity instance를 구한다.
     *
     * @param view
     * @return
     */
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
