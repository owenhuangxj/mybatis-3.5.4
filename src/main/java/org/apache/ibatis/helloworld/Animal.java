package org.apache.ibatis.helloworld;

public interface Animal<T>{
  void eat(T t);
}
