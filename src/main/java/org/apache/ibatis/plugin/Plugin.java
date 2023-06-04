/**
 * Copyright 2009-2019 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ibatis.plugin;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.reflection.ExceptionUtil;

/**
 * 插件：实现了反射核心类InvocationHandler
 *
 * @author Clinton Begin
 */
public class Plugin implements InvocationHandler {
  // 目标对象
  private final Object target;
  // 拦截器
  private final Interceptor interceptor;
  private final Map<Class<?>, Set<Method>> signatureMap;

  private Plugin(Object target, Interceptor interceptor, Map<Class<?>, Set<Method>> signatureMap) {
    this.target = target;
    this.interceptor = interceptor;
    this.signatureMap = signatureMap;
  }

  /**
   * 创建目标对象的代理对象
   *
   * @param target      目标对象
   * @param interceptor 拦截器
   * @return 目标对象的代理对象
   */
  public static Object wrap(Object target, Interceptor interceptor) {
    // 获取用interceptor中@Signature注解中的所有Method信息
    Map<Class<?>, Set<Method>> signatureMap = getSignatureMap(interceptor);

    // 获取目标对象类型
    Class<?> targetType = target.getClass();

    // 获取目标对象的所有接口
    Class<?>[] interfaces = getAllInterfaces(targetType, signatureMap);
    if (interfaces.length > 0) {
      // 如果目标类型有实现接口，就创建目标对象的代理对象
      return Proxy.newProxyInstance(
        targetType.getClassLoader(),
        interfaces,
        new Plugin(target, interceptor, signatureMap));
    }
    return target;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    try {
      Set<Method> methods = signatureMap.get(method.getDeclaringClass());
      if (methods != null && methods.contains(method)) {
        return interceptor.intercept(new Invocation(target, method, args));
      }
      return method.invoke(target, args);
    } catch (Exception e) {
      throw ExceptionUtil.unwrapThrowable(e);
    }
  }

  private static Map<Class<?>, Set<Method>> getSignatureMap(Interceptor interceptor) {
    // 获取@Intercepts注解
    Intercepts interceptsAnnotation = interceptor.getClass().getAnnotation(Intercepts.class);
    // issue #251
    if (interceptsAnnotation == null) {
      throw new PluginException("No @Intercepts annotation was found in interceptor " + interceptor.getClass().getName());
    }
    // 获取value属性值（@Signature注解集合）
    Signature[] signatures = interceptsAnnotation.value();
    Map<Class<?>, Set<Method>> signatureMap = new HashMap<>();
    for (Signature signature : signatures) {
      // 如果signature.type()不存在或者signature.type()对应的value为null，则新建一个Set<Method>,signature.type()在signatureMap中有对应的值什么都不做
      Set<Method> methods = signatureMap.computeIfAbsent(signature.type(), clz -> new HashSet<>());
      try {
        Method method = signature.type().getMethod(signature.method(), signature.args());
        methods.add(method);
      } catch (NoSuchMethodException e) {
        throw new PluginException("Could not find method on " + signature.type() + " named " + signature.method() + ". Cause: " + e, e);
      }
    }
    return signatureMap;
  }

  /**
   * 被拦截类和方法：https://mybatis.net.cn/configuration.html#plugins
   * Executor (update, query, flushStatements, commit, rollback, getTransaction, close, isClosed)
   * ParameterHandler (getParameterObject, setParameters)
   * ResultSetHandler (handleResultSets, handleOutputParameters)
   * StatementHandler (prepare, parameterize, batch, update, query)
   *
   * @param targetType   目标对象的Class类型
   * @param signatureMap 被拦截类中被拦截的方法
   * @return
   */
  private static Class<?>[] getAllInterfaces(Class<?> targetType, Map<Class<?>, Set<Method>> signatureMap) {
    Set<Class<?>> interfaces = new HashSet<>();
    while (targetType != null) {
      for (Class<?> clz : targetType.getInterfaces()) {
        if (signatureMap.containsKey(clz)) {
          interfaces.add(clz);
        }
      }
      targetType = targetType.getSuperclass();
    }
    return interfaces.toArray(new Class<?>[interfaces.size()]);
  }

}
