package com.sparta.delivery.global.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "aiExecutor")
    public Executor aiExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(1);          // AI: 최소 2개
        ex.setMaxPoolSize(2);           // 피크 시 확장
        ex.setQueueCapacity(100);       // 대기열(Queue)에 쌓을 수 있는 작업 개수
        ex.setThreadNamePrefix("ai-");
        ex.setAwaitTerminationSeconds(30); // 앱 종료 시 대기할 시간
        ex.setWaitForTasksToCompleteOnShutdown(true); // 종료 전에 대기할지 여부
        ex.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // 풀과 큐가 모두 꽉 찼을 때의 동작 정의로
        ex.initialize();                                                           // 새 작업을 처리할 스레드가 없으면 이 작업을 보낸 스레드가 직접 실행한다
        return ex;
    }
    @Bean(name = "discordExecutor")
    public Executor discordExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(1);          // Discord: 2개
        ex.setMaxPoolSize(1);           // 알림은 고정폭 권장
        ex.setQueueCapacity(50);
        ex.setThreadNamePrefix("discord-");
        ex.setAwaitTerminationSeconds(15);
        ex.setWaitForTasksToCompleteOnShutdown(true);
        ex.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy()); // 알림 폭주 시 새로운 작업을 그냥 버림 즉 누락
        ex.initialize();
        return ex;
    }

}
