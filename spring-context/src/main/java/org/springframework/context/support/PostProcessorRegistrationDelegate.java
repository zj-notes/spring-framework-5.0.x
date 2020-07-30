/*
 * Copyright 2002-2018 the original author or authors.
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

package org.springframework.context.support;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.lang.Nullable;

/**
 * Delegate for AbstractApplicationContext's post-processor handling.
 *
 * @author Juergen Hoeller
 * @since 4.0
 */
final class PostProcessorRegistrationDelegate {

	// 调用 BeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry 和 BeanFactoryPostProcessor.postProcessBeanFactory
	public static void invokeBeanFactoryPostProcessors( ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

		//保存所有后置处理器bean名称的集合
		Set<String> processedBeans = new HashSet<>();

		// 判断 beanFactory 是否是 BeanDefinitionRegistry
		// 当是 BeanDefinitionRegistry 时
		if (beanFactory instanceof BeanDefinitionRegistry) {

			// 将 beanFactoryPostProcessors 分为 BeanFactoryPostProcessor 集合与 BeanDefinitionRegistryPostProcessor 集合
			// 此处传进来的 beanFactory 是 DefaultListableBeanFactory 实例，实现了 BeanDefinitionRegistry 接口

			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			// BeanFactoryPostProcessor 集合(传进来的参数 beanFactoryPostProcessors 集合中的bean)
			List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
			// BeanDefinitionRegistryPostProcessor 集合(传进来的参数 beanFactoryPostProcessors 集合中的bean)
			List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

			// 遍历查询 BeanFactoryPostProcessor 是否为 BeanDefinitionRegistryPostProcessor
			for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
				if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					// 当前 postProcessor 为 BeanDefinitionRegistryPostProcessor 则执行 postProcessBeanDefinitionRegistry 方法
					// 执行 postProcessBeanDefinitionRegistry 方法
					BeanDefinitionRegistryPostProcessor registryProcessor = (BeanDefinitionRegistryPostProcessor) postProcessor;
					registryProcessor.postProcessBeanDefinitionRegistry(registry);
					registryProcessors.add(registryProcessor);
				}
				else {
					regularPostProcessors.add(postProcessor);
				}
			}

			List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

			// 找出 beanFactory 中的所有实现了 BeanDefinitionRegistryPostProcessor 接口和 PriorityOrdered 接口的 bean
			// 放入 registryProcessors 集合，放入根据 PriorityOrdered 接口来排序，然后这些 bean 会被 invokeBeanDefinitionRegistryPostProcessors 方法执行
			String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			// 根据PriorityOrdered接口来排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// 放入 registryProcessors 集合
			registryProcessors.addAll(currentRegistryProcessors);
			// 然后这些bean会被invokeBeanDefinitionRegistryPostProcessors方法执行
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			currentRegistryProcessors.clear();

