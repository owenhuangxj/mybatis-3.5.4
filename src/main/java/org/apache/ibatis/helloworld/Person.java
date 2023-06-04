package org.apache.ibatis.helloworld;

public class Person implements Animal<String>{
  @Override
  public void eat(String food) {
    System.out.println("Person eat " + food);
  }
}
