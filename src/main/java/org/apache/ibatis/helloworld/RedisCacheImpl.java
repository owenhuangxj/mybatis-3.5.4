package org.apache.ibatis.helloworld;

import org.apache.ibatis.cache.Cache;

public class RedisCacheImpl implements Cache {
  @Override
  public String getId() {
    return null;
  }

  @Override
  public void putObject(Object key, Object value) {
  }

  @Override
  public Object getObject(Object key) {
    return null;
  }

  @Override
  public Object removeObject(Object key) {
    return null;
  }

  @Override
  public void clear() {

  }

  @Override
  public int getSize() {
    return 0;
  }
}
