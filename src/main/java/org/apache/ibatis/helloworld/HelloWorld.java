package org.apache.ibatis.helloworld;

import org.apache.ibatis.helloworld.entity.School;
import org.apache.ibatis.helloworld.entity.User;
import org.apache.ibatis.helloworld.mapper.UserMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.*;
import java.util.Collections;
import java.util.List;

public class HelloWorld {
  private static SqlSession sqlSession;

  private static UserMapper userMapper;

  private static SqlSessionFactory sqlSessionFactory;

  @BeforeAll
  static void before() throws IOException {
    // 1.获取配置文件
    InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");
    // 2.加载配置文件并获取SqlSessionFactory
    sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    sqlSession = sqlSessionFactory.openSession();
    userMapper = sqlSession.getMapper(UserMapper.class);
  }

  @AfterAll
  static void after() {
    if (sqlSession != null) {
      sqlSession.close();
    }
  }

  @Test
  public void test1() {
    User user = userMapper.getOneById(1L);
    System.out.println(user);
//    sqlSession.close();
    System.out.println("----------------------------------------------------------");
    SqlSession sqlSession2 = sqlSessionFactory.openSession();
    UserMapper userMapper2 = sqlSession2.getMapper(UserMapper.class);
    user = userMapper2.getOneById(1L);
    System.out.println(user);
    List<User> users = userMapper2.getUsers(new User(39, "Owen"));
    sqlSession2.close();
  }

  @Test
  public void test2() {
    List<User> users = userMapper.getUsers(new User(120, "Owen"));
  }

  @Test
  public void test3() {
    User user = userMapper.getOneById(12345L);
  }

  @Test
  public void testIsBridge() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Person person = new Person();
    person.eat("六味早餐");
    Method eatStringMethod = Person.class.getMethod("eat", String.class);
    eatStringMethod.invoke(person, "鸡蛋");

//    Assertions.assertTrue(!eatStringMethod.isBridge());
    System.out.println("Is eat(String food) not a bridge method:" + !eatStringMethod.isBridge());
    Method eatObjectMethod = Person.class.getMethod("eat", Object.class);
    System.out.println("Is eat(Object foot) a bridge method:" + eatObjectMethod.isBridge());
  }


  @Test
  public void testGenericType() throws NoSuchFieldException {
    User user = new User();
    List<School> schools = Collections.singletonList(new School());
    user.setSchools(schools);
    Field schoolsField = user.getClass().getDeclaredField("schools");
    Type genericType = schoolsField.getGenericType();
    Class<?> type = schoolsField.getType();
    String name = schoolsField.getName();
    AnnotatedType annotatedType = schoolsField.getAnnotatedType();
  }
}
