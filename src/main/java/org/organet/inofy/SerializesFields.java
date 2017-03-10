package org.organet.inofy;

import org.organet.inofy.Tuple.Tuple;

import java.io.Serializable;
import java.util.List;

interface SerializesFields extends Serializable {
  //List<String> getFieldNames();
  // TODO Create 'IncludeSerialization' annotation class (with custom field name argument - for serialization)
  // TODO the method below MUST ONLY return annotated fields
  //List<Tuple> getFields();// TODO the method below MUST return all fields
  //List<Tuple> getAllFields(); // field name + field type as class name string (e.g. "String", "Double" etc.)

  List<Tuple> serializeFields();
  String serializeFields(String fieldsSeparator, String valueSeparator);
}
