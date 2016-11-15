package com.objectpartners.pnet.domain

import spock.lang.Specification


class EventGeneratorTest extends Specification {

    def "test that the event generator creates events"() {
        given:
        EventGenerator eventGenerator = new EventGenerator()

        when:
        def events = eventGenerator.createEventList()

        then:
        events
    }
}
