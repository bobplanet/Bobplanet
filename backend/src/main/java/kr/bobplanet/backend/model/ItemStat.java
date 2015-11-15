package kr.bobplanet.backend.model;

import com.google.common.collect.Lists;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.OnLoad;

import java.util.List;

/**
 *
 *
 * @author heonkyu.jin
 * @version 15. 11. 15
 */
@Entity
public class ItemStat {
    @Id
    private String name;

    private int countWithin30Days;

    private int countWithin90Days;

    private int countWithin180Days;

    private List<Integer> countWithinDays;

    public ItemStat() {
    }

    public String getName() {
        return name;
    }

    public List<Integer> getCountWithinDays() {
        return countWithinDays;
    }

    @OnLoad
    public void onLoad() {
        countWithinDays = Lists.newArrayList(countWithin30Days, countWithin90Days, countWithin180Days);
    }
}
