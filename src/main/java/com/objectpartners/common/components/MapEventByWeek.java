package com.objectpartners.common.components;


import com.objectpartners.common.domain.Event;
import com.objectpartners.common.domain.EventId;
import org.apache.spark.api.java.function.PairFunction;
import org.joda.time.DateTime;
import scala.Tuple2;

public class MapEventByWeek implements PairFunction<Event, String, Event>  {

    @Override
    public Tuple2<String, Event> call(Event event) throws Exception {
        EventId eid = event.getEventKey();

        DateTime time = eid.getTime();
        int year = time.year().get();
        int weekOfYear = time.weekOfWeekyear().get();
        return new Tuple2<>(year + "" +  weekOfYear, event);
    }
}
