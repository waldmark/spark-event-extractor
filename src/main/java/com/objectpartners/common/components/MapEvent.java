package com.objectpartners.common.components;


import com.datastax.spark.connector.japi.CassandraRow;
import com.objectpartners.common.domain.Event;
import com.objectpartners.common.domain.EventId;
import org.apache.spark.api.java.function.Function;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MapEvent implements Function<CassandraRow, Event> {

    @Value(value="${eventLedger.defaultTtlSeconds:60000}")
    private int defaultTtlSeconds;

    @Override
    public Event call(CassandraRow row) throws Exception {
        String emitterId = row.getString("emitter_id");
        String emitterType = row.getString("emitter_type");
        Integer bucket = row.getInt("bucket");
        DateTime timestamp = row.getDateTime("time");
        String eventId = row.getString("event_id");
        String eventType = row.getString("event_type");
        String textData = row.getString("text_data");

        EventId eventKey = new EventId(
                emitterId,
                emitterType,
                bucket,
                timestamp,
                eventId);

        return new Event<>(eventKey, eventType, defaultTtlSeconds, new DateTime(), textData);
    }

}
