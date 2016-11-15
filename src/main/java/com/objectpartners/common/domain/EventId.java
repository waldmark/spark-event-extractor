package com.objectpartners.common.domain;

import org.joda.time.DateTime;

import java.io.Serializable;

public class EventId implements Serializable {
    static final long serialVersionUID = 100L;

    public static final String EMITTER_ID_COL = "emitter_id";
    public static final String EMITTER_TYPE_COL = "emitter_type";
    public static final String BUCKET_COL = "bucket";
    public static final String INITIAL_TTL_COL = "initial_ttl";
    public static final String TIME_COL = "time";
    public static final String EVENT_ID_COL = "event_id";
    private String emitterId;
    private String emitterType;
    private DateTime time;
    private String eventId;
    private Integer bucket;

    public EventId(String emitterId, String emitterType, DateTime time, String eventId) {
        this(emitterId, emitterType, null, time, eventId);
    }

    public EventId(String emitterId, String emitterType, Integer bucket, DateTime time, String eventId) {
        this.emitterId = emitterId;
        this.emitterType = emitterType;
        this.time = time;
        this.eventId = eventId;
        this.bucket = bucket != null?bucket:this.computeBucket();
    }

    protected EventId() {
    }

    private Integer computeBucket() {
        return DefaultEventBucketCalculator.calculateBucket(this);
    }

    public Integer getBucket() {
        return this.bucket;
    }

    public String getEmitterId() {
        return this.emitterId;
    }

    public String getEmitterType() {
        return this.emitterType;
    }

    public DateTime getTime() {
        return this.time;
    }

    public String getEventId() {
        return this.eventId;
    }

    public String toString() {
        return "EventId{emitterId=\'" + this.emitterId + '\'' + ", emitterType=\'" + this.emitterType + '\'' + ", time=" + this.time + ", eventId=\'" + this.eventId + '\'' + '}';
    }
}
