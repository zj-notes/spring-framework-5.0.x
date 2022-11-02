
package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.AttributeAccessor;
import org.springframework.lang.Nullable;

public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

	/**
	 * 单例Bean的作用域：singleton
	 */
	String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;
	/**
	 * 单例Bean的作用域：prototype
	 */
	String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;

	int ROLE_APPLICATION = 0;
	int ROLE_SUPPORT = 1;
	int ROLE_INFRASTRUCTURE = 2;

	/**
	 * 设置父类Bean的Definition
	 */
	void setParentName(@Nullable String parentName);

	/**
	 * 获取父类Bean的Definition
	 */
	@Nullable
	String getParentName();

	/**
	 * 设置BeanDefinition对应的类的Class
	 */
	void setBeanClassName(@Nullable String beanClassName);

	/**
	 * 获取BeanDefinition对应的类的Class
	 */
	@Nullable
	String getBeanClassName();

	void setScope(@Nullable String scope);

	@Nullable
	String getScope();

	void setLazyInit(boolean lazyInit);

	boolean isLazyInit();

	/**
	 * 设置当前Bean初始化之前需要依赖的Bean（BeanFactory会保证在初始化当前Bean之前初始化这里指定的那些bean）
	 */
	void setDependsOn(@Nullable String... dependsOn);

	@Nullable
	String[] getDependsOn();

	void setAutowireCandidate(boolean autowireCandidate);

	/**
	 * 设置该 Bean 是否可以注入到其他 Bean 中，只对根据类型注入有效 如果根据名称注入，即使这边设置了 false，也是可以的
	 */
	boolean isAutowireCandidate();

	/**
	 * 主要的。同一接口的多个实现，如果不指定名字的话，Spring 会优先选择设置 primary 为 true 的 bean
	 */
	void setPrimary(boolean primary);

	boolean isPrimary();

	/**
	 * 设置BeanFactory名称
	 */
	void setFactoryBeanName(@Nullable String factoryBeanName);

	/**
	 * 返回BeanFactory名称
	 */
	@Nullable
	String getFactoryBeanName();

	/**
	 * 设置工厂方法（对应factory-method属性）
	 */
	void setFactoryMethodName(@Nullable String factoryMethodName);

	/**
	 * 返回工厂方法（对应factory-method属性）
	 */
	@Nullable
	String getFactoryMethodName();

	/**
	 * 获取有参构造方法的参数列表
	 */
	ConstructorArgumentValues getConstructorArgumentValues();

	/**
	 * 是否存在有参构造方法
	 */
	default boolean hasConstructorArgumentValues() {
		return !getConstructorArgumentValues().isEmpty();
	}

	/**
	 * 返回当前Bean的所有属性字段
	 */
	MutablePropertyValues getPropertyValues();

	/**
	 * 是否有定义了属性字段
	 */
	default boolean hasPropertyValues() {
		return !getPropertyValues().isEmpty();
	}

	/**
	 * Bean作用域是否是“单例”
	 */
	boolean isSingleton();

	/**
	 * Bean作用域是否是“原型”
	 */
	boolean isPrototype();

	/**
	 * Bean是否是抽象类
	 */
	boolean isAbstract();

	int getRole();

	@Nullable
	String getDescription();

	@Nullable
	String getResourceDescription();

	@Nullable
	BeanDefinition getOriginatingBeanDefinition();

}