			// 找出 beanFactory 中的所有实现了 BeanDefinitionRegistryPostProcessor 接口和 Ordered 接口的 bean，
			// 放入 registryProcessors 集合，放入根据PriorityOrdered接口来排序，然后这些bean会被invokeBeanDefinitionRegistryPostProcessors方法执行
			postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
			for (String ppName : postProcessorNames) {
				// 排除处理后的bean
				if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
					currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
					processedBeans.add(ppName);
				}
			}
			// 根据PriorityOrdered接口来排序
			sortPostProcessors(currentRegistryProcessors, beanFactory);
			// 放入 registryProcessors 集合
			registryProcessors.addAll(currentRegistryProcessors);
			// 然后这些bean会被invokeBeanDefinitionRegistryPostProcessors方法执行
			invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
			currentRegistryProcessors.clear();

			// 对于那些 beanFactory 中的实现了 BeanDefinitionRegistryPostProcessor 接口，
			// 但是没有实现PriorityOrdered和Ordered的bean也被找出来，然后这些bean会被invokeBeanDefinitionRegistryPostProcessors方法执行
			boolean reiterate = true;
			while (reiterate) {
				reiterate = false;
				postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
				for (String ppName : postProcessorNames) {
					// 排除处理后的bean，经过前两步处理，实现 PriorityOrdered 和 Ordered 接口的bean已经被处理
					if (!processedBeans.contains(ppName)) {
						currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
						processedBeans.add(ppName);
						reiterate = true;
					}
				}
				sortPostProcessors(currentRegistryProcessors, beanFactory);
				registryProcessors.addAll(currentRegistryProcessors);
				invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
				currentRegistryProcessors.clear();
			}
			// 最后调用 BeanFactoryPostProcessor 接口的 postProcessBeanFactory 方法
			// 注意这里的 registryProcessors 中的bean只是实现了 BeanDefinitionRegistryPostProcessor 接口的bean
			invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);

			// 最后调用 BeanFactoryPostProcessor 接口的 postProcessBeanFactory 方法
			// 注意这些bean是传进来的参数 beanFactoryPostProcessors 集合中的bean
			invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
		}
		else {
			// 直接调用 BeanFactoryPostProcessor 接口的 postProcessBeanFactory 方法
			invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
		}


		// 找出实现了 beanFactory 中的 BeanFactoryPostProcessor 接口的 bean，
		// 注意这里已将实现了 BeanDefinitionRegistryPostProcessor 接口的 bean 给剔除了，
		// 将这些bean分为三类：
		// 	 实现了 PriorityOrdered 接口的放入 priorityOrderedPostProcessors，
		// 	 实现了 Ordered 接口的放入 orderedPostProcessorNames，
		// 	 其他的放入 nonOrderedPostProcessorNames，
		// 这段代码是关键，因为我们自定义的实现 BeanFactoryPostProcessor 接口的 bean 就会在此处被找出来
		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);
		List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		List<String> orderedPostProcessorNames = new ArrayList<>();
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (processedBeans.contains(ppName)) {
				// skip - already processed in first phase above
			}
			else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				orderedPostProcessorNames.add(ppName);
			}
			else {
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// 1、 priorityOrderedPostProcessors 集合，都是先做排序再调用 invokeBeanFactoryPostProcessors 方法
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

		// 2、orderedPostProcessorNames 集合，先做排序再调用 invokeBeanFactoryPostProcessors 方法
		List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : orderedPostProcessorNames) {
			orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		sortPostProcessors(orderedPostProcessors, beanFactory);
		invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

		// 3、nonOrderedPostProcessorNames集合，也被传入 invokeBeanFactoryPostProcessors 方法
		List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String postProcessorName : nonOrderedPostProcessorNames) {
			nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
		}
		invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

		beanFactory.clearMetadataCache();
	}

	// 调用 BeanPostProcessor.postProcessBeforeInitialization 方法
	public static void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, AbstractApplicationContext applicationContext) {
		// 获取所有实现 BeanPostProcessor 接口的bean的名称
		String[] postProcessorNames = beanFactory.getBeanNamesForType(BeanPostProcessor.class, true, false);

		// 注意，此时尽管注册操作还没有开始，但是之前已经有一些特殊的bean已经注册进来了，
		// 详情请看 AbstractApplicationContext 类的prepareBeanFactory方法，
		// 因此 getBeanPostProcessorCount() 方法返回的数量并不为零，
		// +1 是因为方法末尾会注册一个 ApplicationListenerDetector 接口的实现类
		// ApplicationListenerDetector 在 referh 方法中 prepareBeanFactory 中已经注册过了，beanFactory.addBeanPostProcessor 注册时会检查是否重复，重复则删除再注册
		int beanProcessorTargetCount = beanFactory.getBeanPostProcessorCount() + 1 + postProcessorNames.length;
		// 这里的 BeanPostProcessorChecker 也是个 BeanPostProcessor 的实现类，用于每个bean的初始化完成后，做一些简单的检查
		beanFactory.addBeanPostProcessor(new BeanPostProcessorChecker(beanFactory, beanProcessorTargetCount));


		// 如果这些bean还实现了 PriorityOrdered 接口（执行顺序），就全部放入集合 priorityOrderedPostProcessors
		List<BeanPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
		// 集合 internalPostProcessors，用来存放同时实现了 PriorityOrdered 和 MergedBeanDefinitionPostProcessor 接口的bean
		List<BeanPostProcessor> internalPostProcessors = new ArrayList<>();
		// 集合orderedPostProcessorNames用来存放实现了Ordered接口的bean的名称（执行顺序）
		List<String> orderedPostProcessorNames = new ArrayList<>();
		// 集合nonOrderedPostProcessorNames用来存放即没实现PriorityOrdered接口，也没有实现Ordered接口的bean的名称（不关心执行顺序）
		List<String> nonOrderedPostProcessorNames = new ArrayList<>();
		for (String ppName : postProcessorNames) {
			if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
				BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
				// 实现了 PriorityOrdered 接口的bean，都放入集合 priorityOrderedPostProcessors
				priorityOrderedPostProcessors.add(pp);
				if (pp instanceof MergedBeanDefinitionPostProcessor) {
					// 实现了 MergedBeanDefinitionPostProcessor 接口的bean，都放入 internalPostProcessors 集合
					internalPostProcessors.add(pp);
				}
			}
			else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
				// 实现了 Ordered 接口的bean，将其名称都放入 orderedPostProcessorNames 集合
				orderedPostProcessorNames.add(ppName);
			}
			else {
				// 既没实现 PriorityOrdered 接口，也没有实现 Ordered 接口的bean，将其名称放入 nonOrderedPostProcessorNames 集合
				nonOrderedPostProcessorNames.add(ppName);
			}
		}

		// 实现了 PriorityOrdered 接口的bean排序
		sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
		// 注册到容器, 调用 AbstractBeanFactory.addBeanPostProcessor
		registerBeanPostProcessors(beanFactory, priorityOrderedPostProcessors);

		// 处理所有实现了 Ordered 接口的bean
		List<BeanPostProcessor> orderedPostProcessors = new ArrayList<>();
		for (String ppName : orderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			orderedPostProcessors.add(pp);
			// 前面将所有实现了 PriorityOrdered 和 MergedBeanDefinitionPostProcessor 的bean放入 internalPostProcessors，
			// 此处将所有实现了 Ordered 和 MergedBeanDefinitionPostProcessor 的bean放入 internalPostProcessors
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		// 实现了 Ordered 接口的bean排序
		sortPostProcessors(orderedPostProcessors, beanFactory);
		// 注册到容器, 调用 AbstractBeanFactory.addBeanPostProcessor
		registerBeanPostProcessors(beanFactory, orderedPostProcessors);

		// // 既没实现 PriorityOrdered 接口，也没有实现 Ordered 接口的bean
		List<BeanPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
		for (String ppName : nonOrderedPostProcessorNames) {
			BeanPostProcessor pp = beanFactory.getBean(ppName, BeanPostProcessor.class);
			nonOrderedPostProcessors.add(pp);
			// 此处将其余实现了 MergedBeanDefinitionPostProcessor 的bean放入 internalPostProcessors
			if (pp instanceof MergedBeanDefinitionPostProcessor) {
				internalPostProcessors.add(pp);
			}
		}
		registerBeanPostProcessors(beanFactory, nonOrderedPostProcessors);

		// 将所有实现了MergedBeanDefinitionPostProcessor接口的bean也注册到容器
		sortPostProcessors(internalPostProcessors, beanFactory);
		registerBeanPostProcessors(beanFactory, internalPostProcessors);

		// 创建一个ApplicationListenerDetector对象并且注册到容器，这就是前面计算beanProcessorTargetCount的值时加一的原因
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(applicationContext));
	}

	private static void sortPostProcessors(List<?> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		Comparator<Object> comparatorToUse = null;
		if (beanFactory instanceof DefaultListableBeanFactory) {
			comparatorToUse = ((DefaultListableBeanFactory) beanFactory).getDependencyComparator();
		}
		if (comparatorToUse == null) {
			comparatorToUse = OrderComparator.INSTANCE;
		}
		postProcessors.sort(comparatorToUse);
	}

	/**
	 * Invoke the given BeanDefinitionRegistryPostProcessor beans.
	 */
	private static void invokeBeanDefinitionRegistryPostProcessors(Collection<? extends BeanDefinitionRegistryPostProcessor> postProcessors, BeanDefinitionRegistry registry) {
		for (BeanDefinitionRegistryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanDefinitionRegistry(registry);
		}
	}

	/**
	 * Invoke the given BeanFactoryPostProcessor beans.
	 */
	private static void invokeBeanFactoryPostProcessors(Collection<? extends BeanFactoryPostProcessor> postProcessors, ConfigurableListableBeanFactory beanFactory) {
		for (BeanFactoryPostProcessor postProcessor : postProcessors) {
			postProcessor.postProcessBeanFactory(beanFactory);
		}
	}

	/**
	 * Register the given BeanPostProcessor beans.
	 */
	private static void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory, List<BeanPostProcessor> postProcessors) {
		for (BeanPostProcessor postProcessor : postProcessors) {
			beanFactory.addBeanPostProcessor(postProcessor);
		}
	}


	/**
	 * BeanPostProcessor that logs an info message when a bean is created during
	 * BeanPostProcessor instantiation, i.e. when a bean is not eligible for
	 * getting processed by all BeanPostProcessors.
	 */
	private static final class BeanPostProcessorChecker implements BeanPostProcessor {

		private static final Log logger = LogFactory.getLog(BeanPostProcessorChecker.class);

		private final ConfigurableListableBeanFactory beanFactory;

		private final int beanPostProcessorTargetCount;

		public BeanPostProcessorChecker(ConfigurableListableBeanFactory beanFactory, int beanPostProcessorTargetCount) {
			this.beanFactory = beanFactory;
			this.beanPostProcessorTargetCount = beanPostProcessorTargetCount;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) {
			if (!(bean instanceof BeanPostProcessor) && !isInfrastructureBean(beanName) &&
					this.beanFactory.getBeanPostProcessorCount() < this.beanPostProcessorTargetCount) {
				if (logger.isInfoEnabled()) {
					logger.info("Bean '" + beanName + "' of type [" + bean.getClass().getName() +
							"] is not eligible for getting processed by all BeanPostProcessors " +
							"(for example: not eligible for auto-proxying)");
				}
			}
			return bean;
		}

		private boolean isInfrastructureBean(@Nullable String beanName) {
			if (beanName != null && this.beanFactory.containsBeanDefinition(beanName)) {
				BeanDefinition bd = this.beanFactory.getBeanDefinition(beanName);
				return (bd.getRole() == RootBeanDefinition.ROLE_INFRASTRUCTURE);
			}
			return false;
		}
	}

}
