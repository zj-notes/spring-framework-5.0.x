package com.tanglongan.aop;


public class BusinessServiceImpl implements IBusinessService {
	@Override
	public void executeBusinessA() {
		try {
			Thread.sleep(200);
			System.out.println("executing business in methodA");
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}

	@Override
	public void executeBusinessB() {
		try {
			Thread.sleep(2000);
			System.out.println("executing business in methodB");
		} catch (InterruptedException e) {
			System.out.println(e.getMessage());
		}
	}
}
