package org.apache.ibatis.helloworld.mapper;


import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.helloworld.entity.User;

import java.util.Collection;
import java.util.List;

public interface UserMapper {
  User getOneById(@Param("id") Long id);

  List<User> getUsers(User user);

  List<User> getUsersByIds(@Param("ids") Collection<Long> ids);

  List<User> getUsersByNameAndAge(@Param("name") String name, @Param("age") int age);

}
