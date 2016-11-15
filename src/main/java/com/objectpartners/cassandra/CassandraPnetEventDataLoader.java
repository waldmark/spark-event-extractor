package com.objectpartners.cassandra;

import com.datastax.driver.core.*;
import com.objectpartners.pnet.domain.EventGenerator;
import com.peoplenet.eventledger.domain.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;

/**
 * Create Cassandra database and
 * load generated telematics event data into Casandra
 */
@Component
//@PropertySource(name = "props", value = "classpath:/application.yml")
public class CassandraPnetEventDataLoader {

    private static Logger LOG = LoggerFactory.getLogger(CassandraPnetEventDataLoader.class);

    @Value(value="${cassandra.keyspaces[0].dropCommand}")
    private String drpKeyspaceCommand;

    @Value(value="${cassandra.keyspaces[0].createCommand}")
    private String createKeyspaceCommand;

    @Value(value="${cassandra.keyspaces[0].tables[0].createCommand}")
    private String createEventTableCommand;

    @Value(value="${cassandra.keyspaces[0].tables[0].insertPreparedStatementCommand}")
    private String inserteDemoEventsCommand;

    @Value(value="${cassandra.keyspaces[0].tables[1].createCommand}")
    private String createMetaEventTableCommand;

    @Value(value="${cassandra.keyspaces[0].tables[2].createCommand}")
    private String createEventBucketTableCommand;

    private Session session;

    public void insertEvents() {
        LOG.info("loading data into Cassandra... ");

        Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();

        Metadata metadata = cluster.getMetadata();
        LOG.info("Connected to cluster: " + metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            LOG.info("Datatacenter: " + host.getDatacenter()
                            + " Host: " + host.getAddress()
                            + " Rack: " + host.getRack() + "n");
        }

        session = cluster.connect();
        createSchema();
        loadData();
        cluster.close();
    }

    private void createSchema() {
        try {
            session.execute(drpKeyspaceCommand);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

        session.execute(createKeyspaceCommand);
        session.execute(createEventTableCommand);
        session.execute(createMetaEventTableCommand);
        session.execute(createEventBucketTableCommand);
    }

    private void loadData() {
        // first, clean out existing data
        session.execute("TRUNCATE event_ledger.events");
        LOG.info("Data truncated....");
        // create the demo data
        List<Event> demoData = EventGenerator.createEventList();
        LOG.info("Demo data: " + demoData.size() + " events generated");

        // insert the demo events into Cassandra
        PreparedStatement prepared = session.prepare(inserteDemoEventsCommand);

        LOG.info("START DATA LOADING");

        int counter = 1;
        for(Event event: demoData) {

//            emitter_id text,
//            emitter_type text,
//            bucket int,
//            time timestamp,
//            event_id text,
//            event_type text,
//            text_data text,
//            bin_data blob,
//            PRIMARY KEY((emitter_id, emitter_type, bucket), time, event_id)

            counter++;

            long millis = event.getEventKey().getTime().getMillis();
            Timestamp timestamp = new Timestamp(millis);

            try {
                session.execute(prepared.bind(
                        event.getEventKey().getEmitterId(),
                        event.getEventKey().getEmitterType(),
                        event.getEventKey().getBucket(),
                        timestamp, // event.getEventKey().getTime() stored as Timestamp in Cassandra
                        event.getEventKey().getEventId(),
                        event.getEventType(),
                        event.getBody()
                        )
                );
            } catch (Exception e) {
                LOG.info("error " + e.getMessage());
            }
        }
        LOG.info("FINSIHED INPUT LOADING - read and loaded " + counter + " events");
    }

}
