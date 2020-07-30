/*
 * Copyright 2002-2010 the original author or authors.
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

package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

/**
 * 手动注册BeanDefinition（编程方式注册Bean定义）
 * 手动注册bean的两种方式：
 * 实现ImportBeanDefinitionRegistrar
 * 实现BeanDefinitionRegistryPostProcessor
 */
/**
 * BeanDefinitionRegistryPostProcessor 接口可以看作是
 * BeanFactoryPostProcessor(可以对bean的定义（配置元数据）进行处理。也就是说，Spring IoC容器允许BeanFactoryPostProcessor在容器实际实例化任何其它的bean之前读取配置元数据，并有可能修改它)
 * ImportBeanDefinitionRegistrar的功能集合，
 * 既可以获取和修改BeanDefinition的元数据，也可以实现BeanDefinition的注册、移除等操作。
 * 有个重要实现类;ConfigurationClassPostProcessor，它是来处理@Configuration配置文件的。它最终就是解析配置文件里的@Import、@Bean等，然后把定义信息都注册进去
 */
/**
 * 在所有bean定义信息将要被加载，bean实例还未创建的时候加载
 * 优先于BeanFactoryPostProcessor执行
 * 利用BeanDefinitionRegistryPostProcessor给容器中再额外添加一些组件
 */
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

	// 这里可以通过registry，我们手动的向工厂里注册Bean定义信息
	void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;

}
