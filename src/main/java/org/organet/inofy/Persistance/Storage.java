package org.organet.inofy.Persistance;

import org.organet.inofy.Deserializer;
import org.organet.inofy.Serializer;

interface Storage<T> {
  boolean initialize(Serializer serializer, Deserializer<T> deserializer);

  // TODO Make this method so that is returns the 'id' of the record
  void insert(T value);

  T get(Object id);
  T getBy(Object fieldName, Object fieldValue);
  //T getBy(List<String> fieldName, List<String> fieldValue);

  void update(Object id, T value);

  T delete(Object id);
  //T delete(List<String> id);

  boolean exists(Object id);

  void close();
}
