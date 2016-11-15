package com.objectpartners.config;


import com.objectpartners.aws.S3Client;
import com.objectpartners.cassandra.CassandraPnetEventDataLoader;
import com.objectpartners.pnet.domain.EventGenerator;
import com.objectpartners.spark.ApplicationRunner;
import com.objectpartners.spark.SparkProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@ComponentScan(basePackages = {"com.objectpartners"})
public class SparkCassandraConfig {

    @Bean
    ApplicationRunner applicationRunner() {
        return new ApplicationRunner();
    }

    @Bean
    SparkProcessor sparkProcessor() {
        return new SparkProcessor();
    }

    @Bean
    S3Client s3Client() {
        return new S3Client();
    }

    @Bean
    CassandraPnetEventDataLoader cassandraPnetEventDataLoader() {
        return new CassandraPnetEventDataLoader();
    }

    @Bean
    EventGenerator eventGenerator() {
        return new EventGenerator();
    }

    @Bean
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return ProfileConfigurator.propertySourcesPlaceholderConfigurer();
    }

}
