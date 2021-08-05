//package com.tanglongan.aop;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.stereotype.Component;
//
//@Component
//@Aspect
//public class AopInterceptor {
//	private static final long DELAY_MINUTE = 1000;
//	public static final String POINTCUT = "execution (* com.tanglongan.aop.*(..))";
//
//	@Around(POINTCUT)
//	public Object analysisAround(ProceedingJoinPoint joinPoint) {
//		Object obj = null;
//		long startTime = System.currentTimeMillis();
//		try {
//			obj = joinPoint.proceed(joinPoint.getArgs());
//		} catch (Throwable e) {
//			System.out.println(e.getMessage());
//		}
//
//		long endTime = System.currentTimeMillis();
//		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//		String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
//		long diffTime = endTime - startTime;
//
//		System.out.println("-----" + methodName + "执行时间 ：" + diffTime + " ms");
//		if(diffTime > DELAY_MINUTE)
//			delayWarning(methodName, diffTime-DELAY_MINUTE);
//
//		return obj;
//	}
//
//	private void delayWarning(String methodName,long delayTime) {
//		System.out.println("-----" + methodName + "超时 ：" + delayTime + " ms");
//	}
//}
