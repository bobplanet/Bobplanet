package kr.bobplanet.android.util;

import android.support.v4.util.Pair;

import com.google.common.collect.Iterables;

import java.util.Calendar;

import kr.bobplanet.android.beacon.BeaconCriteria;

/**
 * 시간계산을 캡슐화한 객체.
 *
 * @author heonkyu.jin
 * @version 15. 11. 22..
 */
public class Time implements Comparable<Time> {
    private long timestamp;

    public static Time now() {
        return new Time(System.currentTimeMillis());
    }

    public Time(long timestamp) {
        this.timestamp = timestamp;
    }

    public Time(int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        timestamp = calendar.getTimeInMillis();
    }

    public boolean between(Pair<Time, Time> interval) {
        return compareTo(interval.first) > 0 && compareTo(interval.second) < 0;
    }

    public long differenceInSeconds(Time from) {
        return (from.timestamp - timestamp) / 1000;
    }

    @Override
    public int compareTo(Time another) {
        return (int) (timestamp - another.timestamp);
    }
}
