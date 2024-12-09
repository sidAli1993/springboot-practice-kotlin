package com.sidalitechnologies.parental_app.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.client.RestTemplate

@Configuration
@EnableTransactionManagement
class TransactionalConfig {
    @Bean
    fun add(mongoDatabaseFactory: MongoDatabaseFactory): PlatformTransactionManager {
        return MongoTransactionManager(mongoDatabaseFactory)
    }

    @Bean
    fun getRestTemplate(): RestTemplate {
        return RestTemplate()
    }

    @Bean
    fun getQuery():Query{
        return Query()
    }
}