/*
 * Copyright 2002-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.config;

import java.lang.reflect.Constructor;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/**
 * InstantiationAwareBeanPostProcessor 接口的扩展，多出了3个方法
 * 添加了用于预测已处理bean的最终类型的回调，再加上父接口的5个方法，所以实现这个接口需要实现8个方法
 * 主要作用也是在于目标对象的实例化过程中需要处理的事情
 */
public interface SmartInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessor {

	// 预测Bean的类型
	@Nullable
	default Class<?> predictBeanType(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	// 选择合适的构造器
	@Nullable
	default Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	// 解决循环引用问题
	/**
	 * 当A实例化之后，Spring IOC会对A依赖的属性进行填充，此时如果发现A依赖了B，会去实例化B。同样在填充B的属性时，如果B也引用了A，就发生了循环依赖。因为A还未创建完成，还未注入Spring中。
	 * Spring的做法是通过对创建中的缓存一个回调函数，类似于一个埋点操作，如果后续填充属性阶段，发生了循环依赖，则通过触发该回调函数来结束该bean的初始化。
	 * ​当对A实例化时，会提前暴露一个回调方法 ObjectFactory（Spring5中改为了函数式接口） 放入缓存。当B引用A，发现A还未实例化结束，就会通过缓存中的回调方法结束A的初始化流程，然后注入B。然后继续A的填充属性流程，将B注入A，然后结束循环依赖。
	 */
	default Object getEarlyBeanReference(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
