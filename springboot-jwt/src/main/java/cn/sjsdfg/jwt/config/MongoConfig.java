package cn.sjsdfg.jwt.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;

/**
 * Created by Joe on 2019/4/28.
 */
@Configuration
public class MongoConfig {
    @Bean
    public MongoTemplate mongoTemplate(@Value("${mongodb.uri}") String mongoUri) {
        System.out.println(mongoUri);
        return new MongoTemplate(new SimpleMongoClientDbFactory(mongoUri));
    }
}
