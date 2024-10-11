package com.cy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoDatabaseFactorySupport;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import javax.annotation.Resource;


/**
 * @author Heachy
 */
@Configuration
public class MongoConfiguration {

    @Resource
    private MongoDatabaseFactorySupport mongoDatabaseFactorySupport;

    @Resource
    private MappingMongoConverter mappingMongoConverter;

    @Bean
    public MongoTemplate mongoTemplate() {

        mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return new MongoTemplate(mongoDatabaseFactorySupport, mappingMongoConverter);
    }
}
