package org.apache.ibatis.helloworld;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.Properties;

@Intercepts(value = {
  @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class LogInterceptor implements Interceptor {
  private String name;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    System.out.println("Before proceed......");
    Object proceed = invocation.proceed();
    Object[] args = invocation.getArgs();
    Object arg = args[0];
    if (arg instanceof MappedStatement) {
      MappedStatement mappedStatement = (MappedStatement) arg;
      BoundSql boundSql = mappedStatement.getBoundSql(mappedStatement.getParameterMap());
      String sql = boundSql.getSql();
      if (args.length >= 1) {
        fillParameters(sql, args[1]);
      }
      System.out.println("sql:" + sql);
    }
    System.out.println("After proceed......");
    return proceed;
  }

  private void fillParameters(String sql, Object parameters) {
    int wildcardCount = countWildcards(sql);
    if (wildcardCount > 0) {
      doFillParameters(sql, wildcardCount, parameters);
    }
  }

  private int countWildcards(String sql) {
    if (sql == null || sql.length() == 0) {
      return 0;
    }
    return sql.split("\\?").length;
  }

  private void doFillParameters(String sql, int wildcardCount, Object parameters) {
    if (wildcardCount == 1) {
      sql = sql.replace("?", parameters + "");
      System.out.println("Parameterized sql:" + sql);
      return;
    }
    Object[] params = (Object[]) parameters;
    for (Object param : params) {
      sql = sql.replaceFirst("\\?", param + "");
    }
  }

  @Override
  public Object plugin(Object target) {
    return Interceptor.super.plugin(target);
  }

  @Override
  public void setProperties(Properties properties) {
    System.out.println("Properties:" + properties);
    Interceptor.super.setProperties(properties);
  }
}
