package com.tanglongan;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class AppService {

//	springboot 中可以直接注入 ApplicationContext 对象，spring中是否可以？
//	@Autowired
//	private ApplicationContext ace;

	public void print() {
		System.out.println("*******************************\nAppService.print()\n*******************************");
//		System.out.println("*******************************" + ace.toString() + "*******************************");
	}

}
