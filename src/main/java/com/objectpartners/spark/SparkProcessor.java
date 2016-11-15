package com.objectpartners.spark;

import com.objectpartners.common.components.MapEvent;
import com.objectpartners.common.domain.Event;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;

import static com.datastax.spark.connector.japi.CassandraJavaUtil.javaFunctions;

/**
 * Spark batch processing analysis of Cassandra data
 */
@Component
public class SparkProcessor implements Serializable {
    static final long serialVersionUID = 100L;
    private static Logger LOG = LoggerFactory.getLogger(SparkProcessor.class);

    @Value(value="${spring.profiles.active}")
    private String springProfile;

    @Value(value="${cassandra.keyspaces[0].name}")
    private String keyspaceName;

    @Value(value="${cassandra.keyspaces[0].tables[0].name}")
    private String eventTableName;

    @Value(value="${cassandra.keywords[0].keyword}")
    private String keyword;

    @Value(value="${spark.cassandra.connection.host}")
    private String cassandraHost;

    JavaRDD<Event> processCassandraData() {

//         *************************************************************************************************************
//         set up Spark context
//         *************************************************************************************************************

        LOG.info("running with Spring profile: " + springProfile);
        LOG.info("connecting to Cassandra at: " + cassandraHost);
        SparkConf conf = new SparkConf()
                .setAppName("CassandraClient")
                .setMaster("local")
                .set("spark.executor.memory", "1g")
                .set("spark.cassandra.connection.host", cassandraHost);
        JavaSparkContext sc = new JavaSparkContext(conf);

//         *************************************************************************************************************
//         read from Cassandra into Spark RDD
//         *************************************************************************************************************

        JavaRDD<Event> eventData = javaFunctions(sc)
                .cassandraTable(keyspaceName, eventTableName)
                .map(new MapEvent());

        LOG.info("callRDD count = " + eventData.count());

//         *************************************************************************************************************
//         analyze and process with Spark
//         *************************************************************************************************************

        // filter by event type
        LOG.info("keyword: " + keyword);
        eventData = eventData.filter( c -> (c.getEventType().matches("(.*\\b"+keyword+"\\b.*)")));
        LOG.info("callRDD count = " + eventData.count());
        return eventData;
    }

}
