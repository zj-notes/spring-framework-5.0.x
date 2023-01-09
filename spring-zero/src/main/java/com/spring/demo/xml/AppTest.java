package com.spring.demo.xml;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppTest {

	public static void main(String[] args) {
		// xml配置文件启动，核心方法refresh()
		BeanFactory context = new ClassPathXmlApplicationContext("applicationContext.xml");
		AppService bean1 = (AppService) context.getBean("appService");
		bean1.print();
		AppService bean = context.getBean(AppService.class);
		bean.print();

		IBusinessService businessService = (IBusinessService) context.getBean("businessService");
		businessService.executeBusinessA();
		businessService.executeBusinessB();
	}

}
