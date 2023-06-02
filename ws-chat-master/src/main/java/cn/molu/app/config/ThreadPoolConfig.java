package cn.molu.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {

    private  int corePoolSize=5;

    private  int maxPoolSize = 10;

    private  long keepAliveTime = 5000;

    private TimeUnit unit = TimeUnit.MILLISECONDS;


    @Bean
    public ThreadPoolExecutor createThreadPoolExecutor(){
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(corePoolSize);

        ThreadFactory threadFactory=Executors.defaultThreadFactory();

        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();

        ThreadPoolExecutor threadPoolExecutor=new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);

        return threadPoolExecutor;
    }
}
