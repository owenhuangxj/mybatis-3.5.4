/**
 *    Copyright 2009-2018 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.reflection.invoker;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.ibatis.reflection.Reflector;

/**
 * @author Clinton Begin
 */
public class MethodInvoker implements Invoker {

  private final Class<?> type; // 第一个参数类型或者方法的返回类型
  private final Method method; // 原始的Method

  public MethodInvoker(Method method) {
    this.method = method;

    if (method.getParameterTypes().length == 1) {
      type = method.getParameterTypes()[0]; // 如果方法只有一个参数返回这个参数的类型
    } else {
      type = method.getReturnType(); // 否则返回方法的返回类型
    }
  }

  @Override
  public Object invoke(Object target, Object[] args) throws IllegalAccessException, InvocationTargetException {
    try {
      return method.invoke(target, args);
    } catch (IllegalAccessException illegalAccessException) {
      if (Reflector.canControlMemberAccessible()) { // 如果报非法访问的错误则设置accessible为投入重新执行
        method.setAccessible(true);
        return method.invoke(target, args);
      } else {
        throw illegalAccessException;
      }
    }
  }

  @Override
  public Class<?> getType() {
    return type;
  }
}
