package com.objectpartners.pnet.domain

import com.peoplenet.eventledger.domain.DefaultEventBucketCalculator
import com.peoplenet.eventledger.domain.Event
import com.peoplenet.eventledger.domain.EventId
import org.joda.time.DateTime

class EventGenerator {

    final static int ttl = 60 * 60 * 24 * 30

    static List<Event> createEventList() {

        final List<Event> eventList = []
        final def emitterIds = ['EMITTER-A', 'EMITTER-B', 'EMITTER-C']
        final def eventType = ['VIN-DISCOVERY', 'HARD-BRAKE', 'ENGINE-ON', 'ENGINE-OFF', 'ENGINE-FAULT']

        def year = 2015
        def monthOfYear = 10
        def day = 1..8
        def hour = 1..23
        def min = 1..59
        def sec = [10, 20, 30, 40, 50]

        long fieldEventId = 0

        day.each {
            def dayOfMonth = it
            hour.each {
                def hourOfDay = it
                min.each {
                    def minuteOfHour = it
                    sec.each {
                        def secondOfMinute = it

                        DateTime dateTime = new DateTime(
                                year,
                                monthOfYear,
                                dayOfMonth,
                                hourOfDay,
                                minuteOfHour,
                                secondOfMinute)

                        int bucket = DefaultEventBucketCalculator.calculateBucket(dateTime)

                        emitterIds.each {
                            def id = it
                            eventType.each {
                                def eventId = new EventId(
                                        emitterId: id,
                                        emitterType: 'TEST',
                                        time: dateTime,
                                        eventId: fieldEventId++,
                                        bucket: bucket
                                )
                                eventList << new Event(
                                        eventKey: eventId,
                                        eventType: it,
                                        ttl: ttl,
                                        writeTime: (new DateTime()),
                                        body: "I AM DATA"
                                )
                            }
                        }
                    }
                }
            }
        }
        eventList
    }

}

