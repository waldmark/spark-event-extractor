package com.objectpartners.config;

import com.objectpartners.spark.SparkProcessor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ProfileConfigurator {

    private static Logger LOG = LoggerFactory.getLogger(SparkProcessor.class);

    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        Resource base = new ClassPathResource("application.yml");
        yaml.setResources(base);

        // get the active profile
        String profile = yaml.getObject().getProperty("spring.profiles.active");
        String systemPropertyProfile = System.getProperty("spring.profiles.active");
        if (StringUtils.isNotEmpty(systemPropertyProfile)) {
            profile = systemPropertyProfile;
        }
        LOG.info("running with active spring profile: " + profile);

        // merge active profile with base profile
        if(null != profile) {
            try {
                Resource resource = new ClassPathResource("application-" + profile + ".yml");
                yaml.setResources(base, resource);
            } catch (Exception e) {
                LOG.error("application-" + profile + ".yml not found");
                LOG.error(e.getMessage());
            }
        }

        propertySourcesPlaceholderConfigurer.setProperties(yaml.getObject());
        return propertySourcesPlaceholderConfigurer;
    }
}
