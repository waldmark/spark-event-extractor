package com.objectpartners.standalone;

import com.objectpartners.config.SparkCassandraConfig;
import com.objectpartners.spark.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class MainApplication {


    public static void main(String[] args) {
        ApplicationContext ctx;
        ApplicationRunner runner;

        ctx = new AnnotationConfigApplicationContext(SparkCassandraConfig.class);
        runner = (ApplicationRunner) ctx.getBean("applicationRunner");

        if(null != runner) {
            runner.runSparkStreamProcessing();
        }
    }
}

