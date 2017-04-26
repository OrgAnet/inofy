package org.organet.inofy;

public interface Deserializer<V> {
  V deserialize(String serializedFields);
}
