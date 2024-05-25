package org.aliothmoon.light.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

@Configuration
class AsyncConfiguration {
    @Bean("asyncExecutor")
    fun asyncExecutor(): ThreadPoolTaskExecutor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 12 // 设置核心线程数
        executor.maxPoolSize = 20 // 设置最大线程数
        executor.queueCapacity = 200 // 设置队列大小
        executor.setThreadNamePrefix("WorkAsyncExecutor-") // 设置线程名前缀
        executor.initialize()
        return executor
    }
}