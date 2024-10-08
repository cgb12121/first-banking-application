package com.backend.bank.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "emailTaskExecutor")
    public Executor emailTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(500);
        executor.setQueueCapacity(500);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setKeepAliveSeconds(5);
        executor.setPrestartAllCoreThreads(true);
        executor.setStrictEarlyShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean(name = "verifyTaskExecutor")
    public Executor verifyTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(500);
        executor.setQueueCapacity(500);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setKeepAliveSeconds(5);
        executor.setPrestartAllCoreThreads(true);
        executor.setStrictEarlyShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean(name = "userTaskExecutor")
    public Executor userTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(500);
        executor.setQueueCapacity(500);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setKeepAliveSeconds(5);
        executor.setPrestartAllCoreThreads(true);
        executor.setStrictEarlyShutdown(true);
        executor.initialize();
        return executor;
    }

    @Bean(name = "transactionTaskExecutor")
    public Executor transactionTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(500);
        executor.setQueueCapacity(500);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setKeepAliveSeconds(5);
        executor.setPrestartAllCoreThreads(true);
        executor.setStrictEarlyShutdown(true);
        executor.initialize();
        return executor;
    }
}
