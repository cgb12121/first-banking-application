package com.backend.bank.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.backend.bank.repository.jpa"
)
@EnableRedisRepositories(
    basePackages = "com.backend.bank.repository.redis"
)
public class MultiDataSourceConfig {
}
