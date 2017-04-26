package org.organet.inofy;

import org.organet.inofy.Tuple.Tuple;

import java.util.List;

public interface Serializer<V extends Serializable> {
  List<Tuple> serializeFields(V v);
  String serializeFields(V v, String fieldsSeparator, String valueSeparator);
}
