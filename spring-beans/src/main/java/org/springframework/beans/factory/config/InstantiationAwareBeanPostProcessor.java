/*
 * Copyright 2002-2019 the original author or authors.
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

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.lang.Nullable;

/**
 * BeanPostProcessor 的子接口，它添加了实例化之前的回调，以及实例化之后但设置了显式属性或自动装配之前的回调
 * 它内部提供了3个方法，再加上BeanPostProcessor接口内部的2个方法，实现这个接口需要实现5个方法
 * InstantiationAwareBeanPostProcessor 接口的主要作用在于目标对象的实例化过程中需要处理的事情，包括实例化对象的前后过程以及实例的属性设置
 */
public interface InstantiationAwareBeanPostProcessor extends BeanPostProcessor {

	// 目标对象被实例化之前调用的方法，可以返回目标实例的一个代理用来代替目标实例
	@Nullable
	default Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
		return null;
	}

	// 该方法在Bean实例化之后执行，返回false，会忽略属性值的设置；如果返回true，会按照正常流程设置属性值
	default boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
		return true;
	}

	// 该方法用于属性注入，在 bean 初始化阶段属性填充时触发。@Autowired，@Resource 等注解原理基于此方法实现
	@Nullable
	default PropertyValues postProcessPropertyValues( PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {
		return pvs;
	}

}
