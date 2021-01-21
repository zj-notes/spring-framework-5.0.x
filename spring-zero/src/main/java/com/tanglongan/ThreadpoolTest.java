//package com.tanglongan;
//
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.Future;
//
//public class ThreadpoolTest {
//	public static void main(String[] args) throws Exception {
//		BeanFactory context = new ClassPathXmlApplicationContext("applicationContext.xml");
//		ThreadPoolTaskExecutor threadPool = (ThreadPoolTaskExecutor) context.getBean("saTaskAsyncPool");
//
//		List<Future<Integer>> futureList = new ArrayList<>();
//		List<Integer> aa = new ArrayList<>();
//		aa.add(1);
//		aa.add(2);
//		aa.add(3);
//
//		for(Integer a : aa){
//			Future<Integer> future = threadPool.submit(new Callable<Integer>() {
//				@Override
//				public Integer call() throws InterruptedException {
//					Thread.currentThread().sleep(2000L);
//					return a * 2;
//				}
//			});
//			futureList.add(future);
//		}
//
//		for(Future<Integer> f : futureList){
//			System.out.println(f.get());
//		}
//	}
//
//
//}
