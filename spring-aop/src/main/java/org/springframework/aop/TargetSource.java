/*<
 * Copyright 2002-2017 the original author or authors.
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

package org.springframework.aop;

import org.springframework.lang.Nullable;

/**
 * A {@code TargetSource} is used to obtain the current "target" of
 * an AOP invocation, which will be invoked via reflection if no around
 * advice chooses to end the interceptor chain itself.
 *
 * <p>If a {@code TargetSource} is "static", it will always return
 * the same target, allowing optimizations in the AOP framework. Dynamic
 * target sources can support pooling, hot swapping, etc.
 *
 * <p>Application developers don't usually need to work with
 * {@code TargetSources} directly: this is an AOP framework interface.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public interface TargetSource extends TargetClassAware {

	// 本方法主要用于返回目标bean的Class类型
	@Override
	@Nullable
	Class<?> getTargetClass();

	// 这个方法用户返回当前bean是否为静态的，比如常见的单例bean就是静态的，而prototype就是动态的，
	// 这里这个方法的主要作用是，对于静态的bean，spring是会对其进行缓存的，在多次使用TargetSource
	// 获取目标bean对象的时候，其获取的总是同一个对象，通过这种方式提高效率
	boolean isStatic();

	// 获取目标bean对象，这里可以根据业务需要进行自行定制
	@Nullable
	Object getTarget() throws Exception;

	// Spring在完目标bean之后会调用这个方法释放目标bean对象，对于一些需要池化的对象，这个方法是必须
	// 要实现的，这个方法默认不进行任何处理
	void releaseTarget(Object target) throws Exception;

}
