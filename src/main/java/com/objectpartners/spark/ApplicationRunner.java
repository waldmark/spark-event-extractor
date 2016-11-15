package com.objectpartners.spark;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.objectpartners.aws.S3Client;
import com.objectpartners.cassandra.CassandraPnetEventDataLoader;
import com.objectpartners.common.components.MapEventByWeek;
import com.objectpartners.common.domain.Event;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Orchestrates the demo processing
 * to load demo data into Cassandar
 * process demo data using Spark analysis
 * store analysis results to S3
 */
@Component
public class ApplicationRunner {
    private static Logger LOG = LoggerFactory.getLogger(ApplicationRunner.class);

    @Autowired
    private SparkProcessor sparkProcessor;

    @Autowired
    private CassandraPnetEventDataLoader dataLoader;

    @Autowired
    private S3Client s3Client;

    @Value(value = "${s3.bucket.name:test-bucket-100}")
    private String bucketName;

    @Value(value = "${cassandra.reload:false}")
    private Boolean reloadCassandra;

    public void runSparkStreamProcessing() {

        if (reloadCassandra) {
            /*
             * initialize Cassandra with 911 call data
             * this requires that Cassandra is up and running
             * the demo is configured to run with a local Cassandra
             * for example a docker Cassandra image
             * @link https://hub.docker.com/_/cassandra/
             */
            dataLoader.insertEvents();
        }

        //now read data from Cassandra into Spark and batch process the data
        LOG.info("processing Cassandra data with Spark");

        JavaRDD<Event> eventData = sparkProcessor.processCassandraData();
        JavaPairRDD<String, Event> vinDiscoveryEvents = eventData.mapToPair(new MapEventByWeek());
        JavaPairRDD<String, Iterable<Event>> eventsGroupedByWeek = vinDiscoveryEvents.groupByKey();

        LOG.info("converting Spark results to JSON");
        ObjectMapper mapper = new ObjectMapper();
        List<String> jsonArrayElements = new ArrayList<>();
        Map<String, String> s3BucketData = new HashMap<>();

        Map<String, Iterable<Event>> map = eventsGroupedByWeek.collectAsMap();
        Set<String> keys = map.keySet();

        for (String key : keys) {
            Iterable<Event> eventIterator = map.get(key);
            Iterator<Event> iterator = eventIterator.iterator();
            while (iterator.hasNext()) {
                Event event = iterator.next();
                try {
                    String eventJSON = mapper.writeValueAsString(event);
                    jsonArrayElements.add(eventJSON);
                } catch (JsonProcessingException e) {
                    LOG.error(e.getMessage());
                }
                LOG.debug("Event: " + event.toString());
            }
            StringJoiner joiner = new StringJoiner(",");
            jsonArrayElements.forEach(joiner::add);
            s3BucketData.put(key, "[" + joiner.toString() + "]");
        }

        /*
         * save to S3
         * this demo assume S3 is available somewhere
         * the demo usesthe Scalaity open source S3 docker image
         * @link https://hub.docker.com/r/scality/s3server/
         */
        LOG.info("storing JSON into S3 bucket: " + bucketName);

        try {
            // remove the S3 bucket, this removes all objects in the bucket first
            s3Client.removeBucket(bucketName);
            LOG.info("S3 bucket " + bucketName + " deleted");
        } catch (Exception e) {
            // bucket not deleted, may not have been there
        }

        try {
            // create the bucket to start fresh
            s3Client.createBucket(bucketName);
            LOG.info("S3 bucket " + bucketName + " created");

            // save to S3
            for (String key : s3BucketData.keySet()) {
                s3Client.storeString(bucketName, key, s3BucketData.get(key));
            }
            LOG.info("saving JSON to S3 completed");

            // list the buckets that were saved
            s3Client.getBucketDescriptions();

            // dump the bucket to see what's inside
            List<String> descs = s3Client.getBucketObjectDescriptions(bucketName);
            LOG.info("S3 bucket " + bucketName + " holds these objects:");
            for (String desc : descs) {
                LOG.info(desc);
            }

            // try reading the JSON data back from the bucket
            String key = s3BucketData.keySet().iterator().next(); // get first key
            String storedObject = s3Client.readS3Object(bucketName, key);
            LOG.info("read from S3 bucket " + bucketName + " and key " + key + "  first 1k bytes: \n"
                    + storedObject.substring(0, 1000) + "... ");
        } catch (Exception e) {
            LOG.error(e.getMessage());
        } finally {
            // clean up
            s3Client.removeBucket(bucketName);
        }
        LOG.info("Spark processing Cassandra data completed.");
    }

}

