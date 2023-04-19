//package com.spring.demo.javaconfig;
//
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//
///**
// * java 配置方式启动
// */
//public class JavaConfigTest {
//
//	public static void main(String[] args) {
//		// java 配置启动
//		// BeanFactory annotationApplicationContex1 = new AnnotationConfigApplicationContext("com.spring.demo.javaconfig");
//		BeanFactory annotationApplicationContext = new AnnotationConfigApplicationContext(AppConfig.class);
//		AppService appService = annotationApplicationContext.getBean(AppService.class);
//		appService.print();
//
//		IBusinessService businessService = (IBusinessService) annotationApplicationContext.getBean("businessService");
//		businessService.executeBusinessA();
//		businessService.executeBusinessB();
//
//	}
//
//}
