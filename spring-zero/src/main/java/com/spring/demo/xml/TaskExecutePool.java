package com.spring.demo.xml;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class TaskExecutePool {
    
    @Bean("saTaskAsyncPool")
    public ThreadPoolTaskExecutor getCenterThreadPoolExecutor() {
    	//MOS是IO密集型程序，线程的作用主要是网络等待。
        //获取cpu核心数
        final int cpu = Runtime.getRuntime().availableProcessors();
        //设置核心线程池数量
        final int corePoolSize = cpu + 1;
        //设置线程池中最大线程数量
        final int maximumPoolSize = cpu * 2 + 1;
        
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(corePoolSize);   
		executor.setMaxPoolSize(maximumPoolSize);
		executor.setThreadNamePrefix("SATaskAsyncPool - ");
		// 线程池对拒绝任务的处理策略
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		// 初始化
		executor.initialize();
		return executor;
        
    }

}
