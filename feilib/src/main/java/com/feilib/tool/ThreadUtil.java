package com.feilib.tool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: java类作用描述
 * @Author: murphy
 * @CreateDate: 2021/8/4 10:52 上午
 * @UpdateUser: 更新者
 * @UpdateDate: 2021/8/4 10:52 上午
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ThreadUtil {

    private void test(){
        ThreadFactory namedThreadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return null;
            }
        };

        //Common Thread Pool
        ExecutorService pool = new ThreadPoolExecutor(3, 200,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(1024), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());

        pool.execute(()-> System.out.println(Thread.currentThread().getName()));
        pool.shutdown();//gracefully shutdown
    }
}
