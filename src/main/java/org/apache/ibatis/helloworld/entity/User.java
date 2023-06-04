package org.apache.ibatis.helloworld.entity;

import java.io.Serializable;
import java.util.List;

public class User<T> implements Serializable {

  private long id;
  private int age;
  private String name;

  // ParameterizedType类型
  private List<School> schools;

  // GenericArrayType类型
  public List<String>[] hobbies;

  public List<T> ts;


  public User() {
  }

  public User(String name) {
    this.name = name;
  }

  public User(int age, String name) {
    this.age = age;
    this.name = name;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<School> getSchools() {
    return schools;
  }

  public void setSchools(List<School> schools) {
    this.schools = schools;
  }

  @Override
  public String toString() {
    return "User{" +
      "id=" + id +
      ", age=" + age +
      ", name='" + name + '\'' +
      '}';
  }
}
