package kr.bobplanet.android;

/**
 *
 *
 * @author heonkyu.jin
 * @version 15. 10. 18
 */
public class Util {
    public static boolean endsWithConsonant(String str) {
        char c = str.charAt(str.length() - 1);
        int f = (c - 0xAC00) % 28;
        return f != 0;
    }
}
