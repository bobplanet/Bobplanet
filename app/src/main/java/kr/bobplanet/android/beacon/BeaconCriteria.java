package kr.bobplanet.android.beacon;

import android.support.v4.util.Pair;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

import kr.bobplanet.android.util.Time;

/**
 * 비콘을 이용해 식당 안인지/밖인지 등을 판단하기 위한 로직 객체.
 *
 * - 식사시간에는 5분에 한 번, 아닌 때는 10분에 한 번씩 스캔함
 * - 식사시간에 비콘 근처에서 3분 이상 머무른 경우에만 식사한 것으로 보고 알림메시지 띄움 (잠깐 지나가는 경우를 필터하기 위해)
 *
 * @author heonkyu.jin
 * @version 15. 11. 21
 */
public class BeaconCriteria {
    /**
     * 식당 내에 3분 이상 머무르면 식사로 판단.
     */
    private static final long RESIDE_CRITERIA = 3 * 60;

    /**
     * 식사시간의 스캔주기. 5분.
     */
    private static final long DINE_SCAN_INTERVAL = 5 * 60;

    /**
     * 식사시간이 아닌 때의 스캔주기. 10분.
     */
    private static final long NO_DINE_SCAN_INTERVAL = 10 * 60;

    /**
     * 식당 영업시간.
     */
    private static final List<Pair<Time, Time>> DINE_HM = Lists.newArrayList(
            new Pair<Time, Time>(new Time(6, 50 - 10), new Time(8, 10 + 10)),
            new Pair<Time, Time>(new Time(11, 50 - 10), new Time(13, 20 + 10)),
            new Pair<Time, Time>(new Time(17, 50 - 10), new Time(19, 10 + 10))
    );

    /**
     * 식사시간인지 아닌지 판단.
     *
     * @return
     */
    protected static boolean isForDining(Time time) {
        return Iterables.any(BeaconCriteria.DINE_HM, target -> time.between(target));
    }

    /**
     * 스캔 주기 확인.
     *
     * @return
     */
    protected static long getScanInterval() {
        return (isForDining(Time.now()) ? DINE_SCAN_INTERVAL : NO_DINE_SCAN_INTERVAL) * 1000;
    }

    /**
     * 식당 구간에 진입했을 경우 호출.
     *
     * @return
     */
    protected static RegionEntrance regionEntered() {
        return new RegionEntrance();
    }

    /**
     * 식당 진입을 represent하는 객체.
     *
     */
    protected static class RegionEntrance {
        /**
         * 처음으로 식당 근처에 나타난 시각.
         */
        Time firstSeen;

        /**
         * 마지막으로 식당 안에 있던 시각.
         */
        Time lastSeen;

        protected RegionEntrance() {
            firstSeen = Time.now();
            lastSeen = firstSeen;
        }

        protected void markStillResiding() {
            lastSeen = Time.now();
        }

        protected boolean isForDining() {
            return BeaconCriteria.isForDining(firstSeen) &&
                    lastSeen.differenceInSeconds(firstSeen) >= RESIDE_CRITERIA;
        }
    }

}
