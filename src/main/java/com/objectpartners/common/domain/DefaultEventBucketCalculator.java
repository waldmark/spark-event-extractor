package com.objectpartners.common.domain;


import org.joda.time.DateTime;

import java.io.Serializable;
import java.util.Date;

public class DefaultEventBucketCalculator implements Serializable {
    static final long serialVersionUID = 100L;

    public DefaultEventBucketCalculator() {
    }

    public static int calculateBucket(DateTime time) {
        int year = time.getWeekyear();
        int week = time.getWeekOfWeekyear();
        return Integer.parseInt(String.format("%d%02d", new Object[]{Integer.valueOf(year), Integer.valueOf(week)}));
    }

    public static int calculateBucket(Date time) {
        DateTime jTime = new DateTime(time);
        return calculateBucket(jTime);
    }

    public static int calculateBucket(EventId eventId) {
        return calculateBucket(eventId.getTime());
    }

    public static int calculateBucket(Event event) {
        return calculateBucket(event.getEventKey().getTime());
    }

    public static DateTime subtractBucket(DateTime time) {
        return time.minusWeeks(1);
    }
}