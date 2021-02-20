package com.tanglongan;

import com.tanglongan.aop.IBusinessService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;
import java.util.concurrent.*;

public class AppTest {

	public static void main(String[] args) {
		// java 配置启动
		BeanFactory annotationApplicationContex1 = new AnnotationConfigApplicationContext("com.tanglongan");
		BeanFactory annotationApplicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
//		AppService appService = annotationApplicationContext.getBean(AppService.class);
//		appService.print();
//
//		IBusinessService businessService = (IBusinessService) annotationApplicationContext.getBean("businessService");
//		businessService.executeBusinessA();
//		businessService.executeBusinessB();

		// xml配置文件启动，核心方法refresh()
		BeanFactory context = new ClassPathXmlApplicationContext("applicationContext.xml");
		AppService bean1 = (AppService) context.getBean("appService");
		bean1.print();
		AppService bean = context.getBean(AppService.class);
		bean.print();

		IBusinessService businessService = (IBusinessService) context.getBean("businessService");
		businessService.executeBusinessA();
		businessService.executeBusinessB();


		// webApp启动，ContextLoaderListener.contextInitialized()->initWebApplicationContext()->configureAndRefreshWebApplicationContext()->refresh()
		// springBoot

		/**
		ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
		map.put("1","1");

		// 线程池测试
		ExecutorService pool = new ThreadPoolExecutor(3, 4, 3000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		for(int i=0;i<10;i++) {
			pool.execute(() -> {
				try {
					Thread.currentThread().sleep(2000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(Thread.currentThread().getName() + ":123");
			});
		}

		ThreadLocal<Object> currentThreadLocal = new ThreadLocal<Object>();
		currentThreadLocal.set(123);
		System.out.println(currentThreadLocal.get());
		 **/
	}

}
