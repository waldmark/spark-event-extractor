package com.objectpartners.common.domain;


import org.joda.time.DateTime;

import java.io.Serializable;

public class Event<T> implements Serializable, EventIdentity {
    static final long serialVersionUID = 100L;

    public static final String EVENT_TYPE_COL = "event_type";
    public static final String EVENT_LEDGER_KS = "event_ledger";
    public static final String EVENT_TABLE = "events";
    private Integer ttl;
    private EventId eventKey;
    private String eventType;
    private DateTime writeTime;
    private T body;

    public Event() {
    }

    public Event(EventId eventKey, String eventType, Integer ttl, DateTime writeTime, T body) {
        this.eventType = eventType;
        this.eventKey = eventKey;
        this.ttl = ttl;
        this.body = body;
        this.writeTime = writeTime;
    }

    public String getEventType() {
        return this.eventType;
    }

    public Integer getTtl() {
        return this.ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public EventId getEventKey() {
        return this.eventKey;
    }

    public DateTime getWriteTime() {
        return this.writeTime;
    }

    public T getBody() {
        return this.body;
    }

    public String toString() {
        return "Event{ttl=" + this.ttl + ", eventType=\'" + this.eventType + '\'' + ", eventKey=\'" + this.eventKey + '\'' + ", type=" + this.getClass().getSimpleName() + '}';
    }
}
